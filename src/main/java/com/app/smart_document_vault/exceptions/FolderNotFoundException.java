package com.app.smart_document_vault.exceptions;

public class FolderNotFoundException extends RuntimeException{

    public FolderNotFoundException(String message){
        super(message);
    }
}
