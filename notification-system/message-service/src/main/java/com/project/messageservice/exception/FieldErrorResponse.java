package com.project.messageservice.exception;

public record FieldErrorResponse(
        String field,
        String message
) {}
