package com.example.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TodoStage stage;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    public Todo(String title, String description, TodoStage stage) {
        this.title = title;
        this.stage = stage;
        this.description = description;
    }

    public Todo(String title, String description, User user) {
        this.user = user;
        this.title = title;
        this.stage = TodoStage.NOT_STARTED;
        this.description = description;
    }
}
