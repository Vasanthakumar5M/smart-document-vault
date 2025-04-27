package com.app.smart_document_vault.exceptionhandler;

import com.app.smart_document_vault.dto.ErrorResponse;
import com.app.smart_document_vault.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class Exceptionhandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.NOT_FOUND;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidCredentialsException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.FORBIDDEN;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleException(DisabledException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.LOCKED;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(UserAlreadyActiveException.class)
    public ResponseEntity<ErrorResponse> handleException(UserAlreadyActiveException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.BAD_REQUEST;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(FolderNotFoundException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.NOT_FOUND;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(FileNotFoundException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.NOT_FOUND;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(FileAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(FileAccessDeniedException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.FORBIDDEN;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidTokenException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.UNAUTHORIZED;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(FolderAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(FolderAlreadyExistsException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.CONFLICT;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(FolderAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(FolderAccessDeniedException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.FORBIDDEN;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(FileAlreadyExistsException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.CONFLICT;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }
    
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleException(SQLIntegrityConstraintViolationException exception){
        String message="The email already exists";
        HttpStatus status=HttpStatus.CONFLICT;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException exception){
        String message="Enter valid details";
        HttpStatus status=HttpStatus.PARTIAL_CONTENT;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }
    
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleException(InternalAuthenticationServiceException exception){
        String message="Username not found";
        HttpStatus status=HttpStatus.UNAUTHORIZED;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(BadCredentialsException exception){
        String message=exception.getMessage();
        HttpStatus status=HttpStatus.UNAUTHORIZED;
        int code=status.value();
        return ResponseEntity.status(status).body(new ErrorResponse(message,status,code, LocalDateTime.now()));
    }

}
