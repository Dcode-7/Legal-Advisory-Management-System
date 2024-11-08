package com.inn.legal.dao;

import com.inn.legal.POJO.Cases;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CasesDao extends JpaRepository<Cases, Integer> {
}
