package com.inn.legal.service;

import com.inn.legal.POJO.Lawyer;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<String> updateClientProfile(Map<String, String> requestMap, String token);

    ResponseEntity<List<Lawyer>> getAllLawyers(String token);

    ResponseEntity<List<Lawyer>> getLawyersBySpecialization(String requestMap,String token);

    ResponseEntity<String> createCase(Map<String, String> requestMap, String token);

    ResponseEntity<Map<String, String>> getUserD(String token);
}
