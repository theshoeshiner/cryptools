package org.thshsh.crypt.web.view;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.User;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.serv.UserService;
import org.thshsh.crypt.serv.UserService.UserExistsException;
import org.thshsh.crypt.web.SecurityConfiguration;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.UIUtils;

import com.vaadin.componentfactory.Popup;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route(value = LoginRegisterView.ROUTE)
@PageTitle(MainLayout.TITLE_PREFIX)
@NpmPackage(value = "@polymer/iron-form", version = "3.0.1")
@JsModule("@polymer/iron-form/iron-form.js")
public class LoginRegisterView extends VerticalLayout implements BeforeEnterObserver {

	public static final Logger LOGGER = LoggerFactory.getLogger(LoginRegisterView.class);

	public static final String ROUTE = "lv";

	private static final String EMAIL_PATTERN = "^" + "([a-zA-Z0-9_\\.\\-+])+" // local
			+ "@" + "[a-zA-Z0-9-.]+" // domain
			+ "\\." + "[a-zA-Z0-9-]{2,}" // tld
			+ "$";


	@Autowired
	CryptoCompare cryptoCompare;

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepo;

	@Autowired
	ApplicationContext context;

	@Autowired
	UserNameValidator userNameValidator;

	// TextField emailField;
	// VerticalLayout formLayout;
	User registerUser;

	Binder<User> binder;
	Span message;
	VerticalLayout registerLayout;
	VerticalLayout formLayout;
	Element ironForm;

	// VerticalLayout formsLayout;

	public LoginRegisterView() {

		this.setSizeFull();

		TitleSpan title = new TitleSpan();
		title.addClassName("h1");
		add(title);

		message = new Span(" ");
		message.addClassName("message");
		// message.setVisible(false);
		add(message);


		this.setAlignItems(Alignment.CENTER);

		setClassName("login-view");

		showLogin();
	}

	TextField emailLoginField;
	Boolean login = true;

	protected void showLogin() {
		login = true;


		if (registerLayout != null)
			this.remove(registerLayout);

		formLayout = new VerticalLayout();
		formLayout.addClassName("form-layout");
		formLayout.setAlignItems(Alignment.CENTER);
		formLayout.setWidth("300px");


		emailLoginField = new TextField();
		emailLoginField.setPlaceholder("Email / Username");
		emailLoginField.getElement().setAttribute("name", "username"); //
		emailLoginField.getElement().setProperty("id", "username");
		emailLoginField.getElement().executeJs("this.setAttribute('id','username');");
		emailLoginField.getElement().executeJs("console.log('test js log: '+this);");
		emailLoginField.getElement().executeJs("console.log(this);");
		//emailLoginField.getElement().executeJs("console.log($(this,'input'));");
		//emailLoginField.getElement().executeJs("console.log(findElement);");
		emailLoginField.getElement().executeJs("console.log(this.querySelectorAll);");
		emailLoginField.getElement().executeJs("console.log('here');");
		emailLoginField.getElement().executeJs("this.shadowRoot.querySelector('input').setAttribute('id','username');");
		emailLoginField.getElement().executeJs("console.log(this.shadowRoot.querySelector('input'));");
		emailLoginField.getElement().executeJs("console.log('here2');");
		emailLoginField.getElement().executeJs("console.log(document.querySelectorAll('span.h1'));");
		emailLoginField.getElement().executeJs("console.log(this.querySelectorAll('input'));");
		emailLoginField.getElement().executeJs("console.log(document.querySelector('*'));");
		//emailLoginField.getElement().executeJs("console.log(this.querySelector('input'));");
		//emailLoginField.getElement().executeJs("console.log(this.querySelector('label'));");
		emailLoginField.getElement().executeJs("console.log(document.querySelector('input'));");
		
		emailLoginField.getElement().executeJs("console.log('finish');");

		
		//UIUtils.setElementAttribute(emailLoginField, "autocomplete", "username");
		UIUtils.setElementAttribute(emailLoginField, "id", "username");
		
		emailLoginField.setId("username");
		emailLoginField.setWidthFull();

		
		
		PasswordField passwordField = new PasswordField();
		passwordField.setPlaceholder("Password");
		passwordField.getElement().setAttribute("name", SecurityConfiguration.PASSWORD_PARAM); //
		passwordField.setWidthFull();
		//UIUtils.setElementAttribute(passwordField, "autocomplete", "password");
		UIUtils.setElementAttribute(passwordField, "name", SecurityConfiguration.PASSWORD_PARAM);
		
		passwordField.getElement().executeJs("this.shadowRoot.querySelector('input').setAttribute('id','password');");

		

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		buttons.setMargin(false);

		Button loginButton = new Button("Login");
		loginButton.setId("submitbutton"); //
		buttons.add(loginButton);
		UIUtils.setElementAttribute(loginButton, "type", "submit");
		
	
		Button register = new Button("Register");
		buttons.add(register);
		register.addClickListener(click -> {
			showRegisterForm();
		});

		formLayout.add(emailLoginField, passwordField, buttons);

		UI.getCurrent().getPage().executeJs(
				"document.getElementById('submitbutton').addEventListener('click', () => document.getElementById('ironform').submit());"); //

		Element formElement = new Element("form"); //
		formElement.setAttribute("method", "post");
		formElement.setAttribute("action", "login");
		formElement.appendChild(formLayout.getElement());

		ironForm = new Element("iron-form"); //
		ironForm.setAttribute("id", "ironform");
		ironForm.setAttribute("allow-redirect", true); //
		ironForm.appendChild(formElement);

		getElement().appendChild(ironForm); //

	}

	protected void showRegisterForm() {

		login = false;

		getElement().removeChild(ironForm);

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
	protected void onAttach(AttachEvent attachEvent) {

		LOGGER.info("on attach");

		super.onAttach(attachEvent);
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
			emailLoginField.setErrorMessage(msg);
			emailLoginField.setInvalid(true);
			// message.setText("Username and Password do not match");
			// emailField.setValue("error");
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
