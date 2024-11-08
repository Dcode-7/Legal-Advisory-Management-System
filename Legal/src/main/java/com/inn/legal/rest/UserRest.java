package com.inn.legal.rest;

import com.inn.legal.POJO.Lawyer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path= "/user")
public interface UserRest {

    @PostMapping(path="/signup")
    public ResponseEntity<String>   signUp(@RequestBody(required = true)Map<String, String> requestMap);

    @PostMapping(path="/login")
    public ResponseEntity<String>  login(@RequestBody(required = true)Map<String, String> requestMap);

    // Client update profile endpoint (added)
    @PutMapping(path="/update-profile")
    public ResponseEntity<String> updateClientProfile(@RequestBody(required = true) Map<String, String> requestMap,
                                                      @RequestHeader("Authorization") String token);

    @GetMapping(path="/search")
    public ResponseEntity<List<Lawyer>> getAllLawyers(@RequestHeader("Authorization") String token);

    @GetMapping("/filterLawyersBySpecialization")
    public ResponseEntity<List<Lawyer>> filterLawyersBySpecialization(
                                                                      @RequestParam("specialization") String specialization,
                                                                      @RequestHeader("Authorization") String token);

    @PostMapping("/raise-case")
    public ResponseEntity<String> createCase(@RequestBody(required = true) Map<String, String> requestMap,
                                             @RequestHeader("Authorization") String token);

    @GetMapping("/details")
    public ResponseEntity<Map<String,String>> getUserD(@RequestHeader("Authorization") String token);
}
