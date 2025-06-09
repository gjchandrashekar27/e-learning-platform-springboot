package com.jnana.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String description;
	
	private Boolean paid;
	
	private Boolean published;
	
	@ManyToOne //Many Tutor Can take Many Courses.
	Tutor tutor;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<QuizQuestion> quizQuestions = new ArrayList<QuizQuestion>();
	
	// NEW: Sections of this course
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();
	
	public List<QuizQuestion> getQuestions() {
	    return this.quizQuestions;
	}


	public void setQuestions(List<QuizQuestion> questions) {
	    this.quizQuestions = questions;
	}

}
