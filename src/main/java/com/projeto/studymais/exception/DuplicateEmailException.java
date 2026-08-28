package com.projeto.studymais.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Email ja cadastrado.");
    }
}
