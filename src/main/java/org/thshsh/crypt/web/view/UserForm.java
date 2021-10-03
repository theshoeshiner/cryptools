package org.thshsh.crypt.web.view;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Role;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.RoleRepository;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.vaadin.entity.EntityForm;
import org.vaadin.gatanaso.MultiselectComboBox;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
@CssImport("./styles/user-form.css") 
public class UserForm extends EntityForm<User,Long> {

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

}
