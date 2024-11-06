package com.inn.legal.dao;

import com.inn.legal.POJO.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ClientDao extends JpaRepository<Client, Integer> {
    Client findByEmail(@Param("email") String email);
}
