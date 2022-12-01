package com.b1.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b1.testing.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    
}
