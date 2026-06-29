package com.project.eda.exception;

public record FieldErrorResponse(
        String field,
        String message
) {}
