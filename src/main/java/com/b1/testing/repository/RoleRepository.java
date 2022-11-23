package com.b1.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b1.testing.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
}
