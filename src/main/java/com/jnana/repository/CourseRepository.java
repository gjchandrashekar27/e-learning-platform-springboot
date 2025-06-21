package com.jnana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jnana.entity.Course;
import com.jnana.entity.Tutor;

public interface CourseRepository extends JpaRepository<Course, Long> {

	List<Course> findByTutor(Tutor attribute);

	List<Course> findByPublishedTrue();

	List<Course> findByTutorAndPublishedTrue(Tutor tutor);

}