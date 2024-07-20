package com.sky.kbelajevs.task.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 200, message = "Email can have a maximum of 200 characters")
    private String email;

    @Size(max = 120, message = "Name can have a maximum of 120 characters")
    private String name;

    @Size(max = 120, message = "Name can have a maximum of 120 characters")
    private String password;
}
