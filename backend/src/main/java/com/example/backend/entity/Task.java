package com.example.backend.entity;

import com.example.backend.enums.TaskPriority;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "columnId", nullable = false)
    private BoardColumn column;

    @Min(0)
    @Column(name = "position", updatable = true, nullable = false)
    private int position;

    @NotBlank
    @Column(name = "title", updatable = true, nullable = false)
    private String title;

    @Column(name = "description", updatable = true, nullable = false)
    @NotBlank
    private String description;

    @NotNull
    @Column(name = "dueDate", updatable = true, nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @ElementCollection
    @CollectionTable(
            name = "task_categories",
            joinColumns = @JoinColumn(name = "task_id")
    )
    @Column(name = "category")
    private List<String> categories = new ArrayList<>();

    @NotBlank
    @Column(name = "colorCode", updatable = true, nullable = false)
    private String colorCode;
}
