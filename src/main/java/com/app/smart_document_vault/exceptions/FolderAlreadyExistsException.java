package com.app.smart_document_vault.exceptions;


public class FolderAlreadyExistsException extends RuntimeException {
    public FolderAlreadyExistsException(String message) {
        super(message);
    }
}

