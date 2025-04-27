package com.app.smart_document_vault.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterDto{
	@NotBlank(message = "Name cannot be empty") 
	private String fullName;
	@Email(message = "Enter a valid email")
	private String email;
	private String password;
	
	
	public RegisterDto(@NotBlank String fullName, @Email String email, String password) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
