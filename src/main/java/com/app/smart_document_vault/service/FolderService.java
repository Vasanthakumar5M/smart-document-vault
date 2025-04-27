package com.app.smart_document_vault.service;

import com.app.smart_document_vault.dto.DocumentDto;
import com.app.smart_document_vault.dto.FolderDto;
import com.app.smart_document_vault.entity.Document;
import com.app.smart_document_vault.entity.Folder;
import com.app.smart_document_vault.entity.User;
import com.app.smart_document_vault.exceptions.FolderAccessDeniedException;
import com.app.smart_document_vault.exceptions.FolderAlreadyExistsException;
import com.app.smart_document_vault.exceptions.FolderNotFoundException;
import com.app.smart_document_vault.repository.DocumentRepository;
import com.app.smart_document_vault.repository.FolderRepository;
import com.app.smart_document_vault.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FolderService {

    private final UserRepository userRepository;

    private final FolderRepository folderRepository;

    private final DocumentRepository documentRepository;

    public FolderService(UserRepository userRepository, FolderRepository folderRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.documentRepository = documentRepository;
    }

    public Folder createFolder(String folderName){
        User user=userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new UsernameNotFoundException("User not found."));

        if (folderRepository.existsByUserEmailAndName(user.getEmail(), folderName)) {
            throw new FolderAlreadyExistsException("A folder with that name already exists.");
        }

        //creating the folder
        Folder folder=new Folder();
        folder.setName(folderName);
        folder.setUser(user);
        return folderRepository.save(folder);
    }

    public Folder renameFolder(Long folderId, String newName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException("Folder not found"));

        if (!folder.getUser().getEmail().equals(username)) {
            throw new FolderAccessDeniedException("Unauthorized to rename this folder.");
        }

        if (folderRepository.existsByUserEmailAndName(username, newName)) {
            throw new FolderAlreadyExistsException("A folder with that name already exists.");
        }

        //changing the name
        folder.setName(newName);
        return folderRepository.save(folder);
    }

    public void deleteFolder(Long folderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException("Folder not found"));

        if (!folder.getUser().getEmail().equals(username)) {
            throw new FolderAccessDeniedException("Unauthorized to delete this folder.");
        }
        
        folder.getDocuments().forEach(doc->{
        	try {
				Files.deleteIfExists(Paths.get(doc.getFilePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
        
        folderRepository.delete(folder);
    }

    public List<FolderDto> getAllFoldersForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return folderRepository.findAllByUserEmail(username).stream()
                .map(FolderDto::new)
                .toList();
    }

    public List<DocumentDto> getFolderById(Long folderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException("Folder not found"));

        if (!folder.getUser().getEmail().equals(username)) {
            throw new FolderAccessDeniedException("Unauthorized to access this folder.");
        }
        
        return folder.getDocuments().stream()
        		.map(DocumentDto::new)
        		.toList();
    }

    public List<DocumentDto> getFolderByName(String name) {
        String userEmail=SecurityContextHolder.getContext().getAuthentication().getName();
        
        Folder folder=folderRepository.findByUserEmailAndName(userEmail, name).orElseThrow(()->new FolderNotFoundException("Folder not found."));
        
        List<DocumentDto> documents=folder.getDocuments().stream()
        							.map(DocumentDto::new)
        							.toList();
        
        return documents;
    }
}
