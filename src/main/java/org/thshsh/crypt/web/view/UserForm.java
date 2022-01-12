package org.thshsh.crypt.web.view;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Role;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.RoleRepository;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.web.AppConfiguration;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.views.main.UserMenu;
import org.thshsh.vaadin.entity.EntityDialog;
import org.vaadin.gatanaso.MultiselectComboBox;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.validator.EmailValidator;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@CssImport("./styles/user-form.css") 
public class UserForm extends AppEntityForm<User,Long> {

	public static enum Type {
		Admin,Register,Profile;
	}
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
 	AppSession session;
	
	@Autowired
 	UserMenu userMenu;
	
	@Autowired
	AppConfiguration appConfig;
	
	UserNameValidator userNameValidator;

	Type type;
	

	public UserForm(User entity) {
		super(User.class, entity);
		this.type = Type.Admin;
	}
	
	public UserForm(User entity, Type t) {
		super(User.class, entity);
		this.type = t;
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
		
		
		if(type == Type.Admin) {
			HorizontalLayout outer = formLayout.startHorizontalLayout();
			outer.setSpacing(true);
			outer.setWidthFull();
			VerticalLayout left = formLayout.startVerticalLayout();
			outer.setFlexGrow(1, left);
		}
		
		TextField name = new TextField("Name");
		name.setWidthFull();
		binder
		.forField(name)
		.withNullRepresentation(StringUtils.EMPTY)
		.bind(User::getDisplayName, User::setDisplayName);
		formLayout.add(name);
		
		
		TextField userField = new TextField("Username");
		userField.setWidthFull();
		formLayout.add(userField);

		binder
		.forField(userField)
		.withNullRepresentation("")
		.withValidator(userNameValidator)
		.bind(User::getUserName, User::setUserName);
		
		TextField emailField = new TextField("Email");
		//emailField.setReadOnly(true);
		if(type == Type.Profile) {
			emailField.setEnabled(false);
		}
		emailField.setWidthFull();
		formLayout.add(emailField);
		binder
		.forField(emailField)
		.asRequired()
		.withValidator(new EmailValidator("Invalid Email Address"))
		.bind(User::getEmail, User::setEmail);
		
		
		TextField apiField = new TextField("API Key");
		apiField.setWidthFull();
		
	
		
		BindingBuilder<User,String> bb = binder
		.forField(apiField)
		.withNullRepresentation(StringUtils.EMPTY)
		.withValidator(context.getBean(ApiKeyValidator.class));
		if(appConfig.getRequireApiKey()) bb.asRequired();
		bb.bind(User::getApiKey, User::setApiKey);
		
		formLayout.add(apiField);
		

		
		
		

		/*	HorizontalLayout em =formLayout.startHorizontalLayout();
			
			em.setWidthFull();
			//un.setJustifyContentMode(JustifyContentMode.CENTER);
			em.setAlignItems(Alignment.BASELINE);*/
		
		
			/*Button changeEmail = new Button("Change");
			changeEmail.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			formLayout.add(changeEmail);*/
		
		//formLayout.endLayout();
		
		if(type == Type.Admin) {
			
			 //end the vertical
			formLayout.endLayout();
			formLayout.startVerticalLayout();
			
			
			List<Role> all = roleRepo.findAll();
			
			MultiselectComboBox<Role> multiselectComboBox = new MultiselectComboBox<>("Roles");
			multiselectComboBox.setWidthFull();
			multiselectComboBox.setItems(all);
			multiselectComboBox.setItemLabelGenerator(r -> {
				return r.getName();
			});
			formLayout.add(multiselectComboBox);
			binder.forField(multiselectComboBox).bind(User::getRoles, User::setRoles);
			
			
			//formLayout.startHorizontalLayout();
	
			ToggleButton tb = new ToggleButton("Confirmed",false);
			binder
			.forField(tb)
			.bind(User::getConfirmed, User::setConfirmed);
			
			formLayout.add(tb);
			
			formLayout.endLayout();
			
			formLayout.endLayout();
			
		}
		
		//formLayout.endLayout();

	}

	@Override
	protected Long getEntityId(User e) {
		return e.getId();
	}
	
	
	
	@Override
	protected void persist() {
		super.persist();
		session.refresh();
		userMenu.refresh();
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
