package com.app.smart_document_vault.exceptions;

public class UserAlreadyActiveException extends RuntimeException{

    public UserAlreadyActiveException(String message){
        super(message);
    }
}
