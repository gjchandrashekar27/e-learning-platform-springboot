package com.jnana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jnana.entity.Course;
import com.jnana.entity.Section;
import com.jnana.entity.Tutor;

public interface SectionRepository  extends JpaRepository<Section, Long>{

	List<Section> findByCourse(Course course);
	
	List<Section> findByCourseId(Long id);

	List<Section> findByCourseIn(List<Course> courses);

	List<Section> findByCourseTutor(Tutor tutor);


}
