package org.thshsh.crypt.web.view;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Role;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.RoleRepository;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.vaadin.entity.EntityDialog;
import org.thshsh.vaadin.entity.EntityForm;
import org.vaadin.gatanaso.MultiselectComboBox;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@CssImport("./styles/user-form.css") 
public class UserForm extends AppEntityForm<User,Long> {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	ApplicationContext context;
	
	
	
	UserNameValidator userNameValidator;
	
	Boolean profile = false;

	public UserForm(User entity) {
		super(User.class, entity);
	}
	
	public UserForm(User entity, Boolean profile) {
		super(User.class, entity);
		this.profile = profile;
	}

	@Override
	protected JpaRepository<User, Long> getRepository() {
		return userRepo;
	}
	
	@Override
	@PostConstruct
	public void postConstruct() {
		userNameValidator = context.getBean(UserNameValidator.class,entity);
		super.postConstruct();
		titleLayout.removeAll();
		titleLayout.setAlignItems(Alignment.CENTER);
		Icon user = VaadinIcon.USER.create();
		user.addClassName("profile");
		titleLayout.add(user); 
		
		if(entity != null) {
			Span name = new Span(entity.getUserName());
			if(create) name.setText("New User");
			titleLayout.add(name);
		}
		
		HorizontalLayout buttonsLeft = new HorizontalLayout();
		buttonsLeft.setAlignItems(Alignment.START);
		this.buttons.addComponentAsFirst(buttonsLeft);
		this.buttons.setFlexGrow(1, buttonsLeft);
		
		Button change = new Button("Change Password");
		buttonsLeft.add(change);
		change.addClickListener(click -> {
			ChangePasswordFormDialog cpd = context.getBean(ChangePasswordFormDialog.class,entity);
			cpd.open();
			cpd.addOpenedChangeListener(changed -> {
				if(cpd.getEntityForm().getSaved()) {
					//need to update user pass
					//User u = userRepo.findById(entity.getId()).get();
					entity.setPassword(((ChangePasswordForm)cpd.getEntityForm()).newPassword);
					LOGGER.info("set entity password: {}",entity.getPassword());
				}
			});
		});
	}

	@Override
	protected void setupForm() {

		//formLayout.startHorizontalLayout();
		
		formLayout.setSpacing(false);
		
		TextField name = new TextField("Name");
		name.setWidthFull();
		binder
		.forField(name)
		.withNullRepresentation("")
		.bind(User::getDisplayName, User::setDisplayName);
		formLayout.add(name);
		
		TextField apiField = new TextField("API Key");
		apiField.setWidthFull();
		binder
		.forField(apiField)
		.withNullRepresentation("")
		.bind(User::getApiKey, User::setApiKey);
		formLayout.add(apiField);
		
		
		//HorizontalLayout un = formLayout.startHorizontalLayout();
		//un.setWidthFull();
		//un.setJustifyContentMode(JustifyContentMode.CENTER);
		//un.setAlignItems(Alignment.BASELINE);
		
		TextField userField = new TextField("Username");
		//userField.setReadOnly(true);
		userField.setWidthFull();
		formLayout.add(userField);
		//un.setFlexGrow(1, userField);
		binder
		.forField(userField)
		.asRequired()
		.withValidator(userNameValidator)
		//.withValidator(new EmailValidator("Invalid Email Address"))
				/*.withValidator((s,c) -> {
					if(userRepo.findByEmail(s).isPresent()) return ValidationResult.error("Email Address already in use");
					else return ValidationResult.ok();
				})*/
		.bind(User::getUserName, User::setUserName);
		
		/*Button changeUser = new Button("Change");
		changeUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		formLayout.add(changeUser);*/
		//un.setFlexGrow(0, changeUser);
		
		//formLayout.endLayout();
		
		
		HorizontalLayout em =formLayout.startHorizontalLayout();
		
		em.setWidthFull();
		//un.setJustifyContentMode(JustifyContentMode.CENTER);
		em.setAlignItems(Alignment.BASELINE);
		
		TextField emailField = new TextField("Email");
		//emailField.setReadOnly(true);
		if(profile) {
			emailField.setEnabled(false);
		}
		emailField.setWidthFull();
		formLayout.add(emailField);
		binder
		.forField(emailField)
		.asRequired()
				/*.withValidator(new EmailValidator("Invalid Email Address"))
				.withValidator((s,c) -> {
					if(userRepo.findByEmail(s).isPresent()) return ValidationResult.error("Email Address already in use");
					else return ValidationResult.ok();
				})*/
		.bind(User::getEmail, User::setEmail);
		
		/*Button changeEmail = new Button("Change");
		changeEmail.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		formLayout.add(changeEmail);*/
		
		formLayout.endLayout();
		
		if(!profile) {
			
			formLayout.startHorizontalLayout();
			
			ToggleButton tb = new ToggleButton("Confirmed",false);
			binder
			.forField(tb)
			.bind(User::getConfirmed, User::setConfirmed);
			
			formLayout.add(tb);
			
			formLayout.endLayout();
			
			List<Role> all = roleRepo.findAll();
			
			MultiselectComboBox<Role> multiselectComboBox = new MultiselectComboBox<>("Roles");
			
			multiselectComboBox.setWidthFull();
			//multiselectComboBox.setLabel("Select Roles");
			//multiselectComboBox.setPlaceholder("Choose...");
			multiselectComboBox.setItems(all);
			//multiselectComboBox.setCompactMode(true);
			//Stream<Role> fetch = multiselectComboBox.getDataProvider().fetch(new Query<>());
			//LOGGER.info("Stream: {}",fetch.count());
			//List<Role> found = fetch.collect(Collectors.toList());
			//LOGGER.info("found: {}",found);
			
			multiselectComboBox.setItemLabelGenerator(r -> {
				//LOGGER.info("get item label {}",r);
				//LOGGER.error("get item label",new RuntimeException());
				return r.getName();
			});
			formLayout.add(multiselectComboBox);
			
			binder.forField(multiselectComboBox).bind(User::getRoles, User::setRoles);
			
		}

	}

	@Override
	protected Long getEntityId(User e) {
		return e.getId();
	}
	
	public static class ChangePasswordEntity {
		
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
	
	/*@Component
	@Scope("prototype")
	public static class ChangePasswordDialog extends Dialog {
		
		User user;
		
		public ChangePasswordDialog(User user) {
			this.user = user;
			this.setWidth("300px");
		}
		
		@PostConstruct
		public void postConstruct() {
			
			VerticalLayout layout = new VerticalLayout();
			layout.setSpacing(false);
			this.add(layout);
			
			
		
			PasswordField oldPass = new PasswordField("Current Password");
			oldPass.setWidthFull();
			layout.add(oldPass);
			
			PasswordField newPass = new PasswordField("New Password");
			newPass.setWidthFull();
			layout.add(newPass);
			
			PasswordField confirmPass = new PasswordField("Confirm New Password");
			confirmPass.setWidthFull();
			layout.add(confirmPass);
			
			HorizontalLayout buttons = new HorizontalLayout();
			buttons.setWidthFull();
			buttons.setMargin(true);
			
			//buttons.setAlignItems(Alignment.END);
			buttons.setJustifyContentMode(JustifyContentMode.END);
			layout.add(buttons);
			
			
			Button save = new Button("Change");
			buttons.add(save);
			Button can = new Button("Cancel");
			can.addClickListener(click -> this.close());
			buttons.add(can);
			
			
			
			
		}
		
	}*/
	
	@Component
	@Scope("prototype")
	public static class ChangePasswordFormDialog extends EntityDialog<ChangePasswordEntity,Long> {

		public ChangePasswordFormDialog(User user) {
			super(ChangePasswordForm.class, new ChangePasswordEntity(user));
			this.setWidth("400px");
		}
		
		@PostConstruct
		public void postConstruct() {
			super.postConstruct();
			this.setCloseOnOutsideClick(true);
		}
		
	}
	
	@Component
	@Scope("prototype")
	public static class ChangePasswordForm extends AppEntityForm<ChangePasswordEntity,Long> {
		
		@Autowired
		UserRepository userRepo;
		
		@Autowired
		PasswordEncoder encoder;
		
		@Autowired
		PlatformTransactionManager transactionManager;
		
		String newPassword;
		User user;
		
		public ChangePasswordForm(ChangePasswordEntity e) {
			super(ChangePasswordEntity.class,null);
			this.user = e.getUser();
			this.setPadding(false);
			this.confirm = false;
			
		}

		@PostConstruct
		public void postConstruct() {
			super.postConstruct();
			this.titleLayout.setVisible(false);
		}

		@Override
		protected JpaRepository<ChangePasswordEntity, Long> getRepository() {
			return null;
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
		protected Long getEntityId(ChangePasswordEntity e) {
			return null;
		}

		@Override
		protected void persist() {

			TransactionTemplate template = new TransactionTemplate(transactionManager);
			template.executeWithoutResult(ts -> {
				newPassword = encoder.encode(entity.getNewPassword());
				User u = userRepo.findById(user.getId()).get();
				u.setPassword(newPassword);
				LOGGER.info("new password: {}",newPassword);
				
			});
			this.saved = true;
			
		}
		
		
		
	}


}
