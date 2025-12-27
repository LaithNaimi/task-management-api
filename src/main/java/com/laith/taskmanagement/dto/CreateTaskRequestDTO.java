package com.laith.taskmanagement.dto;

import com.laith.taskmanagement.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequestDTO {

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Size(max = 1000)
    private String description;
    private TaskStatus status;

    @FutureOrPresent
    private LocalDate dueDate;


}
