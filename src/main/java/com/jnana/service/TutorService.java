package com.jnana.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jnana.dto.CourseDto;
import com.jnana.dto.QuizQuestionDTO;
import com.jnana.dto.SectionDto;
import com.jnana.entity.Course;
import com.jnana.entity.QuizQuestion;
import com.jnana.entity.Section;
import com.jnana.entity.Tutor;
import com.jnana.helper.CloudinaryService;
import com.jnana.repository.CourseRepository;
import com.jnana.repository.QuizQuestionRepository;
import com.jnana.repository.SectionRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class TutorService {
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	QuizQuestionRepository quizQuestionRepository;
	
	 private CloudinaryService cloudinaryService = null;
	 
	 @Autowired
	    public TutorService(CloudinaryService cloudinaryService) {
	        this.cloudinaryService = cloudinaryService;
	    }

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
				
				List<QuizQuestion> questions = Arrays.stream(courseDto.getQuestions().split("\\?"))
						.map(x -> new QuizQuestion()).collect(Collectors.toList());
				course.setQuizQuestions(questions);
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

	public String publishCourse(Long id, HttpSession session) {
		if(session.getAttribute("tutor") != null) {
			Course course = courseRepository.findById(id).orElseThrow();
			
			List<Section> sections = sectionRepository.findByCourse(course);
			
			if(course.getQuizQuestions().isEmpty() || sections.isEmpty()) {
				session.setAttribute("fail", "There Should be atleast one section and Quiz To Publish");
				return "redirect:/tutor/view-courses";
			}else {
				course.setPublished(true);
				session.setAttribute("success", "Course Published Success");
				return "redirect:/tutor/courses";
			}		
			
		}else {
			session.setAttribute("fail", "Invalid session, Login again");
			return "redirect:/login";
		}
	}

	public String loadAddSection(HttpSession session, Model model, SectionDto sectionDto) {
		if (session.getAttribute("tutor") != null) {
			
			List<Course> courses = courseRepository.findByTutor((Tutor) session.getAttribute("tutor"));
			if (courses.isEmpty()) {
				session.setAttribute("fail", "First Add Course to add Section");
				return "redirect:/tutor/courses";
			} else {
				model.addAttribute("courses", courses);
				model.addAttribute("sectionDto", sectionDto);
				return "add-section.html";
			}
		
		}else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
		
		
	}

	public String addSection(@Valid SectionDto sectionDto, BindingResult result, HttpSession session) {
		if (session.getAttribute("tutor") != null) {
			if (result.hasErrors())
				return "add-section.html";
			else {
				
				Course course = courseRepository.findById(sectionDto.getCourseId()).orElseThrow();
				Section section = new Section();
				section.setCourse(course);
				section.setTitle(sectionDto.getTitle());
				section.setNotesUrl(saveNotes(sectionDto.getNotes()));
				section.setVideoUrl(saveVideo(sectionDto.getVideo()));
				
				List<QuizQuestion> questions = Arrays.stream(sectionDto.getQuestions().split("\\?"))
						.map(x -> new QuizQuestion()).collect(Collectors.toList());
				section.setQuizQuestions(questions);
				sectionRepository.save(section);
				
				session.setAttribute("pass", "Section Added Success");
				return "redirect:/tutor/sections";
			}
	}else {
		session.setAttribute("fail", "Invalid Session, Login First");
		return "redirect:/login";
	}
				
	}
	private String saveVideo(MultipartFile video) {
	    return cloudinaryService.uploadVideo(video); // correct method for video
	}

	private String saveNotes(MultipartFile notes) {
	    return cloudinaryService.uploadNotes(notes); // correct method for notes
	}

	public String loadViewSections(HttpSession session, Model model) {
	    if (session.getAttribute("tutor") != null) {
	        Tutor tutor = (Tutor) session.getAttribute("tutor");
	        List<Course> courses = courseRepository.findByTutor(tutor);
	        if (courses.isEmpty()) {
	            session.setAttribute("fail", "No courses found! Please add a course first.");
	            return "redirect:/tutor/courses";
	        }

	        Map<Long, List<Section>> courseSectionsMap = new HashMap<>();
	        for (Course course : courses) {
	            List<Section> sections = sectionRepository.findByCourseId(course.getId());
	            courseSectionsMap.put(course.getId(), sections);
	        }

	        model.addAttribute("courses", courses);
	        model.addAttribute("courseSectionsMap", courseSectionsMap);

	        return "view-sections.html";
	    }
	    session.setAttribute("fail", "Invalid Session, Login First");
	    return "redirect:/login";
	}



}

	

	

	


	


