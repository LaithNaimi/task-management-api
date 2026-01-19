package com.laith.taskmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }


    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTaskNotFound(TaskNotFoundException ex) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, "Task Not Found", ex.getMessage());
        pd.setType(ProblemTypes.TASK_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }


    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCategoryNotFound(CategoryNotFoundException ex) {
        ProblemDetail pd = problem(HttpStatus.NOT_FOUND, "Category Not Found", ex.getMessage());
        pd.setType(ProblemTypes.CATEGORY_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
        ProblemDetail pd = problem(HttpStatus.CONFLICT, "Category Already Exists", ex.getMessage());
        pd.setType(ProblemTypes.CATEGORY_ALREADY_EXISTS);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<ProblemDetail> handleCategoryInUse(CategoryInUseException ex) {
        ProblemDetail pb = problem(HttpStatus.CONFLICT, "Category In Use", ex.getMessage());
        pb.setType(ProblemTypes.CATEGORY_IN_USE);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pb);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Malformed JSON Request","Invalid request body of enum value");
        pd.setType(ProblemTypes.MALFORMED_JSON);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Validation Failed", "Request validation failed");
        pd.setType(ProblemTypes.VALIDATION);

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach((err) -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        pd.setProperty("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Invalid Query Parameter", "Invalid value for parameter: " + ex.getName());
        pd.setType(ProblemTypes.INVALID_QUERY_PARAM);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
        pd.setType(ProblemTypes.BAD_REQUEST);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        ProblemDetail pd = problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error occurred");
        pd.setType(ProblemTypes.INTERNAL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }
}
