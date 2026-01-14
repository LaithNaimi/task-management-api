package com.laith.taskmanagement.repository;

import com.laith.taskmanagement.model.TaskPriority;
import com.laith.taskmanagement.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import com.laith.taskmanagement.model.Task;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByCategoryId(Long categoryId);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(TaskPriority priority, Pageable pageable);

    Page<Task> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority, Pageable pageable);

    Page<Task> findByStatusAndCategoryId(TaskStatus status, Long categoryId, Pageable pageable);

    Page<Task> findByPriorityAndCategoryId(TaskPriority priority,Long categoryId, Pageable pageable);

    Page<Task> findByStatusAndPriorityAndCategoryId(TaskStatus status, TaskPriority priority, Long categoryId, Pageable pageable);

    Page<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titleKeyword,
            String descriptionKeyword,
            Pageable pageable
    );

    Page<Task> findByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCase(
            TaskStatus status1, String titleKeyword,
            TaskStatus status2, String descriptionKeyword,
            Pageable pageable
    );

    Page<Task> findByPriorityAndTitleContainingIgnoreCaseOrPriorityAndDescriptionContainingIgnoreCase(
            TaskPriority priority1, String titleKeyword,
            TaskPriority priority2, String descriptionKeyword,
            Pageable pageable
    );

    Page<Task> findByCategoryIdAndTitleContainingIgnoreCaseOrCategoryIdAndDescriptionContainingIgnoreCase(
            Long categoryId1, String titleKeyword,
            Long categoryId2, String descriptionKeyword,
            Pageable pageable
    );

    Page<Task> findByStatusAndPriorityAndTitleContainingIgnoreCaseOrStatusAndPriorityAndDescriptionContainingIgnoreCase(
            TaskStatus status1, TaskPriority priority1, String titleKeyword,
            TaskStatus status2, TaskPriority priority2, String descriptionKeyword,
            Pageable pageable
    );

    Page<Task> findByStatusAndCategoryIdAndTitleContainingIgnoreCaseOrStatusAndCategoryIdAndDescriptionContainingIgnoreCase(
            TaskStatus status1, Long categoryId1, String titleKeyword,
            TaskStatus status2, Long categoryId2, String descriptionKeyword,
            Pageable pageable
    );

    Page<Task> findByPriorityAndCategoryIdAndTitleContainingIgnoreCaseOrPriorityAndCategoryIdAndDescriptionContainingIgnoreCase(
            TaskPriority priority1, Long categoryId1, String titleKeyword,
            TaskPriority priority2, Long categoryId2, String descriptionKeyword,
            Pageable pageable
    );

    Page<Task> findByStatusAndPriorityAndCategoryIdAndTitleContainingIgnoreCaseOrStatusAndPriorityAndCategoryIdAndDescriptionContainingIgnoreCase(
            TaskStatus status1, TaskPriority priority1, Long categoryId1, String titleKeyword,
            TaskStatus status2, TaskPriority priority2, Long categoryId2, String descriptionKeyword,
            Pageable pageable
    );
}
