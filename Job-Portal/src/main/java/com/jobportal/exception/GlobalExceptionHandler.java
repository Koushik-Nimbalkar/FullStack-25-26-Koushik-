package com.jobportal.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static boolean isApi(HttpServletRequest request) {
        return request.getRequestURI() != null && request.getRequestURI().startsWith("/api/");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        if (isApi(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 404);
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(BadRequestException.class)
    public Object handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        if (isApi(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 400);
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        if (isApi(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", "Validation failed");
            body.put("fields", ex.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a + "; " + b)));
            return ResponseEntity.badRequest().body(body);
        }
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 400);
        mv.addObject("message", "Please check the form and try again.");
        return mv;
    }
}
