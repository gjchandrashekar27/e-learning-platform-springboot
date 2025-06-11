package com.jnana.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.jnana.entity.Course;
import com.jnana.entity.EnrolledCourse;
import com.jnana.entity.EnrolledSection;
import com.jnana.entity.Learner;
import com.jnana.entity.Section;
import com.jnana.repository.CourseRepository;
import com.jnana.repository.EnrolledCourseRepository;
import com.jnana.repository.LearnerRepository;
import com.jnana.repository.SectionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Service
public class LearnerService {

    @Autowired
    private CourseRepository courseRepository;

    @Value("${razor-pay.api.key}")
    private String key;

    @Value("${razor-pay.api.secret}")
    private String secret;
    
    @Autowired
    SectionRepository sectionRepository;
    
    @Autowired
    EnrolledCourseRepository enrolledCourseRepository;
    
    @Autowired
    LearnerRepository learnerRepository;
    
    
    
    public String loadLearnerHome(HttpSession session) {
        if (session.getAttribute("learner") != null) {
            return "learner-home.html";
        } else {
            session.setAttribute("fail", "Invalid session. Please log in first.");
            return "redirect:/login";
        }
    }

    

	
	public String availableCourses(Model model, HttpSession session) {
			if (session.getAttribute("learner") == null) {
	            session.setAttribute("fail", "Please login first.");
	            return "redirect:/login";
	        }

	        List<Course> courses = courseRepository.findByPublishedTrue(); // Only published courses
	        Map<Long, List<Section>> sectionMap = new HashMap<>();

	        for (Course course : courses) {
	            List<Section> sections = sectionRepository.findByCourse(course);
	            sectionMap.put(course.getId(), sections);
	        }

	        model.addAttribute("courses", courses);
	        model.addAttribute("sectionMap", sectionMap);
	        return "available-courses";
		}



	public String enrollCourse(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {
			Learner learner = (Learner) session.getAttribute("learner");
			Course course = courseRepository.findById(id).get();

			if (course.getPaid()) {
				double amount = 199;
				try {
					RazorpayClient client = new RazorpayClient(key, secret);

					JSONObject object = new JSONObject();
					object.put("amount", amount * 100);
					object.put("currency", "INR");

					Order order = client.orders.create(object);
					String orderId = order.get("id");

					model.addAttribute("orderId", orderId);
					model.addAttribute("amount", amount * 100);
					model.addAttribute("currency", "INR");
					model.addAttribute("leaner", learner);
					model.addAttribute("key", key);

					return "payment.html";

				} catch (RazorpayException e) {
					e.printStackTrace();
					session.setAttribute("fail", "Something Went Wrong");
					return "redirect:/learner/home";
				}

			} else {

				List<Section> sections = sectionRepository.findByCourse(course);
				List<EnrolledSection> enrolledSections = new ArrayList<EnrolledSection>();
				for (Section section : sections) {
					EnrolledSection enrolledSection = new EnrolledSection();
					enrolledSection.setSection(section);
					enrolledSections.add(enrolledSection);
				}

				EnrolledCourse enrolledCourse = new EnrolledCourse();
				enrolledCourse.setCourse(course);
				enrolledCourse.setEnrolledSections(enrolledSections);

				learner.getEnrolledCourses().add(enrolledCourse);

				learnerRepository.save(learner);

				session.setAttribute("pass", "Courses Enrolled Success, Thanks " + learner.getName());
				session.setAttribute("learner", learnerRepository.findById(learner.getId()).get());
				return "redirect:/learner/home";
			}

		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String viewEnrolledCourses(HttpSession session, Model model) {
		if (session.getAttribute("learner") != null) {
			Learner learner = (Learner) session.getAttribute("learner");

			List<EnrolledCourse> enrolledCourses = learner.getEnrolledCourses();
			if (enrolledCourses.isEmpty()) {
				session.setAttribute("fail", "Not Enrolled for Any of the Courses");
				return "redirect:/learner/home";
			} else {
				model.addAttribute("enrolledCourses", enrolledCourses);
				return "view-enrolled-courses.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String viewEnrolledSections(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {
			EnrolledCourse enrolledCourse = enrolledCourseRepository.findById(id).get();
			List<EnrolledSection> enrolledSections = enrolledCourse.getEnrolledSections();

			model.addAttribute("enrolledSections", enrolledSections);
			return "view-enrolled-sections.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}







	
}
	

