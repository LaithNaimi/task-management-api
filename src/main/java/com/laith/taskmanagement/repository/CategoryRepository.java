package com.laith.taskmanagement.repository;

import com.laith.taskmanagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);

    List<Category> findAllByOwnerIdOrderByNameAsc(Long ownerId);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

}
