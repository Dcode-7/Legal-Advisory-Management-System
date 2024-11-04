package com.inn.legal.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; //extracting claims and validating tokens.

    @Autowired
    private  ClientUserDetailsService service;

    Claims claims = null; //To hold the claims extracted from the JWT.
    private  String userName = null; // To store the username extracted from the JWT.
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException { //This method is overridden from OncePerRequestFilter, ensuring that the filter is applied only once per request.
        if(httpServletRequest.getServletPath().matches("user/login|user/signup|user/forgotPassword")){ //If the request is for one of the public endpoints , it allows the request to proceed without further authentication checks using filterChain.doFilter(...).
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }else{
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;

            if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer")){ //If the request is for a protected endpoint,
                token=authorizationHeader.substring(7);//it extracts the token by removing the "Bearer " prefix.
                userName=jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
            }
            if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails=service.loadUserByUsername(userName); //It loads user details
                if(jwtUtil.validateToken(token,userDetails)){ //validates the token against the user details.
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(httpServletRequest,httpServletResponse); //allows the request to proceed
        }
    }
//    public boolean isAdmin(){
//        return "admin".equalsIgnoreCase((String) claims.get("role"));
//    }
//    public boolean isUser(){
//        return "user".equalsIgnoreCase((String) claims.get("role"));
//    }
//
//    public String getCurrentUser(){
//        return userName;
//    }
}
