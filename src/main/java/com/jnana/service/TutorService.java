package com.jnana.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jnana.dto.CourseDto;
import com.jnana.entity.Course;
import com.jnana.entity.Tutor;
import com.jnana.repository.CourseRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class TutorService {
	
	@Autowired
	CourseRepository courseRepository;

	public String loadHome(HttpSession session) {
		if (session.getAttribute("tutor") != null) {
			return "tutor-home.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String loadCourses(HttpSession session) {
		if (session.getAttribute("tutor") != null) {
			return "manage-course.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String loadSections(HttpSession session) {
		if (session.getAttribute("tutor") != null) {
			return "manage-section.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
		
	}

	public String loadQuestions(HttpSession session) {
		if (session.getAttribute("tutor") != null) {
			return "manage-question.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String loadLearners(HttpSession session) {
		if (session.getAttribute("tutor") != null) {
			return "manage-learners.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String loadAddCourse(HttpSession session, Model model, CourseDto courseDto) {
		if (session.getAttribute("tutor") != null) {
			model.addAttribute("courseDto", courseDto);
			return "add-course.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
		
	}

	

	public String addCourse( HttpSession session,BindingResult result, @Valid CourseDto courseDto) {
		if(session.getAttribute("tutor") != null) {
			if(result.hasErrors())
				return "add-course.html";
			else {
				Course course = new Course();
				course.setTitle(courseDto.getTitle());
				course.setDescription(courseDto.getDescription());
				course.setPaid(courseDto.getPaid());
				course.setTutor((Tutor) session.getAttribute("tutor"));
				courseRepository.save(course);
				session.setAttribute("pass", "Course Added Success");
				return "redirect:/tutor/courses";
				
			}
			
		}else {
			session.setAttribute("fail", "Invalid Session Login First");
			return "redirect:/login";
		}
		
	}

	public String viewCourses(HttpSession session, Model model) {
		if (session.getAttribute("tutor") != null) {
			List<Course> courses = courseRepository.findByTutor((Tutor) session.getAttribute("tutor"));
			if (courses.isEmpty()) {
				session.setAttribute("fail", "No Courses Added Yet");
				return "redirect:/tutor/courses";
			} else {
				model.addAttribute("courses", courses);
				return "view-courses.html";
			}

		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}
	}

	


