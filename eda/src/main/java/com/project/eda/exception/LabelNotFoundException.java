package com.project.eda.exception;

public class LabelNotFoundException extends RuntimeException {

    public LabelNotFoundException() {
        super("Label not found");
    }

    public LabelNotFoundException(String name) {
        super("Label with name '%s' was not found.".formatted(name));
    }


}
