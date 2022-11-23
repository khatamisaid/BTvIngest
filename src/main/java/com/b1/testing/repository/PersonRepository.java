package com.b1.testing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b1.testing.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    List<Person> findByUsername(String username);
}
