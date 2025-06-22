package com.jnana.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.jnana.dto.CourseDto;
import com.jnana.dto.SectionDto;
import com.jnana.entity.Course;
import com.jnana.entity.Learner;
import com.jnana.entity.QuizQuestion;
import com.jnana.entity.Section;
import com.jnana.entity.Tutor;
import com.jnana.helper.CloudinaryService;
import com.jnana.repository.CourseRepository;
import com.jnana.repository.LearnerRepository;
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
    
    @Autowired
    LearnerRepository learnerRepository;
    

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
        if (session.getAttribute("tutor") == null) {
            session.setAttribute("fail", "Invalid Session. Login First");
            return "redirect:/login";
        }

        List<Map<String, Object>> learnerCourseList = new ArrayList<>();

        List<Learner> learners = learnerRepository.findAll();

        for (Learner learner : learners) {
            Map<String, Object> learnerData = new HashMap<>();
            learnerData.put("name", learner.getName());
            learnerData.put("email", learner.getEmail());

            List<String> courseTitles = learner.getEnrolledCourses()
                    .stream()
                    .map(ec -> ec.getCourse().getTitle())
                    .collect(Collectors.toList());

            learnerData.put("courses", courseTitles);
            learnerCourseList.add(learnerData);
        }

        session.setAttribute("learnerCourseList", learnerCourseList);
        return "manage-learners.html"; // ✅ Your actual HTML file
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

    public String addCourse(HttpSession session, @Valid CourseDto courseDto, BindingResult result) {
		if (session.getAttribute("tutor") != null) {
			if (result.hasErrors())
				return "add-course.html";
			else {
				Course course = new Course();
				course.setTitle(courseDto.getTitle());
				course.setPaid(courseDto.isPaid());
				course.setDescription(courseDto.getDescription());
				course.setTutor((Tutor) session.getAttribute("tutor"));

				List<QuizQuestion> questions = Arrays.stream(courseDto.getQuestions().split("\\?"))
						.map(x -> new QuizQuestion(x)).collect(Collectors.toList());
				course.setQuizQuestions(questions);

				courseRepository.save(course);
				session.setAttribute("pass", "Course Added Success");
				return "redirect:/tutor/courses";
			}
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
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
    
    public String publishCourse(Long courseId, HttpSession session, RedirectAttributes redirectAttributes) {
		Tutor tutor = (Tutor) session.getAttribute("tutor");
	    if (tutor == null) {
	        redirectAttributes.addFlashAttribute("fail", "Please login as a tutor to continue.");
	        return "redirect:/login";
	    }

	    Optional<Course> optionalCourse = courseRepository.findById(courseId);

	    if (optionalCourse.isEmpty() || !optionalCourse.get().getTutor().getId().equals(tutor.getId())) {
	        redirectAttributes.addFlashAttribute("fail", "Course not found or access denied.");
	        return "redirect:/tutor/courses";
	    }

	    Course course = optionalCourse.get();

	    if (course.getSections() == null || course.getSections().isEmpty()) {
	        redirectAttributes.addFlashAttribute("fail", "Cannot publish. Please add at least one section to the course.");
	        return "redirect:/tutor/courses";
	    }

	    if (Boolean.TRUE.equals(course.getPublished())) {
	        redirectAttributes.addFlashAttribute("fail", "Course is already published.");
	        return "redirect:/tutor/courses";
	    }

	    course.setPublished(true);
	    courseRepository.save(course);

	    redirectAttributes.addFlashAttribute("pass", "Course published successfully!");
	    return "redirect:/tutor/courses";
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
        } else {
            session.setAttribute("fail", "Invalid Session, Login First");
            return "redirect:/login";
        }
    }

    public String addSection(@Valid SectionDto sectionDto, BindingResult result, Model model, HttpSession session) {
		if (session.getAttribute("tutor") != null) {
			if (result.hasErrors()) {
				List<Course> courses = courseRepository.findByTutor((Tutor) session.getAttribute("tutor"));
				model.addAttribute("courses", courses);
				return "add-section.html";
			} else {
				Tutor tutor = (Tutor) session.getAttribute("tutor");
				Course course = courseRepository.findById(sectionDto.getCourseId()).orElseThrow();
				Section section = new Section();
				section.setCourse(course);
				section.setTitle(sectionDto.getTitle());
				section.setNotesUrl(saveNotes(sectionDto.getNotes(), tutor.getName(), section.getTitle()));
				section.setVideoUrl(saveVideo(sectionDto.getVideo(), tutor.getName(), section.getTitle()));

				List<QuizQuestion> questions = Arrays.stream(sectionDto.getQuestions().split("\\?"))
				    .map(x -> {
				        QuizQuestion q = new QuizQuestion(x.trim());
				        q.setSection(section); // <-- Set the section!
				        return q;
				    })
				    .collect(Collectors.toList());
				section.setQuizQuestions(questions);

				sectionRepository.save(section);
				session.setAttribute("pass", "Section Added Success");
				return "redirect:/tutor/sections";
			}
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}


    private String saveVideo(MultipartFile video, String string, String string2) {
        return cloudinaryService.uploadVideo(video);
    }

    private String saveNotes(MultipartFile notes, String string, String string2) {
        return cloudinaryService.uploadNotes(notes);
    }

    public String viewSections(HttpSession session, Model model) {
        if (session.getAttribute("tutor") != null) {
            List<Course> courses = courseRepository.findByTutor((Tutor) session.getAttribute("tutor"));
            List<Section> sections = sectionRepository.findByCourseIn(courses);

            if (sections.isEmpty()) {
                session.setAttribute("fail", "No Sections Added Yet");
                return "redirect:/tutor/sections";
            } else {
                model.addAttribute("sections", sections);
                return "view-sections"; // <-- No .html
            }
        } else {
            session.setAttribute("fail", "Invalid Session, Login First");
            return "redirect:/login";
        }
    }

	

	
}