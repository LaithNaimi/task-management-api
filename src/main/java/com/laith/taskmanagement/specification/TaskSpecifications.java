package com.laith.taskmanagement.specification;

import com.laith.taskmanagement.model.Task;
import com.laith.taskmanagement.model.TaskPriority;
import com.laith.taskmanagement.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

public final class TaskSpecifications {
    private TaskSpecifications() {}

    public static Specification<Task> hasOwnerId(Long ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Task> hasId(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> keywordInTitleOrDescription(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String like = "%" + keyword.toLowerCase() + "%";

            var titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), like);
            var descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), like);

            return criteriaBuilder.or(titleLike, descriptionLike);
        };
    }

    public static Specification<Task> build(Long ownerId, TaskStatus status, TaskPriority priority, Long categoryId, String q) {
        Specification<Task> specification = Specification.where(hasOwnerId(ownerId));

        if(status != null) {
            specification = specification.and(hasStatus(status));
        }

        if(priority != null) {
            specification = specification.and(hasPriority(priority));
        }
        if(categoryId != null) {
            specification = specification.and(hasCategoryId(categoryId));
        }
        if(q != null && !q.trim().isEmpty()) {
            specification = specification.and(keywordInTitleOrDescription(q.trim()));
        }

        return specification;

    }
}

