package org.thshsh.crypt.web.view.user;

import org.thshsh.crypt.User;

public class ChangePasswordEntity {
	
	User user;
	String currentPassword;
	String newPassword;
	String confirmPassword;

	public ChangePasswordEntity() {}
	
	public ChangePasswordEntity(User user) {
		this.user = user;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}