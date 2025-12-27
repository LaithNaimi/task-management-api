package com.laith.taskmanagement.controller;

import com.laith.taskmanagement.dto.CreateTaskRequestDTO;
import com.laith.taskmanagement.dto.TaskResponseDTO;
import com.laith.taskmanagement.dto.UpdateTaskRequestDTO;
import com.laith.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks() {
        List<TaskResponseDTO> allTasks = taskService.getAll();
        return new ResponseEntity<>(allTasks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id) {
        TaskResponseDTO task = taskService.getById(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody CreateTaskRequestDTO newTask) {
        TaskResponseDTO task = taskService.addTask(newTask);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTaskRequestDTO newTask) {
        TaskResponseDTO task = taskService.updateTask(id, newTask);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
