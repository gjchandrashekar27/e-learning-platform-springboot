
package com.jnana.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jnana.dto.CourseDto;

import com.jnana.dto.SectionDto;
import com.jnana.entity.Course;
import com.jnana.entity.QuizQuestion;
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

	
	@GetMapping("/learners")
	public String loadLearners(HttpSession session) {
	    return tutorService.loadLearners(session);
	}

	
	@GetMapping("/add-course")
	public String loadAddCourse(HttpSession session, Model model,CourseDto courseDto) {
		return tutorService.loadAddCourse(session,model,courseDto);
	}
	
	@PostMapping("/add-course")
	public String addCourse(@ModelAttribute @Valid CourseDto courseDto, BindingResult result, HttpSession session) {
		return tutorService.addCourse(session, courseDto, result);
	}
	
	@GetMapping("/publish/{id}")
	public String publishCourse(@PathVariable("id") Long courseId, HttpSession session, RedirectAttributes redirectAttributes) {
	    return tutorService.publishCourse(courseId, session,redirectAttributes);
	}
	
	@GetMapping("/view-courses")
	public String viewCourses(HttpSession session, Model model) {
		return tutorService.viewCourses(session, model);
	}

	
	@GetMapping("/add-section")
	public String loadAddSection(HttpSession session, Model model, SectionDto sectionDto) {
		return tutorService.loadAddSection(session,model,sectionDto);
	}
	
	@PostMapping("/add-section")
	public String addSection(@ModelAttribute @Valid SectionDto sectionDto, BindingResult result, Model model,
			HttpSession session) {
		return tutorService.addSection(sectionDto, result, model, session);
	}
	@GetMapping("/view-sections")
	public String loadViewSections(HttpSession session, Model model) {
	    return tutorService.viewSections(session, model);
	}
	
	
	
	

	
	
	
	
	
}