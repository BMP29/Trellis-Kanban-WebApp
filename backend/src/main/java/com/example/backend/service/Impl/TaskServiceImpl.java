package com.example.backend.service.Impl;

import com.example.backend.dto.TaskCreateRequest;
import com.example.backend.dto.TaskCreatedResponseDto;
import com.example.backend.dto.TaskUpdateDto;
import com.example.backend.dto.TaskUpdateResponseDto;
import com.example.backend.entity.BoardColumn;
import com.example.backend.entity.Task;
import com.example.backend.exception.ColumnNotFoundException;
import com.example.backend.exception.TaskNotFoundException;
import com.example.backend.mapper.TaskMapper;
import com.example.backend.repository.ColumnRepository;
import com.example.backend.repository.TaskRepository;
import com.example.backend.service.ITaskService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements ITaskService {

    TaskRepository taskRepository;
    ColumnRepository columnRepository;
    TaskMapper taskMapper;

    @Transactional
    @Override
    public TaskCreatedResponseDto createTask(TaskCreateRequest data) {
        BoardColumn column = columnRepository.findById(data.columnId())
                .orElseThrow(() -> new ColumnNotFoundException(data.columnId()));

        int maxPosition = column.getTasks().stream()
                .mapToInt(Task::getPosition)
                .max()
                .orElse(0);

        Task newTask = taskMapper.toEntity(
                data,
                UUID.randomUUID(),
                column,
                maxPosition + 100
        );

        return taskMapper.toCreatedResponse(newTask);
    }

    @Transactional
    @Override
    public TaskUpdateResponseDto updateTask(TaskUpdateDto data) {
        Task task = taskRepository.findById(data.taskId())
                .orElseThrow(() -> new TaskNotFoundException(data.taskId()));

        BoardColumn currentColumn = task.getColumn();

        List<BoardColumn> columns = currentColumn.getBoard().getColumns();

        UUID currentColumnId = currentColumn.getId();
        UUID newColumnId = data.newColumnId();

        if(!Objects.equals(currentColumnId, newColumnId) && newColumnId != null) {
            BoardColumn newColumn = columns.stream()
                    .filter(c -> c.getId().equals(newColumnId))
                    .findAny()
                    .orElseThrow(() -> new ColumnNotFoundException(newColumnId));

            newColumn.getTasks().add(task);
            task.setColumn(newColumn);
        }

        if(data.position() != null && data.position() < 0)
            throw new IllegalArgumentException("Position must be greater than or equal to 0");

        taskMapper.updateTaskFromDto(data, task);

        List<Task> tasks = task.getColumn().getTasks();

        orderTaskListAfterTask(tasks, task);

        return taskMapper.toUpdatedResponse(task);
    }

    private void orderTaskListAfterTask(List<Task> list, Task task) {
        list.sort(
                Comparator.comparingInt(Task::getPosition)
                        .thenComparing(t -> t.getId().equals(task.getId()) ? 0 : 1)
        );

        int index = list.indexOf(task);

        Task currentTask = task;

        for(int i = index+1; i < list.size(); i++) {
            if(list.get(i).getPosition() > currentTask.getPosition())
                return;

            list.get(i).setPosition(currentTask.getPosition()+10);
            currentTask = list.get(i);
        }
    }

    @Transactional
    @Override
    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        List<Task> tasks = task.getColumn().getTasks();

        tasks.remove(task);
    }
}
