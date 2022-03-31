package com.example.springbatch;

public class NoSkippableException extends Exception {
    public NoSkippableException(String s) {
        super(s);
    }
}
