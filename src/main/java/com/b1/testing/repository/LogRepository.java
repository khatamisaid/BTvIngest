package com.b1.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b1.testing.entity.Log;

public interface LogRepository extends JpaRepository<Log, Long>{
    
}
