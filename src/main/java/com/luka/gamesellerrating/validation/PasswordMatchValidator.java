package com.luka.gamesellerrating.validation;

import com.luka.gamesellerrating.annotation.PasswordMatch;
import com.luka.gamesellerrating.dto.UserDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserDTO> {

    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext context) {
        if (userDTO.getPassword() == null || userDTO.getConfirmPassword() == null) {
            return false;
        }

        boolean isValid = userDTO.getPassword().equals(userDTO.getConfirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("confirmPassword")
                   .addConstraintViolation();
        }

        return isValid;
    }
}