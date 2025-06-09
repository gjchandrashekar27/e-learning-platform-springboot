package com.jnana.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CourseDto {
	
	@Size(min = 5,max = 30,message = "* Title Should Be 5~30 Characters")
	private String title;
	
	@Size(min = 10,max = 100,message = "* Description Should Be 10~100 Characters")
	private String description;
	
	@NotNull(message = "* Select Any One")
	private Boolean paid;
	
	private boolean published;
	
	@Size(min = 5, max = 500, message = "* Questions Should be 5~500 charecters")
	private String questions;



}
