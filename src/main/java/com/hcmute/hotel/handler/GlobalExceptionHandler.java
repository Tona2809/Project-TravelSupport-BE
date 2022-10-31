package com.hcmute.hotel.handler;



import com.hcmute.hotel.model.payload.response.MessageResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;


import java.util.HashMap;

import java.util.List;
import java.util.Map;



@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<MessageResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = "";
        String fieldName = "";
        String errorMessage = "";
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            fieldName = ((FieldError) error).getField();
            errorMessage = error.getDefaultMessage();
            errors += errorMessage;
        }
        MessageResponse messageResponse = new MessageResponse("Bad request", errors, errors);
        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<MessageResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        MessageResponse errorDetails = new MessageResponse("Forbidden", "ACCESS_IS_DENIED", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }
}
