package com.jnana.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.jnana.dto.AccountType;
import com.jnana.dto.UserDto;
import com.jnana.entity.Learner;
import com.jnana.entity.Tutor;
import com.jnana.repository.LearnerRepository;
import com.jnana.repository.TutorRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class GeneralService {
	
	@Autowired
	LearnerRepository learnerRepository;
	
	@Autowired
	TutorRepository tutorRepository;
	
	@Autowired // For Encrypt The Password In DataBase.
	PasswordEncoder passwordEncoder;
	
	@Autowired // For Sending Mail Purpose.
	JavaMailSender mailSender;

	
	@Autowired
	TemplateEngine templateEngine; // Used To write Value in HTml Page using Html.
	
	@Value("${spring.mail.username}")
	private String email;

	public String loadRegister(UserDto userDto, Model model) {
		model.addAttribute("userDto", userDto);
		return "register.html";
	}

	public String register(@Valid UserDto userDto, BindingResult result, HttpSession session) {
		if (!userDto.getConfirmPassword().equals(userDto.getPassword()))
			result.rejectValue("confirmPassword", "error.confirmPassword",
					"* Password and Confirm Password not matching");

		if (learnerRepository.existsByMobile(userDto.getMobile())
				|| tutorRepository.existsByMobile(userDto.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Number Already in Use");

		if (learnerRepository.existsByEmail(userDto.getEmail()) || tutorRepository.existsByEmail(userDto.getEmail()))
			result.rejectValue("email", "error.email", "* Email Adress Already in Use");

		if (!result.hasErrors()) {
			int otp = new Random().nextInt(100000, 1000000);
			session.setAttribute("otp", otp);
			session.setAttribute("userDto", userDto);
			sendEmail(otp, userDto);
			return "otp.html";
		}
		return "register.html";
	}

	
	// This Method Is Send Mail Not Written in controller.
	private void sendEmail(int otp,  UserDto userDto) {
		 MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			try {
				helper.setTo(userDto.getEmail());
				helper.setFrom(email, "Elearning Platform");
				helper.setSubject("Verify OTP");

				Context context = new Context();
				context.setVariable("otp", otp);
				context.setVariable("name", userDto.getName());
				context.setVariable("role", userDto.getType().name());

				String body = templateEngine.process("email-template.html", context);

				helper.setText(body, true);

				mailSender.send(message);
			} catch (Exception e) {
				System.out.println("Failed to Send OTP : " + otp);
			}
	
		
	}

	public String submitOtp(int otp, HttpSession session) {
		int sessionOtp = (int) session.getAttribute("otp");
		UserDto userDto = (UserDto) session.getAttribute("userDto");
		
		if(sessionOtp == otp) {
			
			if(userDto.getType() == AccountType.TUTOR) {
				
				Tutor tutor = new Tutor();
				tutor.setEmail(userDto.getEmail());
				tutor.setMobile(userDto.getMobile());
				tutor.setName(userDto.getName());
				tutor.setPassword(passwordEncoder.encode(userDto.getPassword()));

				
				tutorRepository.save(tutor);
				
			}else {
				
				Learner learner = new Learner ();
				learner.setEmail(userDto.getEmail());
				learner.setMobile(userDto.getMobile());
				learner.setName(userDto.getName());
				learner.setPassword(passwordEncoder.encode(userDto.getPassword()));
				
				learnerRepository.save(learner);
			}
			
			return "redirect:/";
			
		}else {
			return "redirect:/otp";
		}
	}

}
