package com.jnana.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    // Constructor to set question only
    public QuizQuestion(String question) {
        this.question = question;
    }

    // ✅ Constructor to set both question and section
    public QuizQuestion(String question, Section section) {
        this.question = question;
        this.section = section;
    }
}