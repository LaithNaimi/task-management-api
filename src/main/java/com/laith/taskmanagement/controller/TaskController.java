package com.laith.taskmanagement.controller;

import com.laith.taskmanagement.dto.CreateTaskRequestDTO;
import com.laith.taskmanagement.dto.TaskResponseDTO;
import com.laith.taskmanagement.dto.UpdateTaskRequestDTO;
import com.laith.taskmanagement.model.TaskPriority;
import com.laith.taskmanagement.model.TaskStatus;
import com.laith.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getTasks(
            @RequestParam(required = false)TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            Pageable pageable
            ) {
        return ResponseEntity.ok(taskService.getAll(status, priority, categoryId, q, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody CreateTaskRequestDTO task) {
        TaskResponseDTO created  = taskService.addTask(task);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequestDTO newTask
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, newTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
