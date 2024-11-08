package com.inn.legal.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtUtil {

    private String secret = "Legal@24-01DBMS";

    public String extractUsername(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Removes 'Bearer ' which is 7 characters long
        }
        String username = extractClaims(token, Claims::getSubject);
        log.info("Extracted username from token: {}", username); // Add debug log
        return username;
    }

    public Date extractExpiration(String token){ //It tells you when the token will no longer be valid.
        return extractClaims(token,Claims::getExpiration);
    }

    public <T> T extractClaims(String token, @org.jetbrains.annotations.NotNull Function<Claims,T> claimsResolver){ //This is a generic method that allows you to extract any piece of information (claims) from the token.
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token){//This method decodes the JWT token using the secret key and retrieves all claims contained in the token.
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username,String role){ //This method creates a new JWT token. It takes the username and role, adds the role to the claims, and then calls createToken to build the token.
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role);
        return createToken(claims,username);
    }
    private  String createToken(Map<String,Object> claims,String subject){ //This method actually constructs the JWT and signs it with the secret key, and then compacts it into a string.
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *60*60*5))
                .signWith(SignatureAlgorithm.HS256,secret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails){ //This checks if the token is valid and ensure the token hasn’t expired.
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }



}
