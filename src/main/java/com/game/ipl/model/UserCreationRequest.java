package com.game.ipl.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.game.ipl.exceptions.FailedCreateUserException;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreationRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String password;
    @JsonIgnore
    private MultipartFile userImage;

    public void validateUserCreationRequest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserCreationRequest>> violations = validator.validate(this);

        violations.stream().findFirst().ifPresent(userCreationRequestConstraintViolation -> {
            throw new FailedCreateUserException(userCreationRequestConstraintViolation.getPropertyPath() + " " + userCreationRequestConstraintViolation.getMessage());
        });
    }
}
