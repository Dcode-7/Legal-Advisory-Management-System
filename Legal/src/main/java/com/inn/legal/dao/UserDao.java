package com.inn.legal.dao;

import com.inn.legal.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {
    User findByEmailIdAndRole(@Param("email") String email, @Param("role") User.Role role);
}
