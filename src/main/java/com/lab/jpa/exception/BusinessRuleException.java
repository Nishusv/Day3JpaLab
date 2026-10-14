package com.lab.jpa.exception;

/** Thrown when a request is valid JSON but breaks a business rule (e.g. duplicate email). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
