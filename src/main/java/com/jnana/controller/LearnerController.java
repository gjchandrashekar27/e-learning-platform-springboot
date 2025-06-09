package com.jnana.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jnana.service.LearnerService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/learner")
public class LearnerController {
	
	@Autowired
	LearnerService learnerService;
	
	@GetMapping("/home")
	public String loadLearnerHome(HttpSession session) {
		return learnerService.loadLearnerHome(session);
	}
	
	@GetMapping("/view-courses")
	public String loadCourses(HttpSession session, Model model) {
		return learnerService.viewCourses(session, model);
	}


}
