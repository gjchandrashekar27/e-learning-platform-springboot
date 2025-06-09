package com.jnana.entity;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "section_id") // This name matches what Section expects
    private Section section;
}
