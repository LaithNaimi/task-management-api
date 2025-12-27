package com.laith.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.laith.taskmanagement.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
