package com.jnana.entity;

import java.util.List;

import org.hibernate.annotations.ManyToAny;
import org.springframework.web.multipart.MultipartFile;

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
public class Section {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String videoUrl;
	
	private String notesUrl;
	
	@ManyToOne
	Course course;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)	
	List<QuizQuestion> quizQuestions;

}
