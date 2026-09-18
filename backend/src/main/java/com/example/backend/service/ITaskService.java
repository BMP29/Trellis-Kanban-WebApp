package com.example.backend.service;

import com.example.backend.dto.TaskCreateRequest;
import com.example.backend.dto.TaskCreatedResponseDto;
import com.example.backend.dto.TaskUpdateDto;
import com.example.backend.dto.TaskUpdateResponseDto;

import java.util.UUID;

public interface ITaskService {
    public TaskCreatedResponseDto createTask(TaskCreateRequest data);
    public TaskUpdateResponseDto updateTask(TaskUpdateDto data);
    public void deleteTask(UUID taskId);
}
