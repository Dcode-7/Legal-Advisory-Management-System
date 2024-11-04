package com.inn.legal.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LegalUtils {

    private LegalUtils(){

    }

    public  static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}",httpStatus);
    }
}
