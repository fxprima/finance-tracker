package com.example.finance_tracker.exception;

public class InvalidCSVFormatException extends RuntimeException{

    public InvalidCSVFormatException(String message) {
        super(message);
    }

}
