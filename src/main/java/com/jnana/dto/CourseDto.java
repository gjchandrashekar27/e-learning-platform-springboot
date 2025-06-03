package com.jnana.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseDto {
	
	@Size(min = 5,max = 30,message = "* Title Should Be 5~30 Characters")
	private String title;
	
	@Size(min = 10,max = 100,message = "* Description Should Be 10~100 Characters")
	private String description;
	
	@NotNull(message = "* Select Any One")
	private Boolean paid;


}
