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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.jnana.dto.AccountType;
import com.jnana.dto.UserDto;
import com.jnana.entity.Learner;
import com.jnana.entity.Tutor;
import com.jnana.repository.LearnerRepository;
import com.jnana.repository.TutorRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
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
	
	@Value("${spring.mail.username}") //it will give the original gmail.
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
			session.setMaxInactiveInterval(60);
			session.setAttribute("otp", otp);
			session.setAttribute("userDto", userDto);
			sendEmail(otp, userDto);
			session.setAttribute("pass", "Otp Sent Success");
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
				System.err.println("Failed to Send OTP : " + otp);
			}
	
		
	}

	public String submitOtp(int otp, HttpSession session) {
		
		try {
			int sessionOtp = (int) session.getAttribute("otp");
			UserDto userDto = (UserDto) session.getAttribute("userDto");
			if (sessionOtp == otp) {
				if (userDto.getType() == AccountType.TUTOR) {
					Tutor tutor = new Tutor();
					tutor.setEmail(userDto.getEmail());
					tutor.setMobile(userDto.getMobile());
					tutor.setName(userDto.getName());
					tutor.setPassword(passwordEncoder.encode(userDto.getPassword()));

					tutorRepository.save(tutor);
				} else {
					Learner learner = new Learner();
					learner.setEmail(userDto.getEmail());
					learner.setMobile(userDto.getMobile());
					learner.setName(userDto.getName());
					learner.setPassword(passwordEncoder.encode(userDto.getPassword()));

					learnerRepository.save(learner);
				}
				session.setAttribute("pass", "Account Created Success");
				return "redirect:/";
		}else {
			
			session.setAttribute("fail", "Invalid Otp Try Again");
			return "redirect:/otp";
		}
		
		} catch (NullPointerException e) {
			session.setAttribute("fail", "Otp Expired, Try Again");
			return "redirect:/register";
	}
	}

	
	
	 public void removeMessage() {
		 
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = attributes.getRequest();
		HttpSession session = request.getSession(true);

		session.removeAttribute("pass");
		session.removeAttribute("fail");
	}

	public String resendOtp(HttpSession session) {
		 UserDto userDto = (UserDto) session.getAttribute("userDto");
		    if (userDto == null) {
		        session.setAttribute("fail", "Session expired. Please register again.");
		        return "redirect:/register";
		    }

		    int otp = new Random().nextInt(100000, 1000000);
		    session.setAttribute("otp", otp);
		    session.setMaxInactiveInterval(300); // Increase to 5 minutes

		    sendEmail(otp, userDto); // your implementation

		    session.setAttribute("pass", "OTP re-sent successfully.");
		    return "redirect:/otp";
	}

	public String login(String email, String password, HttpSession session,String captchaInput) {
		
		 String sessionCaptcha = (String) session.getAttribute("captcha");

		    if (sessionCaptcha == null || !captchaInput.equalsIgnoreCase(sessionCaptcha)) {
		        session.setAttribute("fail", "Invalid CAPTCHA");
		        return "redirect:/login";
		    }
		
		Learner learner = learnerRepository.findByEmail(email);
		Tutor tutor = tutorRepository.findByEmail(email);

		if (learner == null && tutor == null) {
			session.setAttribute("fail", "Invalid Email");
			return "redirect:/login";
		} else {
			if (tutor != null) {
				if (passwordEncoder.matches(password, tutor.getPassword())) {
					session.setAttribute("pass", "Login Success as Tutor");
					session.setAttribute("tutor", tutor);
					return "redirect:/tutor/home";
				} else {
					session.setAttribute("fail", "Invalid Password");
					return "redirect:/login";
				}
			} else {
				
				if (passwordEncoder.matches(password, learner.getPassword())) {
					session.setAttribute("pass", "Login Success as Learner");
					session.setAttribute("learner", learner);
					return "redirect:/learner/home";
				} else {
					session.setAttribute("fail", "Invalid Password");
					return "redirect:/login";
				}
			}
		}
	
	}

	

	public String forgetPassword(@RequestParam("email") String email, HttpSession session) {
	    Tutor tutor = tutorRepository.findByEmail(email);
	    Learner learner = learnerRepository.findByEmail(email);

	    if (tutor != null || learner != null) {
	        int otp = new Random().nextInt(100000, 1000000);
	        session.setMaxInactiveInterval(300); 
	        session.setAttribute("otp", otp);
	        session.setAttribute("email", email);

	        if (tutor != null) {
	            session.setAttribute("userType", AccountType.TUTOR); 
	            sendForgetOtpEmail(otp, tutor.getEmail(), tutor.getName(), AccountType.TUTOR.name());
	        } else {
	            session.setAttribute("userType", AccountType.LEARNER);
	            sendForgetOtpEmail(otp, learner.getEmail(), learner.getName(), AccountType.LEARNER.name());
	        }

	        session.setAttribute("pass", "OTP sent to your email.");
	        return "forget-otp.html"; 
	    } else {
	        session.setAttribute("fail", "Email not registered.");
	        return "forgot-password.html"; 
	    }
	}

	private void sendForgetOtpEmail(int otp, String toEmail, String name, String role) {
	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);

	    try {
	        helper.setTo(toEmail);
	        helper.setFrom(email, "Elearning Platform");
	        helper.setSubject("Reset Password OTP");

	        Context context = new Context();
	        context.setVariable("otp", otp);
	        context.setVariable("name", name);
	        context.setVariable("role", role);

	        String body = templateEngine.process("email-template.html", context);
	        helper.setText(body, true);

	        mailSender.send(message);
	    } catch (Exception e) {
	        System.err.println("Failed to Send OTP: " + otp);
	        e.printStackTrace();
	    }
	}

	public String submitForgetOtp(int otp, String newPassword, String confirmPassword, HttpSession session) {
	    try {
	        Integer sessionOtp = (Integer) session.getAttribute("otp");
	        String email = (String) session.getAttribute("email");

	        if (sessionOtp == null || email == null) {
	            session.setAttribute("fail", "Session expired. Please try again.");
	            return "redirect:/forgot-password";
	        }

	        if (sessionOtp != otp) {
	            session.setAttribute("fail", "Invalid OTP.");
	            return "redirect:/forget-otp";
	        }

	        if (!newPassword.equals(confirmPassword)) {
	            session.setAttribute("fail", "Passwords do not match.");
	            return "redirect:/forget-otp";
	        }

	        // Try to find Tutor by email
	        Tutor tutor = tutorRepository.findByEmail(email);
	        if (tutor != null) {
	            tutor.setPassword(passwordEncoder.encode(newPassword));
	            tutorRepository.save(tutor);
	        } else {
	            // If Tutor not found, try Learner
	            Learner learner = learnerRepository.findByEmail(email);
	            if (learner != null) {
	                learner.setPassword(passwordEncoder.encode(newPassword));
	                learnerRepository.save(learner);
	            } else {
	                // No user found with email, show error
	                session.setAttribute("fail", "User not found.");
	                return "redirect:/forgot-password";
	            }
	        }

	        // Clean up session attributes
	        session.removeAttribute("otp");
	        session.removeAttribute("email");

	        session.setAttribute("pass", "Password reset successful. Please login.");
	        return "redirect:/login";

	    } catch (Exception e) {
	        session.setAttribute("fail", "Something went wrong. Try again.");
	        return "redirect:/forget-otp";
	    }
	}

	public String logout(HttpSession session) {
		session.removeAttribute("learner");
		session.removeAttribute("tutor");
		session.setAttribute("fail", "Logout Success");
		return "redirect:/";
	}
	
	// Captcha Method 
	public class CaptchaUtil {
	    public static String generateCaptcha(int length) {
	        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	        StringBuilder captcha = new StringBuilder();
	        Random random = new Random();

	        for (int i = 0; i < length; i++) {
	            captcha.append(chars.charAt(random.nextInt(chars.length())));
	        }
	        return captcha.toString();
	    }

	}
}
