package org.thshsh.crypt.web.view.user;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.Role;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.RoleRepository;
import org.thshsh.crypt.web.AppConfiguration;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.view.ApiKeyValidator;
import org.thshsh.crypt.web.view.AppEntityForm;
import org.thshsh.crypt.web.views.main.UserMenu;
import org.thshsh.vaadin.entity.EntityDescriptor;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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
		super(entity);
		this.type = Type.Admin;
	}
	
	public UserForm(User entity, Type t) {
		super(entity);
		this.type = t;
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
			formLayout.endComponent();
			formLayout.startVerticalLayout();
			
			
			List<Role> all = roleRepo.findAll();
			
			MultiSelectComboBox<Role> multiselectComboBox = new MultiSelectComboBox<>("Roles");
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
			
			formLayout.endComponent();
			
			formLayout.endComponent();
			
		}
		
		//formLayout.endLayout();

	}

	
	@Override
	protected User persist() {
		User p =super.persist();
		session.refresh();
		userMenu.refresh();
		return p;
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
	
	
	
	
	@Override
	@Autowired
	public void setDescriptor(EntityDescriptor<User, Long> descriptor) {
		super.setDescriptor(descriptor);
	}

	@Override
	@Autowired
	public void setRepository(CrudRepository<User, Long> repository) {
		super.setRepository(repository);
	}
	
	
	
	


}
