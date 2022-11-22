package com.b1.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b1.testing.entity.DbIngest;

public interface DbIngestRepository extends JpaRepository<DbIngest, Integer>{
    
}
