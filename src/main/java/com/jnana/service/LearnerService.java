package com.jnana.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.jnana.entity.Course;
import com.jnana.entity.EnrolledCourse;
import com.jnana.entity.EnrolledSection;
import com.jnana.entity.Learner;
import com.jnana.entity.QuizQuestion;
import com.jnana.entity.Section;
import com.jnana.repository.CourseRepository;
import com.jnana.repository.EnrolledCourseRepository;
import com.jnana.repository.EnrolledSectionRepository;
import com.jnana.repository.LearnerRepository;
import com.jnana.repository.QuizQuestionRepository;
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
    
    @Autowired
    EnrolledSectionRepository enrolledSectionRepository;
    
    @Autowired
    QuizQuestionRepository quizQuestionRepository;
    
    @Autowired
	ChatClient chatClient;
    
    
    
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

        List<Course> courses = courseRepository.findByPublishedTrue();
        Map<Long, List<Section>> sectionMap = new HashMap<>();

        for (Course course : courses) {
            List<Section> sections = sectionRepository.findByCourse(course);
            sectionMap.put(course.getId(), sections);
        }

        model.addAttribute("courses", courses);
        model.addAttribute("sectionMap", sectionMap); // ✅ THIS must be added to the model
        return "available-courses";
    }


	public String enrollCourse(HttpSession session, Long id, Model model) {
	    if (session.getAttribute("learner") == null) {
	        session.setAttribute("fail", "Invalid Session, Login First");
	        return "redirect:/login";
	    }

	    Learner learner = (Learner) session.getAttribute("learner");

	    Optional<Course> courseOpt = courseRepository.findById(id);
	    if (!courseOpt.isPresent()) {
	        session.setAttribute("fail", "Course not found");
	        return "redirect:/learner/home";
	    }

	    Course course = courseOpt.get();

	    // Check if already enrolled
	    boolean alreadyEnrolled = learner.getEnrolledCourses().stream()
	        .anyMatch(enrolledCourse -> enrolledCourse.getCourse().getId().equals(course.getId()));
	    if (alreadyEnrolled) {
	        session.setAttribute("fail", "You are already enrolled in this course");
	        return "redirect:/learner/home";
	    }

	    if (course.isPaid()) {
	        double amount = 199;
	        try {
	            RazorpayClient client = new RazorpayClient(key, secret);

	            JSONObject object = new JSONObject();
	            object.put("amount", (int)(amount * 100)); // Razorpay expects integer amount in paise
	            object.put("currency", "INR");

	            Order order = client.orders.create(object);
	            String orderId = order.get("id");

	            model.addAttribute("orderId", orderId);
	            model.addAttribute("amount", (int)(amount * 100));
	            model.addAttribute("currency", "INR");
	            model.addAttribute("learner", learner);
	            model.addAttribute("key", key);
	            model.addAttribute("courseId", id); // Needed to verify and enroll after payment

	            return "payment.html";

	        } catch (RazorpayException e) {
	            e.printStackTrace();
	            session.setAttribute("fail", "Something Went Wrong While Creating Payment");
	            return "redirect:/learner/home";
	        }

	    } else {
	        // Free course - Enroll directly
	        List<Section> sections = sectionRepository.findByCourse(course);
	        List<EnrolledSection> enrolledSections = new ArrayList<>();
	        for (Section section : sections) {
	            EnrolledSection enrolledSection = new EnrolledSection();
	            enrolledSection.setSection(section);
	            enrolledSections.add(enrolledSection);
	        }

	        EnrolledCourse enrolledCourse = new EnrolledCourse();
	        enrolledCourse.setCourse(course);
	        enrolledCourse.setEnrolledSections(enrolledSections);
	        enrolledCourse.setLearner(learner); // Add this if bidirectional

	        learner.getEnrolledCourses().add(enrolledCourse);
	        learnerRepository.save(learner);

	        session.setAttribute("pass", "Course Enrolled Successfully, Thanks " + learner.getName());
	        session.setAttribute("learner", learnerRepository.findById(learner.getId()).get());

	        return "redirect:/learner/home";
	    }
	}


	public String viewEnrolledCourses(HttpSession session, Model model) {
	    if (session.getAttribute("learner") != null) {
	        Learner learner = (Learner) session.getAttribute("learner");
	        List<EnrolledCourse> enrolledCourses = learner.getEnrolledCourses();

	        if (enrolledCourses.isEmpty()) {
	            session.setAttribute("fail", "Not Enrolled for Any of the Courses");
	            return "redirect:/learner/home";
	        }

	        // 🔴 Add this logic
	        Map<Long, Boolean> certificateStatusMap = new HashMap<>();
	        for (EnrolledCourse ec : enrolledCourses) {
	            boolean allQuizCompleted = ec.getEnrolledSections().stream()
	                .allMatch(section -> section.isSectionQuizCompleted());
	            certificateStatusMap.put(ec.getCourse().getId(), allQuizCompleted);
	        }

	        model.addAttribute("enrolledCourses", enrolledCourses);
	        model.addAttribute("certificateStatusMap", certificateStatusMap); // ✅ Add to model

	        return "view-enrolled-courses.html";
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




	public String viewVideo(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {

			EnrolledSection section = (EnrolledSection) enrolledSectionRepository.findById(id).get();
			section.setSectionCompleted(true);

			enrolledSectionRepository.save(section);

			String videoUrl = section.getSection().getVideoUrl();
			model.addAttribute("link", videoUrl);
			EnrolledCourse course = enrolledCourseRepository.findByEnrolledSections(section);
			model.addAttribute("id", course.getId());
			return "play-video.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}




	public String loadSectionQuiz(Long id, HttpSession session, Model model) {
		if (session.getAttribute("learner") != null) {

			EnrolledSection section = (EnrolledSection) enrolledSectionRepository.findById(id).get();

			if (!section.isSectionCompleted()) {
				EnrolledCourse course = enrolledCourseRepository.findByEnrolledSections(section);
				session.setAttribute("fail", "First Complete the Section to Take Quiz");
				return "redirect:/learner/view-enrolled-sections/" + course.getId();
			}
			List<QuizQuestion> questions = section.getSection().getQuizQuestions();
			model.addAttribute("questions", questions);
			model.addAttribute("id", id);

			return "section-quiz.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}


	public String enrollPaidCourse(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {
			Learner learner = (Learner) session.getAttribute("learner");
			Course course = courseRepository.findById(id).get();
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

		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}




	public String submitQuiz(Long id, HttpSession session, Map<String, String> quiz) {
		if (session.getAttribute("learner") == null) {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}

		Optional<EnrolledSection> sectionOpt = enrolledSectionRepository.findById(id);
		if (sectionOpt.isEmpty()) {
			session.setAttribute("fail", "Invalid Section");
			return "redirect:/learner/home";
		}
		EnrolledSection section = sectionOpt.get();

		// Step 1: Build prompt for ChatGPT
		StringBuilder promptBuilder = new StringBuilder("Evaluate the following quiz. For each question, consider the given answer. Return ONLY the total score out of 100 (just a number).\n\n");

		for (String questionIdStr : quiz.keySet()) {
			try {
				Long questionId = Long.parseLong(questionIdStr);
				Optional<QuizQuestion> quizQuestionOpt = quizQuestionRepository.findById(questionId);
				if (quizQuestionOpt.isPresent()) {
					String question = quizQuestionOpt.get().getQuestion();
					String answer = quiz.get(questionIdStr);
					promptBuilder.append("Question: ").append(question).append("\nAnswer: ").append(answer).append("\n\n");
				}
			} catch (NumberFormatException e) {
				continue; // skip invalid IDs
			}
		}

		String prompt = promptBuilder.toString();

		// Step 2: Call ChatGPT and extract score
		int score = 0;
		try {
			String response = chatClient.prompt(prompt).call().content().trim();
			score = Integer.parseInt(response.replaceAll("[^0-9]", "")); // remove any extra text if present
		} catch (Exception e) {
			session.setAttribute("fail", "Unable to evaluate quiz. Please try again.");
			return "redirect:/learner/view-enrolled-sections/" + enrolledCourseRepository.findByEnrolledSections(section).getId();
		}

		// Step 3: Evaluate result
		if (score >= 75) {
			section.setSectionQuizCompleted(true);
			enrolledSectionRepository.save(section);
			session.setAttribute("pass", "Quiz Passed! 🎉 Score: " + score);
		} else {
			session.setAttribute("fail", "Quiz Failed. Your Score: " + score + ". Try again.");
		}

		// Step 4: Redirect back to enrolled sections page
		EnrolledCourse course = enrolledCourseRepository.findByEnrolledSections(section);
		return "redirect:/learner/view-enrolled-sections/" + course.getId();
	}
	
	// Utility method to check if quiz is completed for all sections in a course
	public boolean isCourseQuizCompleted(EnrolledCourse course) {
	    for (EnrolledSection section : course.getEnrolledSections()) {
	        if (!(section.isSectionCompleted() && section.isSectionQuizCompleted())) {
	            return false; // Any section not completed or quiz not done
	        }
	    }
	    return true;
	}


	public String generateCertificate(Long id, HttpSession session, Model model) {
		 if (session.getAttribute("learner") == null) {
		        session.setAttribute("fail", "Invalid Session, Login First");
		        return "redirect:/login";
		    }

		    Learner learner = (Learner) session.getAttribute("learner");

		    Optional<EnrolledCourse> enrolledCourseOpt = learner.getEnrolledCourses()
		        .stream()
		        .filter(c -> c.getCourse().getId().equals(id))
		        .findFirst();

		    if (enrolledCourseOpt.isEmpty()) {
		        session.setAttribute("fail", "Course Not Found");
		        return "redirect:/learner/home";
		    }

		    EnrolledCourse enrolledCourse = enrolledCourseOpt.get();

		    boolean completed = isCourseQuizCompleted(enrolledCourse);

		    if (!completed) {
		        session.setAttribute("fail", "Complete all sections and quizzes to view the certificate.");
		        return "redirect:/learner/view-enrolled-sections/" + enrolledCourse.getId();
		    }

		    // Put details in model for certificate view
		    model.addAttribute("learner", learner);
		    model.addAttribute("course", enrolledCourse.getCourse());
		    model.addAttribute("date", java.time.LocalDate.now());

		    return "certificate.html"; // Your certificate view
	}

	
	






	

	
}
	