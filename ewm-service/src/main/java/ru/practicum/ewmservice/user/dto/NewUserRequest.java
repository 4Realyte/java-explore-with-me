package ru.practicum.ewmservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
