package com.app.smart_document_vault.exceptions;

public class FolderAccessDeniedException extends RuntimeException{

    public FolderAccessDeniedException(String message){
        super(message);
    }
}
