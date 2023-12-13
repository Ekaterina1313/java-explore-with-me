package ru.practicum.mainService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.mainService.error.ApiError;
import ru.practicum.mainService.error.IncorrectParamException;
import ru.practicum.mainService.error.InvalidRequestException;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiError handleConflict(DataIntegrityViolationException ex) {
        return new ApiError("INTERNAL_SERVER_ERROR",
                "Integrity constraint has been violated.",
                ex.getMessage());
    }

    @ExceptionHandler(value = {InvalidRequestException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleInvalidRequest(Exception ex) {
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