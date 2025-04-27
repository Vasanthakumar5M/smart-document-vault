package com.app.smart_document_vault.service;

import com.app.smart_document_vault.dto.DashboardDto;
import com.app.smart_document_vault.dto.DocumentDto;
import com.app.smart_document_vault.dto.FolderDto;
import com.app.smart_document_vault.entity.*;
import com.app.smart_document_vault.exceptions.FileAccessDeniedException;
import com.app.smart_document_vault.exceptions.FolderNotFoundException;
import com.app.smart_document_vault.exceptions.InvalidTokenException;
import com.app.smart_document_vault.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final UserRepository userRepository;

    private final DownloadTokenRepository downloadTokenRepository;

    private final FolderRepository folderRepository;

    private final FolderService folderService;

    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository, DownloadTokenRepository downloadTokenRepository, FolderRepository folderRepository, FolderService folderService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.downloadTokenRepository = downloadTokenRepository;
        this.folderRepository = folderRepository;
        this.folderService = folderService;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Document uploadDocument(MultipartFile file,String userEmail,Long folderId) throws IOException {

        User user=userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        Path uploadPath= Paths.get(uploadDir);

//       Creating directory
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        //save file to disk
        String fileName=UUID.randomUUID()+"_"+file.getOriginalFilename();
        Path filePath=uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);

        //Save metadata to DB
        Document document=new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(filePath.toString());
        document.setFileType(file.getContentType());
        document.setSize(file.getSize());
        document.setUploadedAt(LocalDateTime.now());
        document.setUploadedBy(user);

        //setting the folder
        if(folderId!=null){
            Folder folder=folderRepository.findById(folderId)
                    .orElseThrow(()->new FolderNotFoundException("Folder not found."));
            document.setFolder(folder);
            folder.addDocument(document);
        }

        return documentRepository.save(document);
    }

    public Resource downloadFile(Long documentId,String userEmail) throws MalformedURLException, FileNotFoundException {

        Document document=documentRepository.findById(documentId).orElseThrow(()->new FileNotFoundException("File not found."));

        //check ownership
        if(!document.getUploadedBy().getEmail().equals(userEmail)){
            throw new FileAccessDeniedException("You are not allowed to download this file.");
        }

        Path filePath=Paths.get(document.getFilePath());
        Resource resource=new UrlResource(filePath.toUri());

        if(!resource.exists()){
            throw new FileNotFoundException("File not found on server.");
        }

        return resource;
    }

    public List<DocumentDto> getDocumentsWithFilters(String email, String name, Long folderId, String fileType,
                                                     LocalDate fromDate, LocalDate toDate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Document> documents = documentRepository.findByUploadedBy(user);

        return documents.stream()
                .filter(doc -> {
                	// Name filter
                    if(name!=null && !doc.getFileName().equalsIgnoreCase(name)){
                        return false;
                    }
                    // Folder filter
                    if (folderId != null && (doc.getFolder() == null || !doc.getFolder().getId().equals(folderId))) {
                        return false;
                    }

                    // File type filter
                    if (fileType != null && !doc.getFileType().equalsIgnoreCase(fileType)) {
                        return false;
                    }

                    // Date range filter
                    if (fromDate != null && doc.getUploadedAt().toLocalDate().isBefore(fromDate)) {
                        return false;
                    }
                    if (toDate != null && doc.getUploadedAt().toLocalDate().isAfter(toDate)) {
                        return false;
                    }

                    return true;
                })
                .map(DocumentDto::new)
                .collect(Collectors.toList());
    }

    public void deleteDocument(Long documentId,String userEmail) throws IOException {
        Document document=documentRepository.findById(documentId).orElseThrow(()-> new FileNotFoundException("File not found"));

        if(!document.getUploadedBy().getEmail().equals(userEmail)){
            throw new FileAccessDeniedException("You are not allowed to delete this file.");
        }

        //Delete the file from disk
        Files.deleteIfExists(Paths.get(document.getFilePath()));

        //Delete metadata from DB
        documentRepository.delete(document);
    }

    public String generateDownloadToken(Long documentId,String userEmail) throws FileNotFoundException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        if (!document.getUploadedBy().getEmail().equals(userEmail)) {
            throw new FileAccessDeniedException("Unauthorized");
        }

        //token which identifies the shared document.
        String token= UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        //which holds the documents to be shared in the database.
        DownloadToken downloadToken=new DownloadToken();
        downloadToken.setToken(token);
        downloadToken.setExpiresAt(expiry); //<- setting the expiry time.
        downloadToken.setDocument(document);

        downloadTokenRepository.save(downloadToken);

        return "http://localhost:8080/api/documents/share/" + token; //<- this link will be shared to anyone.
    }

    public Resource downloadViaToken(String token) throws MalformedURLException {
        DownloadToken downloadToken=downloadTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token."));

        //checking expiration of the token.
        if(downloadToken.getExpiresAt().isBefore(LocalDateTime.now())){
            return null;
        }

        Document document=downloadToken.getDocument();
        Path filePath= Paths.get(document.getFilePath());
        Resource resource=new UrlResource(filePath.toUri());

        return resource;
    }

    //It will run automatically for every one hour to delete the expired token.
    @Scheduled(fixedRate = 60*60*1000)
    public void deleteExpiredTokens(){
        downloadTokenRepository.deleteAllExpired(LocalDateTime.now());
    }


    public List<DocumentDto> getDocumentsByUserId(Long userId){
        return documentRepository.findByUserId(userId)
                .stream()
                .map(DocumentDto::new)
                .toList();
    }

    public DashboardDto userDashboard(){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        List<FolderDto> folders=folderService.getAllFoldersForUser();
        List<DocumentDto> documents=documentRepository.findByUserEmail(username)
                .stream()
                .filter(doc -> {
                    return doc.getFolder() == null;
                })
                .map(DocumentDto::new)
                .toList();
        return new DashboardDto(folders,documents);
    }

    public void moveDocument(Long documentId,Long folderId,String userEmail) throws FileNotFoundException {

        Document document=documentRepository.findById(documentId)
                .orElseThrow(()-> new FileNotFoundException("File not found."));

        if (!document.getUploadedBy().getEmail().equals(userEmail)) {
            throw new FileAccessDeniedException("Unauthorized");
        }

        //if the folder id is present then we have to move the files into the folder.
        if(folderId!=null){
            Folder folder=folderRepository.findById(folderId)
                    .orElseThrow(() -> new FolderNotFoundException("Folder not found."));

            if(!folder.getUser().getEmail().equals(userEmail)){
                throw new FileAccessDeniedException("You are not allowed to perform this action.");
            }

            document.setFolder(folder);
            folder.getDocuments().add(document);
            folderRepository.save(folder);
            return;
        }

        document.setFolder(null);
        documentRepository.save(document);
    }

    public void copyDocument(Long documentId,Long folderId,String userEmail) throws IOException {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new FileNotFoundException("File not found."));

        if (!document.getUploadedBy().getEmail().equals(userEmail)) {
            throw new FileAccessDeniedException("Unauthorized");
        }

        Path copyPath=Paths.get(uploadDir);

        String fileName=UUID.randomUUID()+"_"+document.getFileName();
        Path filePath=copyPath.resolve(fileName);
        Path source=Paths.get(document.getFilePath());
        Files.copy(source,filePath, StandardCopyOption.REPLACE_EXISTING);

        //if the folder id is present then we have to copy the files into the folder.
        if(folderId!=null){
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new FolderNotFoundException("Folder not found."));

            if (!folder.getUser().getEmail().equals(userEmail)) {
                throw new FileAccessDeniedException("You are not allowed to perform this action.");
            }

            Document document1=new Document(document);
            document1.setFilePath(filePath.toString());
            document1.setFolder(folder);
            folder.addDocument(document1);
            folderRepository.save(folder);
            return;
        }


        Document document1=new Document(document);
        document1.setFilePath(filePath.toString());
        document1.setFolder(null);

        documentRepository.save(document1);
    }

    public void renameDocument(Long documentId,String newName,String userEmail) throws FileNotFoundException, FileAlreadyExistsException {
        Document document=documentRepository.findById(documentId)
                .orElseThrow(() -> new FileNotFoundException("File not found."));

        if(!document.getUploadedBy().getEmail().equals(userEmail)){
            throw new FileAccessDeniedException("Unauthorized to rename this file.");
        }

        if(documentRepository.existsByUploadedByEmailAndFileName(userEmail,newName)){
            throw new FileAlreadyExistsException("File with that name already exists.");
        }

        document.setFileName(newName);
        documentRepository.save(document);
    }
    
    public List<DocumentDto> findByFileName(String fileName,String userEmail){
    	User user=userRepository.findByEmail(userEmail)
    			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	
    	return documentRepository.findByUploadedByAndFileNameLike(user,"%"+fileName+"%").stream()
    			.map(DocumentDto::new)
    			.toList();
    			
    }
}
