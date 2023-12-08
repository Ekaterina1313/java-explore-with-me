package ru.practicum.ewm;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.common.error.ApiError;
import ru.practicum.common.error.IncorrectParamException;
import ru.practicum.common.error.InvalidRequestException;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleConflict(DataIntegrityViolationException ex) {
        return new ApiError("CONFLICT",
                "Integrity constraint has been violated.",
                ex.getMessage());
    }

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

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ApiError(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(value = {IncorrectParamException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleIncorrectParamException(IncorrectParamException ex) {
        return new ApiError(
                HttpStatus.FORBIDDEN.name(),
                "For the requested operation the conditions are not met.",
                ex.getMessage()
        );
    }
}