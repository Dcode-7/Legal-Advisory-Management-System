package com.inn.legal.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.inn.legal.JWT.ClientUserDetailsService;
import com.inn.legal.JWT.JwtUtil;
import com.inn.legal.POJO.User;
import com.inn.legal.constants.LegalConstants;
import com.inn.legal.dao.UserDao;
import com.inn.legal.service.UserService;
import com.inn.legal.utils.LegalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ClientUserDetailsService clientUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap){ //Handles user registration.
        log.info("Inside signup {}",requestMap);
        try{
            if(validateSignUpMap(requestMap)){ //checks if it contains email, password, and role
                String email = requestMap.get("email");
                User.Role role = User.Role.valueOf(requestMap.get("role").toUpperCase()); // Convert to enum

                // Call the method with both email and role
                User user = userDao.findByEmailIdAndRole(email, role); //checks if a user already exists
                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return  LegalUtils.getResponseEntity("Successfully Registered !",HttpStatus.OK);
                }else{
                    return LegalUtils.getResponseEntity("Email already exists.",HttpStatus.BAD_REQUEST);
                }
            }else{
                return LegalUtils.getResponseEntity(LegalConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return LegalUtils.getResponseEntity(LegalConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private  boolean validateSignUpMap(Map<String,String> requestMap){ //Checks if the required fields are present
        return requestMap.containsKey("email") && requestMap.containsKey("password") && requestMap.containsKey("role");
    }
    private  User getUserFromMap(Map<String,String> requestMap){ //Converts the incoming data map into a User object.
        User user=new User();
        user.setEmail(requestMap.get("email"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setStatus("true");
        String roleString = requestMap.get("role");
        if (roleString != null && !roleString.isEmpty()) {
            user.setRole(User.Role.valueOf(roleString.toUpperCase())); //converting string to the corresponding enum value.
        }
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) { //Handles user login
        log.info("Inside Login ");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            ); //to authenticate the user with the provided email and password.
            log.info("Authentication successful for user: {}", requestMap.get("email"));
            if (auth.isAuthenticated()) {
                if(clientUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){ //Checks if the user’s status is active (approved).
                   return new ResponseEntity<String>("{\"token\":\""+jwtUtil.generateToken(clientUserDetailsService.getUserDetail()
                                   .getEmail(),
                           String.valueOf(clientUserDetailsService.getUserDetail().getRole()))+"\"}",HttpStatus.OK); //generates a JWT token
               }else {
                    return new ResponseEntity<>("{\"message\":\"Wait for Admin Approval\"}", HttpStatus.BAD_REQUEST); //admin approval is needed.
                }
            }else {
                log.warn("Authentication failed for user: {}", requestMap.get("email")); //If authentication fails
            }
        } catch (Exception ex) {
            log.error("Error during login: {}", ex.getMessage());
        }
        return new ResponseEntity<>("{\"message\":\"Bad Credentials\"}", HttpStatus.BAD_REQUEST);
    }



}
