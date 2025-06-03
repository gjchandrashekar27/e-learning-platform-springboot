package com.jnana.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
	
	@Size(min = 3, max = 30, message = "* Enter proper Name")
	private String name;
	
	@NotEmpty(message = "* It is Required")
	@Email(message = "* Enter Proper Email")
	private String email;
	
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
	         message = "* Password should contain at least one uppercase, lowercase, special character, and number and be minimum 8 characters")
	private String password;
	
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
	         message = "* Password should contain at least one uppercase, lowercase, special character, and number and be minimum 8 characters")
	private String confirmPassword;
	
	@DecimalMin(value = "6000000000", message = "* Enter Proper Number")
	@DecimalMax(value = "9999999999", message = "* Enter Proper Number")
	private Long mobile;
	
	@NotNull(message = "* It is required")
	private AccountType paid;

	@NotEmpty(message = "* CAPTCHA is required")
	private String captchaInput;

}


