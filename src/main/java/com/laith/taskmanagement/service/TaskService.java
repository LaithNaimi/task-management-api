package com.laith.taskmanagement.service;

import com.laith.taskmanagement.dto.CreateTaskRequestDTO;
import com.laith.taskmanagement.dto.TaskResponseDTO;
import com.laith.taskmanagement.dto.UpdateTaskRequestDTO;
import com.laith.taskmanagement.exception.TaskNotFoundException;
import com.laith.taskmanagement.model.Task;
import com.laith.taskmanagement.mapper.TaskMapper;
import com.laith.taskmanagement.model.TaskStatus;
import com.laith.taskmanagement.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final TaskMapper taskMapper;
    public TaskService(TaskRepository taskRepo, TaskMapper taskMapper) {
        this.taskRepo = taskRepo;
        this.taskMapper = taskMapper;
    }

    public List<TaskResponseDTO> getAll() {
        List<Task> taskList = taskRepo.findAll();
        List<TaskResponseDTO> taskResponsesList = new ArrayList<>();
        for (Task task : taskList) {
            taskResponsesList.add(taskMapper.mapEntityToDTO(task));
        }
        return taskResponsesList;
    }

    public TaskResponseDTO getById(Long id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        TaskResponseDTO taskResponseDTO = taskMapper.mapEntityToDTO(task);
        return taskResponseDTO;
    }

    public TaskResponseDTO addTask(CreateTaskRequestDTO newTask) {
        Task task = new Task();
        task.setTitle(newTask.getTitle());
        task.setDescription(newTask.getDescription());
        task.setStatus(newTask.getStatus());
        task.setDueDate(newTask.getDueDate());
        return taskMapper.mapEntityToDTO(taskRepo.save(task));
    }

    public TaskResponseDTO updateTask(Long id, @Valid UpdateTaskRequestDTO updateTaskRequest) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        if (updateTaskRequest.getTitle() != null) {
            String title = updateTaskRequest.getTitle().trim();
            if (title.isEmpty()) {
                throw new IllegalArgumentException("title must not be blank");
            }
            task.setTitle(title);
        }        if(updateTaskRequest.getDescription() != null) task.setDescription(updateTaskRequest.getDescription());
        if(updateTaskRequest.getStatus() != null) task.setStatus(updateTaskRequest.getStatus());
        if(updateTaskRequest.getDueDate() != null) task.setDueDate(updateTaskRequest.getDueDate());
        return taskMapper.mapEntityToDTO(taskRepo.save(task));
    }

    public void deleteTask(Long id) {
        Task task = taskRepo.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepo.delete(task);
    }
}
