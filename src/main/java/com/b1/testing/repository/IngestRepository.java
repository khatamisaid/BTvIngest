package com.b1.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b1.testing.entity.Ingest;

public interface IngestRepository extends JpaRepository<Ingest, Integer>{
    
}
