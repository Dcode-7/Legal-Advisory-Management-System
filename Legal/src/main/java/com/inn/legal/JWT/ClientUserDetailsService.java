package com.inn.legal.JWT;

import com.inn.legal.POJO.User;
import com.inn.legal.dao.UserDao;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service //automatically detected and managed by the Spring context
@Slf4j  //allowing you to log messages easily.
public class ClientUserDetailsService implements UserDetailsService { //for validating user credentials during login.

    @Autowired
    UserDao userDao;

    @Getter
    private com.inn.legal.POJO.User userDetail;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}", username);

        // Attempt to find the user with CLIENT role
        userDetail = userDao.findByEmailIdAndRole(username, User.Role.CLIENT); // to find a user with the CLIENT role using the userDao.
        if (userDetail == null) { //If no user is found,
            log.info("No user found with CLIENT role for email: {}", username);
            // Check for LAWYER role
            userDetail = userDao.findByEmailIdAndRole(username, User.Role.LAWYER); //checks for a user with the LAWYER role.
        }

        // Check if userDetail is not null
        if (userDetail != null) {
            log.info("User found: {}", userDetail.getEmail());
            log.info("User status: {}", userDetail.getStatus());
            return new org.springframework.security.core.userdetails.User( //object created using Spring Security’s User class,
                    userDetail.getEmail(),
                    userDetail.getPassword(),
                    new ArrayList<>()
            );
        } else { //If no user is found,
            log.warn("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }


}
