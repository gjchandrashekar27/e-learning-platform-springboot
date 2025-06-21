package com.jnana.entity;

import java.time.LocalDate;


import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class Certificate {
	
	@Id
	@GeneratedValue(generator = "cert_id")
	@SequenceGenerator(name = "cert_id", initialValue = 1001001, allocationSize = 1)
	private Long id;
	
	@ManyToOne //Many Learner can have one Certificate.
	Learner learner;
	
	@ManyToOne //Many Courses has One Certificate.
	Course course;
	
	@CreationTimestamp
	private LocalDate issueDate;
	
	
	

}