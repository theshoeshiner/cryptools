package org.thshsh.crypt.web.view.user;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.view.AppEntityForm;

import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.ValidationResult;

@Component
@Scope("prototype")
public class ChangePasswordForm extends AppEntityForm<ChangePasswordEntity,Long> {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	String newPassword;
	User user;
	
	public ChangePasswordForm(ChangePasswordEntity e) {
		super(null);
		this.user = e.getUser();
		this.setPadding(false);
		
	}

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		this.titleLayout.setVisible(false);
		this.getButtons().setConfirm(false);
	}


	@Override
	protected void setupForm() {
		
		if(!SecurityUtils.hasAccess(Feature.User, Access.ReadWriteDelete)) {
			PasswordField oldPass = new PasswordField("Current Password");
			oldPass.setWidthFull();
			formLayout.add(oldPass);
			
			binder.forField(oldPass)
			.asRequired()
			.withValidator((s,c) -> {
				if(encoder.matches(s, user.getPassword())) return ValidationResult.ok();
				else return ValidationResult.error("Current Password Incorrect");
			})
			.bind(ChangePasswordEntity::getCurrentPassword,ChangePasswordEntity::setCurrentPassword);
		}
		
		PasswordField newPass = new PasswordField("New Password");
		newPass.setWidthFull();
		formLayout.add(newPass);
		
		PasswordField confirmPass = new PasswordField("Confirm New Password");
		confirmPass.setWidthFull();
		formLayout.add(confirmPass);

		binder.forField(newPass)
		.asRequired()
		.bind(ChangePasswordEntity::getNewPassword,ChangePasswordEntity::setNewPassword);
		

		binder.forField(confirmPass)
		.asRequired()
		.withValidator((u, s) -> {
			if (u.equals(newPass.getValue()))
				return ValidationResult.ok();
			else
				return ValidationResult.error("Passwords do not match");
		})
		.bind(ChangePasswordEntity::getConfirmPassword,ChangePasswordEntity::setConfirmPassword);
		
		
	}


	@Override
	protected ChangePasswordEntity persist() {

		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.executeWithoutResult(ts -> {
			newPassword = encoder.encode(entity.getNewPassword());
			User u = userRepo.findById(user.getId()).get();
			u.setPassword(newPassword);
			LOGGER.info("new password: {}",newPassword);
			
		});
		this.saved = true;
		return entity;
		
	}
	
	
	
}