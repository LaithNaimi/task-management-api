package com.laith.taskmanagement.repository;

import com.laith.taskmanagement.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Override
    @EntityGraph(attributePaths = {"category"})
    Page<Task> findAll(Specification<Task> spec, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByOwnerIdAndCategoryId(Long userId, Long id);
}
