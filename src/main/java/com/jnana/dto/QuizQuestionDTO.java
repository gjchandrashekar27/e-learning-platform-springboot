package com.jnana.dto;

import lombok.Data;

@Data
public class QuizQuestionDTO {

    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    private Long sectionId;  // Needed to link the question to a section
    private Long courseId;   // Optional - if needed for validation or course-wide quizzes
}
