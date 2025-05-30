package com.jnana.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jnana.entity.Learner;

public interface LearnerRepository extends JpaRepository<Learner, Long>{

	boolean existsByMobile(Long mobile);

	boolean existsByEmail(String email);

	Learner findByEmail(String email);

}
