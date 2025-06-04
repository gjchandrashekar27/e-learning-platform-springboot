package com.jnana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jnana.entity.Course;
import com.jnana.entity.Section;

public interface SectionRepository  extends JpaRepository<Section, Long>{

	List<Section> findByCourse(Course course);

}
