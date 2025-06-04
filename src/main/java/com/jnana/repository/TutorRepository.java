package com.jnana.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jnana.entity.Tutor;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

	boolean existsByMobile(Long mobile);

	boolean existsByEmail(String email);

	Tutor findByEmail(String email);

}