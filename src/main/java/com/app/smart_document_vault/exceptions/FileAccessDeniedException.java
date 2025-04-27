package com.app.smart_document_vault.exceptions;

public class FileAccessDeniedException extends RuntimeException{

    public FileAccessDeniedException(String message){
        super(message);
    }
}
