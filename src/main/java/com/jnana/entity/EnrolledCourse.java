package com.jnana.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
public class EnrolledCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Course course;

   

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<EnrolledSection> enrolledSections;

    private boolean courseCompleted;
    private boolean finalQuizCompleted;
    private LocalDate completionDate;
    
    @ManyToOne
    private Learner learner;

    public Learner getLearner() {
        return learner;
    }
}
