package ru.practicum.ewm.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleInvalidRequest(InvalidRequestException ex) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                ex.getMessage()
        );
    }
}