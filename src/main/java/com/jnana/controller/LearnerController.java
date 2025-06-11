package com.jnana.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import com.jnana.service.LearnerService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/learner")
public class LearnerController {


    @Autowired
    private LearnerService learnerService;
    
 
    @GetMapping("/home")
    public String loadLearnerHome(HttpSession session) {
        return learnerService.loadLearnerHome(session);
    }

    
    @GetMapping("/available-courses")
    public String showAvailableCourses(Model model, HttpSession session) {
        return learnerService.availableCourses(model,session);
    }
    
    @GetMapping("/enroll/{id}")
	public String enrollCourse(HttpSession session, @PathVariable Long id,Model model) {
		return learnerService.enrollCourse(session, id,model);
	}
    
    @GetMapping("/enrolled-courses")
	public String viewEnrolledCourses(HttpSession session, Model model) {
		return learnerService.viewEnrolledCourses(session, model);
	}

	@GetMapping("/view-enrolled-sections/{id}")
	public String viewEnrolledSections(HttpSession session, @PathVariable Long id, Model model) {
		return learnerService.viewEnrolledSections(session, id, model);
	}
}
