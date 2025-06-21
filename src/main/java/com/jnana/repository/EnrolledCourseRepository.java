package com.jnana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jnana.entity.Course;
import com.jnana.entity.EnrolledCourse;
import com.jnana.entity.EnrolledSection;
import com.jnana.entity.Learner;

public interface EnrolledCourseRepository  extends JpaRepository<EnrolledCourse, Long>{

	boolean existsByCourseAndLearner(Course course, Learner learner);

	EnrolledCourse findByEnrolledSections(EnrolledSection section);

	List<Long> findCourseIdsByLearnerId(Long id);

}