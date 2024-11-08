package com.inn.legal.dao;

import com.inn.legal.POJO.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LawyerDao extends JpaRepository<Lawyer, Integer> {
    List<Lawyer> findAll();
    List<Lawyer> findBySpecialization(@Param("specialization") String specialization);

//    Optional <Lawyer> findById(@Param("lawyerID") Integer lawyerID);
}
