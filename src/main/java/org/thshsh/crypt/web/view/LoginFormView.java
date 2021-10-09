package org.thshsh.crypt.web.view;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.RoleRepository;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.serv.UserService;
import org.thshsh.crypt.serv.UserService.UserExistsException;
import org.thshsh.crypt.web.SecurityConfiguration;
import org.thshsh.vaadin.UIUtils;

import com.vaadin.componentfactory.Popup;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login")
@Route(value = "login")
public class LoginFormView extends VerticalLayout implements BeforeEnterObserver  {

	public static final Logger LOGGER = LoggerFactory.getLogger(LoginFormView.class);
	
	public LoginFormView() {
		
		
	}
	
	Span message;
	
	LoginForm lf;
	VerticalLayout loginLayout;;
	
	@Autowired
	PasswordEncoder encoder;
	

	
	@PostConstruct
	public void postConstruct() {
		
		this.addClassName("login-view");
		this.setAlignItems(Alignment.CENTER);
		this.setSpacing(false);
		

		TitleSpan title = new TitleSpan();
		title.addClassName("h1");
		add(title);

		message = new Span(" ");
		message.addClassName("message");
		// message.setVisible(false);
		add(message);
		
		LOGGER.info("postConstruct");

		showLogin();
		
		//Button button = new Button("CLick me");
		//lf.getElement().appendChild(button.getElement());
		
	}
	
	protected void showLogin() {
		if(registerLayout!=null) this.remove(registerLayout);
		
		loginLayout = new VerticalLayout();
		loginLayout.setWidth("300px");
		loginLayout.setAlignItems(Alignment.CENTER);
		loginLayout.setSpacing(false);
		add(loginLayout);
		
		lf = new LoginForm();
		lf.setAction("login");
		lf.setForgotPasswordButtonVisible(false);
		lf.getElement().getClassList().add("cryptools-login");
		loginLayout.add(lf);
		
		
		LOGGER.info("children: {}",lf.getElement().getChildCount());
		lf.getChildren().forEach(c -> {
			LOGGER.info("child: {},{}",c,c.getClass());
		});
		
		
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		buttons.setMargin(false);

		Button loginButton = new Button("Login");
		loginButton.setId("submitbutton"); //
		buttons.add(loginButton);
		UIUtils.setElementAttribute(loginButton, "type", "submit");
		loginButton.addClickListener(click -> {
			//lf.getElement().executeJs("console.log('form: '+this);");
			lf.getElement().executeJs("console.log(this);");
			//lf.getElement().executeJs("this.submit");
			lf.getElement().executeJs("this.submit();");
			//lf.getElement().executeJs("console.log(this.querySelector('vaadin-button'));");
			//lf.getElement().executeJs("console.log(this.querySelector('vaadin-button').click);");
			//lf.getElement().executeJs("this.querySelector('vaadin-button').click();");
			//encoder.matches(lf.get, encodedPassword)
			
		});
		
	
		Button register = new Button("Register");
		buttons.add(register);
		register.addClickListener(click -> {
			showRegisterForm();
		});
		
		loginLayout.add(buttons);
	}
	
	Binder<User> binder;
	Boolean login;
	User registerUser;
	VerticalLayout registerLayout;
	
	@Autowired
	UserNameValidator userNameValidator;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	UserService userService;
	
	protected void showRegisterForm() {

		login = false;

		this.remove(loginLayout);
		this.message.setText(" ");
		
		//getElement().removeChild(ironForm);

		binder = new Binder<>();
		registerUser = new User();

		registerLayout = new VerticalLayout();
		registerLayout.addClassName("form-layout");
		registerLayout.setAlignItems(Alignment.CENTER);
		registerLayout.setWidth("300px");

		HorizontalLayout names = new HorizontalLayout();
		names.setWidthFull();

		TextField nameField = new TextField();
		nameField.setMinWidth("0px");
		nameField.setPlaceholder("Name");

		names.add(nameField);
		names.setFlexGrow(1, nameField);

	

		PasswordField passwordField = new PasswordField();
		passwordField.setPlaceholder("Password");
		passwordField.getElement().setAttribute("name", "password"); //
		passwordField.setWidthFull();

		TextField userNameField = new TextField();
		userNameField.setPlaceholder("Username (Optional)");
		userNameField.setWidthFull();

		TextField emailField = new TextField();
		emailField.setPlaceholder("Email");
		emailField.setWidthFull();

		TextField apiKey = new TextField();
		apiKey.setPlaceholder("CryptoCompare API Key");
		apiKey.setWidthFull();

		PasswordField confirmPasswordField = new PasswordField();
		confirmPasswordField.setPlaceholder("Confirm Password");
		// passwordField.getElement().setAttribute("name", "password"); //
		confirmPasswordField.setWidthFull();
		// confirmPasswordField.setVisible(false);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		buttons.setMargin(false);

		/*loginButton = new Button("Login");
		loginButton.setId("submitbutton"); //
		buttons.add(loginButton);*/

		Button backButton = new Button("Back");
		buttons.add(backButton);
		backButton.addClickListener(click -> {
			showLogin();
		});

		Button register = new Button("Register");
		buttons.add(register);
		register.addClickListener(click -> {
			registerUser();

		});

		HorizontalLayout apikeyrow = new HorizontalLayout();
		apikeyrow.setWidthFull();
		apikeyrow.add(apiKey);

		Button button = new Button("What's This?");
		button.addClassName("helper-text");
		button.addClassName("api-help");
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		button.setId("api-help-button");
		Popup popup = new Popup();
		popup.setFor(button.getId().orElse(null));
		Div text = new Div();
		text.addClassName("helper-text");
		text.addClassName("popup");
		text.setWidth("250px");
		Html h = new Html(
				"<span>Price information for currencies is retrieved from the CryptoCompare API. Due to usage limits an API Key is necessary for each User. Keys can be obtained by following <a href='https://www.cryptocompare.com/coins/guides/how-to-use-our-api/' target='_blank'>this guide</a>.</span>");
		// text.setText("Price data for portfolios is retrieved from the CryptoCompare
		// API. An API Key can be obtained by following this guide.");
		text.add(h);
		/*Div text2 = new Div();
		text2.setText("element 2");*/
		popup.add(text);

		apikeyrow.add(button, popup);

		registerLayout.add(names, userNameField, emailField, apikeyrow, passwordField, confirmPasswordField, buttons);
		

		binder.forField(emailField).asRequired().withValidator(new EmailValidator("Invalid Email Address"))
				.withValidator((s, c) -> {
					if (userRepo.findByEmail(s).isPresent())
						return ValidationResult.error("Email Address already in use");
					else
						return ValidationResult.ok();
				}).bind(User::getEmail, User::setEmail);

		binder.forField(userNameField).withNullRepresentation("")
				/*.withValidator(new RegexpValidator("Invalid Username", "\\p{Alnum}++"))
				.withValidator((s, c) -> {
					if (s != null && userRepo.findByUserNameIgnoreCase(s).isPresent())
						return ValidationResult.error("Username already in use");
					else
						return ValidationResult.ok();
				})*/
				.withValidator(userNameValidator).bind(User::getUserName, User::setUserName);

		binder.forField(nameField).withNullRepresentation("").bind(User::getDisplayName, User::setDisplayName);

		binder.forField(apiKey).asRequired().withNullRepresentation("").withValidator((s, c) -> {

			if (s.length() == 64 && StringUtils.isAlphanumeric(s)) {
				return ValidationResult.ok();
			} else {
				return ValidationResult.error("Invalid API Key");
			}
			

		}).bind(User::getApiKey, User::setApiKey);

		// binder.forField(lastName).withNullRepresentation("").bind(User::getLastName,
		// User::setLastName);

		binder.forField(confirmPasswordField).asRequired().withValidator((u, s) -> {
			LOGGER.info("validate password {} vs {}", u, passwordField.getValue());
			if (u.equals(passwordField.getValue()))
				return ValidationResult.ok();
			else
				return ValidationResult.error("Passwords do not match");
		}).bind(User::getPassword, User::setPassword);

		this.add(registerLayout);

	}

	protected void registerUser() {

		try {
			LOGGER.info("write bean");
			binder.writeBean(registerUser);
			try {
				userService.registerUser(registerUser);
				message.setText("Registration Successful. Check email for confirmation link.");
				message.setVisible(true);

				showLogin();
				LOGGER.info("registration success");
				// UI.getCurrent().getPage().reload();

			} catch (UserExistsException e) {

			}
		} catch (ValidationException e) {
			LOGGER.error("validation error", e);
		}

	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		LOGGER.info("beforeEnter");
		// login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
		Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
		if (params.containsKey("error")) {

			String hash = params.get("error").get(0);

			String msg = SecurityConfiguration.ERROR_MESSAGES.inverseBidiMap().get(hash);

			LOGGER.info("setting error");
			
			message.setText(msg);
			message.addClassName("error");
			//lf.setError(true);
			
			
			//emailLoginField.setErrorMessage(msg);
			//emailLoginField.setInvalid(true);
		
		}
		if (params.containsKey("confirm")) {

			String token = params.get("confirm").get(0);
			userRepo.findByConfirmToken(token).ifPresent(u -> {

				u.setConfirmed(true);
				userRepo.save(u);

			});

			message.setText("Account confirmed. Login below.");
			message.setVisible(true);


		}
	}


}
