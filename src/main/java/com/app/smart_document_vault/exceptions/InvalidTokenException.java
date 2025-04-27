package com.app.smart_document_vault.exceptions;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException(String message){
        super(message);
    }

}
