package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId", columnDefinition = "uuid", updatable = false, nullable = false)
    private Board board;

    @NotBlank
    @Column(name = "title", updatable = true, nullable = false)
    private String title;

    @Min(0)
    @Column(name = "position", updatable = true, nullable = false)
    private int position;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArrayList<Task> tasks = new ArrayList<>();
}
