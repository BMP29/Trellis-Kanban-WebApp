package com.example.backend.service;

import com.example.backend.dto.TaskUpdateDto;
import com.example.backend.dto.TaskUpdateResponseDto;
import com.example.backend.entity.Task;
import com.example.backend.exception.TaskNotFoundException;
import com.example.backend.repository.TaskRepository;
import com.example.backend.service.Impl.TaskServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TaskServiceImplTest {

    private TaskServiceImpl taskService;
    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImplTest(TaskServiceImpl taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @Test
    @Transactional
    public void deleteTaskTest_keepsTasksPositionsStrictlyIncreasing_whenDeletingTask() {
        UUID taskId = UUID.fromString("03f9417c-b739-4507-8aed-830a2415d337");

        List<Task> tasks  = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId))
                .getColumn().getTasks();

        taskService.deleteTask(taskId);

        tasks.sort(Comparator.comparingInt(Task::getPosition));

        boolean arePositionsStrictlyIncreasing  = IntStream.range(0, tasks.size()-1)
                .allMatch(i -> tasks.get(i).getPosition() < tasks.get(i+1).getPosition());

        assertEquals(true, arePositionsStrictlyIncreasing);
    }

    @Test
    @Transactional
    public void updateTaskTest_keepsTasksPositionsStrictlyIncreasing_whenUpdatingTaskPosition() {
        UUID taskId = UUID.fromString("18bbe496-194a-4ec3-899f-5f2a31e4f101");

        TaskUpdateDto data = new TaskUpdateDto(
                taskId,
                null,
                2,
                null,
                null,
                null,
                null,
                null,
                null
        );

        TaskUpdateResponseDto response = taskService.updateTask(data);

        List<Task> tasks = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId))
                .getColumn().getTasks();

        tasks.sort(Comparator.comparingInt(Task::getPosition));

        boolean arePositionsStrictlyIncreasing  = IntStream.range(0, tasks.size()-1)
                .allMatch(i -> tasks.get(i).getPosition() < tasks.get(i+1).getPosition());

        assertEquals(true, arePositionsStrictlyIncreasing);
    }

    @Test
    @Transactional
    public void updateTaskTest_keepsTasksPositionsStrictlyIncreasing_whenUpdatingTaskPositionAndColumn() {
        UUID newColumnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
        UUID taskId = UUID.fromString("18bbe496-194a-4ec3-899f-5f2a31e4f101");

        TaskUpdateDto data = new TaskUpdateDto(
                taskId,
                newColumnId,
                2,
                null,
                null,
                null,
                null,
                null,
                null
        );

        TaskUpdateResponseDto response = taskService.updateTask(data);

        List<Task> tasks = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId))
                .getColumn().getTasks();

        tasks.sort(Comparator.comparingInt(Task::getPosition));

        boolean arePositionsStrictlyIncreasing  = IntStream.range(0, tasks.size()-1)
                .allMatch(i -> tasks.get(i).getPosition() < tasks.get(i+1).getPosition());

        assertEquals(true, arePositionsStrictlyIncreasing);
    }

    @Test
    @Transactional
    public void updateTaskTest_keepsTasksPositionsStrictlyIncreasing_whenUpdatingTaskColumn() {
        UUID newColumnId = UUID.fromString("367bdeb5-e45c-4511-a414-e8797b0266b2");
        UUID taskId = UUID.fromString("18bbe496-194a-4ec3-899f-5f2a31e4f101");

        TaskUpdateDto data = new TaskUpdateDto(
                taskId,
                newColumnId,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        TaskUpdateResponseDto response = taskService.updateTask(data);

        List<Task> tasks = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId))
                .getColumn().getTasks();

        tasks.sort(Comparator.comparingInt(Task::getPosition));

        boolean arePositionsStrictlyIncreasing  = IntStream.range(0, tasks.size()-1)
                .allMatch(i -> tasks.get(i).getPosition() < tasks.get(i+1).getPosition());

        assertEquals(true, arePositionsStrictlyIncreasing);
    }
}
