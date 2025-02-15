package com.inn.legal.restImpl;

import com.inn.legal.POJO.Lawyer;
import com.inn.legal.constants.LegalConstants;
import com.inn.legal.rest.UserRest;
import com.inn.legal.service.UserService;
import com.inn.legal.utils.LegalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins="*",allowedHeaders = "*")
public class UserRestImpl implements UserRest {

    @Autowired
    UserService userService;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return LegalUtils.getResponseEntity(LegalConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String>  login(@RequestBody(required = true)Map<String, String> requestMap){
        try{
            return userService.login(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return LegalUtils.getResponseEntity(LegalConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateClientProfile(Map<String, String> requestMap, String token) {
        try {
            return userService.updateClientProfile(requestMap, token); // Delegate to the UserService
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return LegalUtils.getResponseEntity(LegalConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Lawyer>> getAllLawyers(String token) {
        try {
            return userService.getAllLawyers(token);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Handle error
        }
    }

    @Override
    public ResponseEntity<List<Lawyer>> filterLawyersBySpecialization(String specialization,String token) {
        try {
            return userService.getLawyersBySpecialization(specialization,token);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Handle error
        }
    }

    @Override
    public ResponseEntity<String> createCase(Map<String, String> requestMap, String token) {
        try{
            return userService.createCase(requestMap,token);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return LegalUtils.getResponseEntity(LegalConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<Map<String, String>> getUserD(String token) {
        try {
            return userService.getUserD(token);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Handle error
        }
    }


}
