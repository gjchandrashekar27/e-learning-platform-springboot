package com.jnana.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.jnana.dto.UserDto;
import com.jnana.service.GeneralService;
import com.jnana.service.GeneralService.CaptchaUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class GeneralController {
	
	@Autowired
	GeneralService generalService;
	
	@GetMapping("/")
	public String loadHome() {
		return "home.html";
	}
	
	@GetMapping("/register")
	public String loadRegister(UserDto userDto, Model model) {
		return generalService.loadRegister(userDto,model);
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute @Valid UserDto userDto, BindingResult result, HttpSession session) {
		return generalService.register(userDto,result,session);
		
	}
	
	@GetMapping("/otp")
	public String loadOtp() {
		return "otp.html";
	}
	
	@PostMapping("/submit-otp")
	public String submitOtp(@RequestParam int otp, HttpSession session) {
		return generalService.submitOtp(otp,session);
	}
	
	@GetMapping("/resend-otp")
	public String resendOtp(HttpSession session) {
		return generalService.resendOtp(session);
				
	}
	
	@GetMapping("/login")
	public String showLoginPage(HttpSession session, Model model) {
	    String captcha = CaptchaUtil.generateCaptcha(5);
	    session.setAttribute("captcha", captcha);
	    model.addAttribute("captcha", captcha);
	    return "login";
	}
	
	@PostMapping("/login")
	public String login (@RequestParam String email, @RequestParam String password,@RequestParam String captchaInput, HttpSession session) {
		return generalService.login(email,password,session,captchaInput);
	}
	
	
	
	@GetMapping("/forget-password")
	public String loadForgetPassword() {
		return "forget-password.html";
	}
	
	@PostMapping("/forget-password")
	public String forgetPassword(@RequestParam("email")String email , HttpSession session) {
		return generalService.forgetPassword(email,session);
	}
	
	@PostMapping("/submit-forget-otp")
	public String submitForgetOtp(
	    @RequestParam("otp") int otp,
	    @RequestParam("newPassword") String newPassword,
	    @RequestParam("confirmPassword") String confirmPassword,
	    HttpSession session) {
	    
	    return generalService.submitForgetOtp(otp, newPassword, confirmPassword, session);
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return generalService.logout(session);
	}

}
	