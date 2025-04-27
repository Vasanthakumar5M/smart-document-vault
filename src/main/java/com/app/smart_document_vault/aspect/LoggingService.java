package com.app.smart_document_vault.aspect;

import com.app.smart_document_vault.dto.DocumentDto;
import com.app.smart_document_vault.dto.LoginDto;
import com.app.smart_document_vault.dto.RegisterDto;
import com.app.smart_document_vault.entity.Folder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@Aspect
public class LoggingService {

    private static final Logger logger= LoggerFactory.getLogger(LoggingService.class);

    @Before("execution(public * com.app.smart_document_vault.service.UserService.*(..))")
    public void logUserService(JoinPoint joinPoint){
        logger.info("Admin requested {}", joinPoint.getSignature());
    }

    @Before("execution(public * com.app.smart_document_vault.service.AdminService.*(..))")
    public void logAdminService(JoinPoint joinPoint){
        logger.info("Admin requested {}", joinPoint.getSignature());
    }

    @Around("execution(public * com.app.smart_document_vault.service.AuthService.register(..)) && args(dto)")
    public Object logUserRegistration(ProceedingJoinPoint joinPoint,RegisterDto dto) throws Throwable {
        logger.info("New registration attempt with email: {}", dto.getEmail());
        Object result=joinPoint.proceed();
        logger.info("User Registration Success.");
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.AuthService.register(..)) && args(dto)",throwing = "exception")
    public void logUserRegistrationFailure(RegisterDto dto,Throwable exception){
        logger.error("Registration failed for email {}: {}", dto.getEmail(), exception.getMessage());
    }

    @Around("execution(public * com.app.smart_document_vault.service.AuthService.login(..)) && args(dto)")
    public Object logUserLogin(ProceedingJoinPoint joinPoint, LoginDto dto) throws Throwable {
        logger.info("Login attempt for email: {}", dto.username());
        Object result=joinPoint.proceed();
        logger.info("User '{}' successfully authenticated", dto.username());
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.AuthService.login(..)) && args(dto)",throwing = "exception")
    public void logUserLoginFailure(LoginDto dto,Throwable exception){
        logger.warn("Failed login attempt for email: {}", dto.username());
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.uploadDocument(..)) && args(file,userEmail,folderId)")
    public Object logUploadDocument(ProceedingJoinPoint joinPoint,MultipartFile file,String userEmail,Long folderId) throws Throwable {
        String fileName=file.getOriginalFilename();
        logger.info("User '{}' is uploading document: {}", userEmail, fileName);
        Object result=joinPoint.proceed();
        logger.info("Document '{}' uploaded successfully", fileName);
        return result;
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.downloadFile(..)) && args(documentId,userEmail)")
    public Object logDownloadDocument(ProceedingJoinPoint joinPoint,Long documentId,String userEmail) throws Throwable {
        logger.info("User '{}' requests to download a document with ID: {}", userEmail, documentId);
        Object result=joinPoint.proceed();
        logger.info("Document id '{}' downloaded successfully", documentId);
        return result;
    }

    @AfterThrowing(value = "execution(public * com.app.smart_document_vault.service.DocumentService.downloadFile(..)) && args(documentId,userEmail)",throwing = "exception")
    public void logDownloadFailure(Long documentId,String userEmail,Throwable exception){
        logger.warn("Download failed: Document with ID {} not found for user '{}'", documentId, userEmail);
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.deleteDocument(..)) && args(documentId,userEmail)")
    public Object logDeleteDocument(ProceedingJoinPoint joinPoint,Long documentId,String userEmail) throws Throwable {
        logger.info("User '{}' requests to delete a document with ID: {}", userEmail, documentId);
        Object result=joinPoint.proceed();
        logger.info("Document id '{}' deleted successfully", documentId);
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.DocumentService.deleteDocument(..)) && args(documentId,userEmail)",throwing = "exception")
    public void logDeleteFailure(Long documentId,String userEmail,Throwable exception){
        logger.warn("Delete failed: Document ID {} not found or unauthorized for user '{}'", documentId, userEmail);
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.generateDownloadToken(..)) && args(documentId,userEmail)")
    public Object logTokenGeneration(ProceedingJoinPoint joinPoint,Long documentId,String userEmail) throws Throwable {
        logger.info("User '{}' requests to generate download token for document ID: {}", userEmail, documentId);
        Object result = joinPoint.proceed();
        logger.info("Download token for document id '{}' generated successfully", documentId);
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.DocumentService.generateDownloadToken(..)) && args(documentId,userEmail)",throwing = "exception")
    public void logTokenGenerationFailure(Long documentId,String userEmail,Throwable exception){
        logger.warn("Token Generation failed: Document ID {} not found or unauthorized for user '{}'", documentId, userEmail);
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.downloadViaToken(..)) && args(token)")
    public Object logDownloadViaToken(ProceedingJoinPoint joinPoint,String token) throws Throwable {
        logger.info("Download attempt using token: {}", token);
        Object result=joinPoint.proceed();
        logger.info("Token '{}' validated successfully and download begins.",token);
        return result;

    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.DocumentService.downloadViaToken(..)) && args(token)",throwing = "exception")
    public void logDownloadViaTokenFailure(String token,Throwable exception){
        logger.warn("Download failed: Invalid or expired token used - {}", token);
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.getDocumentsWithFilters(..)) && args(email, folderId, fileType, fromDate, toDate)")
    public Object logDocumentsFiltering(ProceedingJoinPoint joinPoint, String email, Long folderId, String fileType,
                                    LocalDate fromDate, LocalDate toDate) throws Throwable {
        logger.info("User '{}' requests to fetch documents with filters - Folder: {}, Type: {}, FromDate: {} , ToDate: {}",
                email, folderId, fileType, fromDate, toDate);
        Object result = joinPoint.proceed();
        if (result instanceof List<?> docs) {
            logger.info("Total documents returned: {}", docs.size());
        }
        return result;
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.moveDocument(..)) && args(documentId,folderId,userEmail)")
    public Object logMoveDocument(ProceedingJoinPoint joinPoint,Long documentId,Long folderId,String userEmail) throws Throwable {
        logger.info("User '{}' requests to move document id {} to folder id {}.", userEmail, documentId,folderId);
        Object result = joinPoint.proceed();
        logger.info("Document id {} moved to folder id {} successfully", documentId,folderId);
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.DocumentService.moveDocument(..)) && args(documentId,folderId,userEmail)",throwing = "exception")
    public void logMoveDocumentFailure(Long documentId,Long folderId,String userEmail,Throwable exception){
        logger.warn("Document Move failed: Document ID {} not found or Folder ID {} not found or unauthorized for user '{}'", documentId, folderId, userEmail);
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.copyDocument(..)) && args(documentId,folderId,userEmail)")
    public Object logCopyDocument(ProceedingJoinPoint joinPoint,Long documentId,Long folderId,String userEmail) throws Throwable {
        logger.info("User '{}' requests to copy document id {} to folder id {}.", userEmail, documentId,folderId);
        Object result = joinPoint.proceed();
        logger.info("Document id {} copied to folder id {} successfully", documentId,folderId);
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.DocumentService.copyDocument(..)) && args(documentId,folderId,userEmail)",throwing = "exception")
    public void logCopyDocumentFailure(Long documentId,Long folderId,String userEmail,Throwable exception){
        logger.warn("Document Copy failed: Document ID {} not found or Folder ID {} not found or unauthorized for user '{}'", documentId, folderId, userEmail);
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.renameDocument(..)) && args(documentId,newName,userEmail)")
    public Object logDocumentRenaming(ProceedingJoinPoint joinPoint,Long documentId,String newName,String userEmail) throws Throwable {
        logger.info("User '{}' requests to rename a document ID {} to '{}'", userEmail, documentId, newName);
        Object result=joinPoint.proceed();
        logger.info("Document ID {} renamed to '{}' by user '{}'", documentId, newName, userEmail);
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.DocumentService.renameDocument(..)) && args(documentId,newName,userEmail)",throwing = "exception")
    public void logDocumentRenamingFailure(Long documentId,String newName,String userEmail,Throwable exception){
        logger.warn("Rename Document failed: Document ID {} not found or unauthorized for user '{}'", documentId, userEmail);
        logger.error("Error occurred: ",exception);
    }
    
    @Around("execution(public * com.app.smart_document_vault.service.DocumentService.findByFileName(..)) && args(fileName,userEmail)")
    public Object logDocumentSearching(ProceedingJoinPoint joinPoint,String fileName,String userEmail) throws Throwable {
        logger.info("User '{}' requests to search a document named '{}'", userEmail, fileName);
        Object result=joinPoint.proceed();
        if(result instanceof List<?> documents)
        	logger.info("{} Documents retrieved with the name of '{}' for user '{}'", documents.size(), fileName, userEmail);
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.DocumentService.findByFileName(..)) && args(fileName,userEmail)",throwing = "exception")
    public void logDocumentSearchingFailure(String fileName,String userEmail,Throwable exception){
        logger.warn("Finding Document failed: Document name '{}' not found or unauthorized for user '{}'", fileName, userEmail);
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.FolderService.createFolder(..)) && args(folderName)")
    public Object logFolderCreation(ProceedingJoinPoint joinPoint,String folderName) throws Throwable {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.info("User '{}' requests to create a folder named '{}'", authentication.getName(), folderName);
        Object result = joinPoint.proceed();
        if (result instanceof Folder folder) {
            logger.info("Folder '{}' created successfully with ID: {}", folder.getName(), folder.getId());
        }
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.FolderService.createFolder(..)) && args(folderName)",throwing = "exception")
    public void logFolderCreationFailure(String folderName,Throwable exception){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.warn("Create folder failed: Folder '{}' already exists for user '{}'", folderName, authentication.getName());
        logger.error("Error occurred: ",exception);

    }

    @Around("execution(public * com.app.smart_document_vault.service.FolderService.renameFolder(..)) && args(folderId, newName)")
    public Object logFolderRenaming(ProceedingJoinPoint joinPoint, Long folderId,String newName) throws Throwable {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.info("User '{}' requests to rename a folder ID {} to '{}'", authentication.getName(), folderId, newName);
        Object result=joinPoint.proceed();
        logger.info("Folder ID {} renamed to '{}' by user '{}'", folderId, newName, authentication.getName());
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.FolderService.renameFolder(..)) && args(folderId, newName)",throwing = "exception")
    public void logFolderRenamingFailure(Long folderId,Throwable exception){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.warn("Rename folder failed: Folder ID {} not found or unauthorized for user '{}'", folderId, authentication.getName());
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.FolderService.deleteFolder(..)) && args(folderId)")
    public Object logFolderDeletion(ProceedingJoinPoint joinPoint, Long folderId) throws Throwable {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.info("User '{}' requests to delete a folder ID: {}", authentication.getName(), folderId);
        Object result = joinPoint.proceed();
        logger.info("Folder ID {} deleted successfully by user '{}'", folderId, authentication.getName());
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.FolderService.deleteFolder(..)) && args(folderId)",throwing = "exception")
    public void logFolderDeletionFailure(Long folderId,Throwable exception){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.warn("Delete folder failed: Folder ID {} not found or unauthorized for user '{}'", folderId, authentication.getName());
        logger.error("Error occurred: ",exception);
    }

    @Around("execution(public * com.app.smart_document_vault.service.FolderService.getAllFoldersForUser(..))")
    public Object logGetAllFolders(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.info("Fetching folder list for user '{}'", authentication.getName());
        Object result = joinPoint.proceed();
        if (result instanceof List<?> folders) {
            logger.debug("User '{}' has {} folders", authentication.getName(), folders.size());
        }
        return result;
    }

    @Around("execution(public * com.app.smart_document_vault.service.FolderService.getFolderById(..)) && args(folderId)")
    public Object logFindingFolder(ProceedingJoinPoint joinPoint,Long folderId) throws Throwable {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.info("User '{}' requests to retrieve folder by ID: {}", authentication.getName(), folderId);
        Object result = joinPoint.proceed();
        logger.info("Folder ID {} retrieved successfully by user '{}'", folderId, authentication.getName());
        return result;
    }

    @AfterThrowing(pointcut = "execution(public * com.app.smart_document_vault.service.FolderService.getFolderById(..)) && args(folderId)",throwing = "exception")
    public void logFindingFolderFailure(Long folderId,Throwable exception){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        logger.warn("Get folder failed: Folder ID {} not found or unauthorized for user '{}'", folderId, authentication.getName());
        logger.error("Error occurred: ",exception);
    }

}
