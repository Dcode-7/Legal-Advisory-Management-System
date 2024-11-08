package com.inn.legal.serviceImpl;

import com.inn.legal.JWT.ClientUserDetailsService;
import com.inn.legal.JWT.JwtUtil;
import com.inn.legal.POJO.Cases;
import com.inn.legal.POJO.Client;
import com.inn.legal.POJO.Lawyer;
import com.inn.legal.POJO.User;
import com.inn.legal.constants.LegalConstants;
import com.inn.legal.dao.CasesDao;
import com.inn.legal.dao.ClientDao;
import com.inn.legal.dao.LawyerDao;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    ClientDao clientDao;

    @Autowired
    CasesDao casesDao;

    @Autowired
    LawyerDao lawyerDao;

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
                    user=getUserFromMap(requestMap);
                    if (role == User.Role.CLIENT) {
                        Client client = new Client();
                        client.setEmail(email);  // Set the email for Client
                        // Other fields like name, contactNo, etc., will be null during signup
                        client = clientDao.save(client);  // Save the new Client and generate ClientID
                        user.setClient(client);
                        userDao.save(user);
                    }else {
                        userDao.save(user); //responsible for persisting (inserting or updating) the User entity.
                    }
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
                String token = jwtUtil.generateToken(
                        clientUserDetailsService.getUserDetail().getEmail(),
                        String.valueOf(clientUserDetailsService.getUserDetail().getRole())
                );
                return new ResponseEntity<String>("{\"token\":\""+token+"\"}",HttpStatus.OK); //generates a JWT token
            }else {
                log.warn("Authentication failed for user: {}", requestMap.get("email")); //If authentication fails
            }
        } catch (Exception ex) {
            log.error("Error during login: {}", ex.getMessage());
        }
        return new ResponseEntity<>("{\"message\":\"Bad Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> updateClientProfile(Map<String, String> requestMap, String token) {
        log.info("Inside updateClientProfile for email: {}", token);

        // Extract email from token and find user
        String email = jwtUtil.extractUsername(token); // Extract email from JWT token
        User user = userDao.findByEmailIdAndRole(email, User.Role.CLIENT);  // Find user with CLIENT role

        if (user != null && user.getClient() != null) {
            // Now you have the associated client, let's update the client profile
            Client client = user.getClient();

            if (requestMap.containsKey("name")) {
                client.setName(requestMap.get("name"));
            }
            if (requestMap.containsKey("contactNo")) {
                client.setContactNo(requestMap.get("contactNo"));
            }
            if (requestMap.containsKey("occupation")) {
                client.setOccupation(requestMap.get("occupation"));
            }
            if (requestMap.containsKey("address")) {
                client.setAddress(requestMap.get("address"));
            }

            clientDao.save(client);  // Save the updated client details
            return LegalUtils.getResponseEntity("Client profile updated successfully", HttpStatus.OK);
        } else {
            return LegalUtils.getResponseEntity("Client not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<Lawyer>> getAllLawyers(String token) {
        try {
            // Extract token from the Authorization header
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Removes 'Bearer ' which is 7 characters long
            }
            // If no token is found, return 401 Unauthorized
            if (token == null || jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Unauthorized
            }


            List<Lawyer> lawyers = lawyerDao.findAll(); // Fetch all lawyers from the DB
            if (lawyers.isEmpty()) {
                return ResponseEntity.noContent().build();  // Return 204 No Content if no lawyers found
            }
            return ResponseEntity.ok(lawyers);  // Return 200 OK with the list of lawyers
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Handle error with 500 Internal Server Error
        }
    }

    @Override
    public ResponseEntity<List<Lawyer>> getLawyersBySpecialization(String requestMap,String token) {
        log.info("Filtering lawyers by specialization: {}", requestMap);

        try {
            // Extract token from the Authorization header
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Removes 'Bearer ' which is 7 characters long
            }
            // If no token is found, return 401 Unauthorized
            if (token == null || jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Unauthorized
            }


            // Validate that 'specialization' is provided in the request map
            if (requestMap!=null) {
                String specialization = requestMap.toLowerCase(); // Convert to lowercase to make it case-insensitive
                log.info("Getting lawyer with {} specialization", specialization);
                // Fetch all lawyers (Role = LAWYER) with the given specialization
                List<Lawyer> lawyers = lawyerDao.findBySpecialization(specialization);

                // If no lawyers found, return NO_CONTENT status
                if (lawyers.isEmpty()) {
                    return ResponseEntity.noContent().build();  // Return 204 No Content if no lawyers found
                }

                // Return the list of lawyers
                return ResponseEntity.ok(lawyers);  // Return 200 OK with the list of lawyers
            } else {
                // Return an empty list with BAD_REQUEST status when specialization is not provided
                return ResponseEntity.noContent().build();   // Returning empty list with a BAD_REQUEST status
            }
        }  catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Handle error with 500 Internal Server Error
        }
    }

    @Override
    public ResponseEntity<String> createCase(Map<String, String> requestMap, String token) {
        log.info("Inside raiseCase with request: {}", requestMap);

        try {
            if(validatecase(requestMap)){
                // Fetch the client using the provided clientId from the request
                String email = jwtUtil.extractUsername(token); // Extract email from JWT token
                User user = userDao.findByEmailIdAndRole(email, User.Role.CLIENT);  // Find user with CLIENT role
                Client client;
                if (user != null && user.getClient() != null) {
                    // Now you have the associated client, let's update the client profile
                    client = user.getClient();
                }else {
                    return LegalUtils.getResponseEntity("Client not found", HttpStatus.NOT_FOUND);
                }
                // Fetch the lawyer using the provided lawyerId from the request
                String lawyerIdStr = requestMap.get("lawyerId");
                if (lawyerIdStr == null || lawyerIdStr.isEmpty()) {
                    return LegalUtils.getResponseEntity("Lawyer ID is required", HttpStatus.BAD_REQUEST);
                }
                Lawyer lawyer;
                try {
                    Integer lawyerId = Integer.parseInt(lawyerIdStr); // Safely parse the lawyerId
                    Optional<Lawyer> optionalLawyer = lawyerDao.findById(lawyerId);

                    if (optionalLawyer.isEmpty()) { // Check if lawyer was not found
                        return LegalUtils.getResponseEntity("Lawyer not found", HttpStatus.NOT_FOUND);
                    }

                    lawyer = optionalLawyer.get(); // Get the Lawyer from Optional
                    // Now you can proceed with the case creation logic
                } catch (NumberFormatException e) {
                    return LegalUtils.getResponseEntity("Invalid lawyer ID format", HttpStatus.BAD_REQUEST); // Handle invalid format
                }

                // Create and populate the Case object
                Cases newCase = new Cases();
                newCase.setClient(client);  // Associate client
                newCase.setLawyer(lawyer);  // Associate lawyer
                newCase.setCaseDescription(requestMap.get("caseDescription"));

                String statusStr = requestMap.get("status");
                if (statusStr != null && !statusStr.isEmpty()) {
                    try {
                        newCase.setStatus(Cases.CaseStatus.valueOf(statusStr.toUpperCase()));  // Convert to enum
                    } catch (IllegalArgumentException e) {
                        return LegalUtils.getResponseEntity("Invalid case status provided. Allowed values are: OPEN, CLOSED", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return LegalUtils.getResponseEntity("Case status is required", HttpStatus.BAD_REQUEST);
                }

                newCase.setDateCreated(new java.util.Date());  // Current timestamp for case creation

// Save the case to the database
                casesDao.save(newCase);


                return LegalUtils.getResponseEntity("Case created successfully", HttpStatus.OK);
            }else {
                return LegalUtils.getResponseEntity(LegalConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            log.error("Error creating case: {}", e.getMessage());
            return LegalUtils.getResponseEntity("Something went wrong while creating the case.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> getUserD(String token) {
        try {
            // Extract email from the token (using JWT utils or whatever method you're using)
            String email = jwtUtil.extractUsername(token); // Extract email from JWT token

            // Find user by email and role CLIENT
            User user = userDao.findByEmailIdAndRole(email, User.Role.CLIENT);

            // If user exists and has an associated client
            if (user != null && user.getClient() != null) {
                Client client = user.getClient();
                Map<String, String> userDetails = new HashMap<>();
                userDetails.put("clientID", client.getClientID().toString());
                userDetails.put("name", client.getName());
                userDetails.put("contactNo", client.getContactNo());
                userDetails.put("occupation", client.getOccupation());
                userDetails.put("address", client.getAddress());
                userDetails.put("email", client.getEmail());

                // Return the user details wrapped in ResponseEntity
                return new ResponseEntity<>(userDetails, HttpStatus.OK);
            } else {
                // Return 404 if client is not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle any unexpected errors and return a 500 Internal Server Error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private  boolean validatecase(Map<String,String> requestMap){ //Checks if the required fields are present
        return requestMap.containsKey("caseDescription") && requestMap.containsKey("status") && requestMap.containsKey("lawyerId");
    }


}
