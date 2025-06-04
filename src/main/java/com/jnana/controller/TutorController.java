
package com.jnana.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jnana.dto.CourseDto;
import com.jnana.entity.Course;
import com.jnana.service.TutorService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/tutor")
public class TutorController {
	
	@Autowired
	TutorService tutorService;
	
	@GetMapping("/home")
	public String loadTutorHome(HttpSession session) {
		return tutorService.loadHome(session);
	}
	
	@GetMapping("/courses")
	public String loadCourses(HttpSession session) {
		return tutorService.loadCourses(session);
	}

	@GetMapping("/sections")
	public String loadSections(HttpSession session) {
		return tutorService.loadSections(session);
	}

	@GetMapping("/questions")
	public String loadQuestions(HttpSession session) {
		return tutorService.loadQuestions(session);
	}

	@GetMapping("/learners")
	public String loadLearners(HttpSession session) {
		return tutorService.loadLearners(session);
	}
	
//	@GetMapping("/add-course")
//	public String showAddCourseForm(Model model, HttpSession session) {
//		return tutorService.showAddCourseForm(model,session);  
//	}
//	
//	@PostMapping("/add-course")
//	public String addCourse(@ModelAttribute("course") Course course,
//	                        HttpSession session,
//	                        RedirectAttributes redirectAttributes) {
//	    return tutorService.saveCourse(course, session, redirectAttributes);
//	}
//	
//	@GetMapping("/view-courses")
//	public String viewCourses(HttpSession session, Model model) {
//	    return tutorService.viewCourses(session, model);
//	}
	
	@GetMapping("/add-course")
	public String loadAddCourse(HttpSession session, Model model,CourseDto courseDto) {
		return tutorService.loadAddCourse(session,model,courseDto);
	}
	
	@PostMapping("/add-course")
	public String addCourse(HttpSession session, @Valid CourseDto courseDto, BindingResult result) {
		return tutorService.addCourse(session,result,courseDto);
	}
	
	@GetMapping("/view-courses")
	public String viewCourses(HttpSession session, Model model) {
		return tutorService.viewCourses(session, model);
	}
	
	

}
