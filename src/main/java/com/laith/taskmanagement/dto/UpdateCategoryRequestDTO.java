package com.laith.taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequestDTO {

    @NotBlank
    @Size(min = 2, max = 60)
    private String name;
}
