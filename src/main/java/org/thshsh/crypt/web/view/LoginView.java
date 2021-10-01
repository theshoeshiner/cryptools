package org.thshsh.crypt.web.view;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.stat.inference.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.User;
import org.thshsh.crypt.UserRepository;
import org.thshsh.crypt.cryptocompare.CryptoCompare;
import org.thshsh.crypt.serv.UserService;
import org.thshsh.crypt.serv.UserService.UserExistsException;
import org.thshsh.crypt.web.SecurityConfiguration;
import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.componentfactory.Popup;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/*@SuppressWarnings("serial")
@Route(value = "login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

}
*/
//@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle(MainLayout.TITLE_PREFIX)
//@HtmlImport("frontend://bower_components/iron-form/iron-form.html")
//@JsModule("frontend://bower_components/iron-form/iron-form.html")
@NpmPackage(value = "@polymer/iron-form", version = "3.0.1")
@JsModule("@polymer/iron-form/iron-form.js")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	public static final Logger LOGGER = LoggerFactory.getLogger(LoginView.class);

	public static final String ROUTE = "login";

	private static final String EMAIL_PATTERN = "^" + "([a-zA-Z0-9_\\.\\-+])+" // local
			+ "@" + "[a-zA-Z0-9-.]+" // domain
			+ "\\." + "[a-zA-Z0-9-]{2,}" // tld
			+ "$";

	/*private LoginOverlay login = new LoginOverlay(); //

	public LoginView(){
	    login.setAction("login"); //

	    //login.setTitle((Component)null);
	    //login.setTitle("Cryptools");
	    login.setTitle((Component)null);
	    login.setOpened(true); //

	    //login.setDescription("Login Overlay Example");
	    getElement().appendChild(login.getElement()); //


	}*/

	/*private LoginForm login = new LoginForm();

	public LoginView() {
	    addClassName("login-view");
	    setSizeFull();

	    setJustifyContentMode(JustifyContentMode.CENTER);
	    setAlignItems(Alignment.CENTER);

	    login.setAction("login");

	   // add(new H1("Test Application"), login);

	    add(login);
	}*/
	
	@Autowired
	CryptoCompare cryptoCompare;

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	ApplicationContext context;

	//TextField emailField;
	//VerticalLayout formLayout;
	User registerUser;

	Binder<User> binder;
	//Button loginButton;
	//HorizontalLayout names;
	//Button register;
	//TextField userNameField;
	//PasswordField confirmPasswordField;
	//TextField firstName;
	//TextField lastName;
	//PasswordField passwordField;
	//Button backButton;
	Span message;
	//VerticalLayout registerForm;
	VerticalLayout registerLayout;
	 VerticalLayout	formLayout;
	 Element ironForm;

	 
	//VerticalLayout formsLayout;

	public LoginView() {

		this.setSizeFull();


		TitleSpan title = new TitleSpan();
		 title.addClassName("h1");
		 add(title);

		message = new Span(" ");
		 message.addClassName("message");
		 //message.setVisible(false);
		 add(message);

		 //formsLayout = new VerticalLayout();
		 //add(formsLayout);

		/*registerForm = new VerticalLayout();




		 message = new Span(" ");
		 message.addClassName("message");
		 //message.setVisible(false);
		 add(message);


			formLayout = new VerticalLayout();
			formLayout.setAlignItems(Alignment.CENTER);
			formLayout.setWidth("300px");

		names = new HorizontalLayout();
		names.setWidthFull();

		firstName = new TextField();
		firstName.setMinWidth("0px");
		firstName.setPlaceholder("First Name");


		lastName = new TextField();
		lastName.setMinWidth("0px");
		lastName.setPlaceholder("Last Name");

		names.add(firstName, lastName);
		names.setFlexGrow(1, firstName, lastName);


		emailField = new TextField();
		emailField.getElement().setAttribute("name", "username"); //
		emailField.setWidthFull();


		passwordField = new PasswordField();
		passwordField.setPlaceholder("Password");
		passwordField.getElement().setAttribute("name", "password"); //
		passwordField.setWidthFull();

		userNameField = new TextField();

		userNameField.setPlaceholder("Username (Optional)");
		userNameField.setWidthFull();

		confirmPasswordField = new PasswordField();
		confirmPasswordField.setPlaceholder("Confirm Password");
		//passwordField.getElement().setAttribute("name", "password"); //
		confirmPasswordField.setWidthFull();
		//confirmPasswordField.setVisible(false);


		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		buttons.setMargin(false);

		loginButton = new Button("Login");
		loginButton.setId("submitbutton"); //
		buttons.add(loginButton);

		backButton = new Button("Back");
		buttons.add(backButton);
		backButton.addClickListener(click -> {
			showLogin();
		});

		register = new Button("Register");
		buttons.add(register);
		register.addClickListener(click -> {
			showRegister();
		});

		//formLayout.add(names,emailField, userNameField, passwordField, confirmPasswordField, buttons);
		registerForm.add(names,userNameField,passwordField,confirmPasswordField,buttons);



		showLogin();*/

		this.setAlignItems(Alignment.CENTER);

		setClassName("login-view");

		showLogin();
	}

	TextField emailLoginField;
	Boolean login = true;

	public static class LoginForm extends VerticalLayout {

		TextField emailField;
		Span message;
		PasswordField passwordField;
		Button loginButton;

		public LoginForm(VerticalLayout page) {
			super();

			this.setSizeFull();

			//registerForm = new VerticalLayout();

			/* TitleSpan title = new TitleSpan();
			 title.addClassName("h1");
			 add(title);*/

			message = new Span(" ");
			message.addClassName("message");
			//message.setVisible(false);
			add(message);

			//formLayout = new VerticalLayout();
			this.setAlignItems(Alignment.CENTER);
			this.setWidth("300px");

			/*names = new HorizontalLayout();
			names.setWidthFull();

			firstName = new TextField();
			firstName.setMinWidth("0px");
			firstName.setPlaceholder("First Name");


			lastName = new TextField();
			lastName.setMinWidth("0px");
			lastName.setPlaceholder("Last Name");

			names.add(firstName, lastName);
			names.setFlexGrow(1, firstName, lastName);
			*/

			emailField = new TextField();
			emailField.getElement().setAttribute("name", "username"); //
			emailField.setWidthFull();

			passwordField = new PasswordField();
			passwordField.setPlaceholder("Password");
			passwordField.getElement().setAttribute("name", "password"); //
			passwordField.setWidthFull();



			HorizontalLayout buttons = new HorizontalLayout();
			buttons.setWidthFull();
			buttons.setJustifyContentMode(JustifyContentMode.CENTER);
			buttons.setMargin(false);

			loginButton = new Button("Login");
			loginButton.setId("submitbutton"); //
			buttons.add(loginButton);


			this.add(emailField, passwordField, buttons);
			//registerForm.add(names,userNameField,passwordField,confirmPasswordField,buttons);

			this.setAlignItems(Alignment.CENTER);

			setClassName("login-view");

			//showLogin();

			UI.getCurrent().getPage().executeJs(
					"document.getElementById('submitbutton').addEventListener('click', () => document.getElementById('ironform').submit());");
			//

			Element formElement = new Element("form"); //
			formElement.setAttribute("method", "post");
			formElement.setAttribute("action", "login");
			formElement.appendChild(this.getElement());

			Element ironForm = new Element("iron-form"); //
			ironForm.setAttribute("id", "ironform");
			ironForm.setAttribute("allow-redirect", true); //
			ironForm.appendChild(formElement);

			page.getElement().appendChild(ironForm); //

		}

	}



	protected void showLogin() {
		login = true;
		
		/*Button button = new Button("Push Me");
		button.setId("push-me");
		Popup popup = new Popup();
		popup.setFor(button.getId().orElse(null));
		Div text = new Div();
		text.setText("element 1");
		Div text2 = new Div();
		text2.setText("element 2");
		popup.add(text, text2);
		add(button, popup);*/

		if(registerLayout!=null) this.remove(registerLayout);

		 formLayout = new VerticalLayout();
		 formLayout.addClassName("form-layout");
		formLayout.setAlignItems(Alignment.CENTER);
		formLayout.setWidth("300px");

		/*names = new HorizontalLayout();
		names.setWidthFull();

		firstName = new TextField();
		firstName.setMinWidth("0px");
		firstName.setPlaceholder("First Name");


		lastName = new TextField();
		lastName.setMinWidth("0px");
		lastName.setPlaceholder("Last Name");

		names.add(firstName, lastName);
		names.setFlexGrow(1, firstName, lastName);*/


		emailLoginField = new TextField();
		emailLoginField.setPlaceholder("Email / Username");
		emailLoginField.getElement().setAttribute("name", "username"); //
		emailLoginField.setWidthFull();


		PasswordField passwordField = new PasswordField();
		passwordField.setPlaceholder("Password");
		passwordField.getElement().setAttribute("name", "password"); //
		passwordField.setWidthFull();

		/*	userNameField = new TextField();

			userNameField.setPlaceholder("Username (Optional)");
			userNameField.setWidthFull();
		*/
		/*confirmPasswordField = new PasswordField();
		confirmPasswordField.setPlaceholder("Confirm Password");
		//passwordField.getElement().setAttribute("name", "password"); //
		confirmPasswordField.setWidthFull();
		//confirmPasswordField.setVisible(false);
		*/

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		buttons.setMargin(false);

		Button loginButton = new Button("Login");
		loginButton.setId("submitbutton"); //
		buttons.add(loginButton);

		/*backButton = new Button("Back");
		buttons.add(backButton);
		backButton.addClickListener(click -> {
			showLogin();
		});*/

		Button register = new Button("Register");
		buttons.add(register);
		register.addClickListener(click -> {
			showRegisterForm();
		});

		formLayout.add(emailLoginField,  passwordField,  buttons);


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

		TextField firstName = new TextField();
		firstName.setMinWidth("0px");
		firstName.setPlaceholder("First Name");


		TextField lastName = new TextField();
		lastName.setMinWidth("0px");
		lastName.setPlaceholder("Last Name");

		names.add(firstName, lastName);
		names.setFlexGrow(1, firstName, lastName);


		/*emailField = new TextField();
		emailField.getElement().setAttribute("name", "username"); //
		emailField.setWidthFull();*/


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
		//passwordField.getElement().setAttribute("name", "password"); //
		confirmPasswordField.setWidthFull();
		//confirmPasswordField.setVisible(false);


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
		Html h = new Html("<span>Price information for currencies is retrieved from the CryptoCompare API. Due to usage limits an API Key is necessary for each User. Keys can be obtained by following <a href='https://www.cryptocompare.com/coins/guides/how-to-use-our-api/' target='_blank'>this guide</a>.</span>");
		//text.setText("Price data for portfolios is retrieved from the CryptoCompare API. An API Key can be obtained by following this guide.");
		text.add(h);
		/*Div text2 = new Div();
		text2.setText("element 2");*/
		popup.add(text);
		
		apikeyrow.add(button, popup);
		
		/*Button why = new Button("What's This?");
		why.setId("why");
		why.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		why.addClassName("helper-text");
		apikeyrow.add(why);
		
		Popup popup = new Popup();
		popup.addPopupOpenChangedEventListener(change -> {
			LOGGER.info("change: {}",change.isOpened());
		});
		popup.setFor("why");
		Div exp = new Div();
		Span span = new Span("Price data for portfolios is retrieved from the CryptoCompare API. An API Key can be obtained by following this guide.");
		exp.add(span);
		popup.add(exp);
		add(popup);*/
		//https://www.cryptocompare.com/coins/guides/how-to-use-our-api/

		registerLayout.add(names, userNameField,emailField,apikeyrow, passwordField, confirmPasswordField, buttons);
		//registerForm.add(names,userNameField,passwordField,confirmPasswordField,buttons);

		//formsLayout.getElement().appendChild(formLayout.getElement());


		binder.forField(emailField)
		.asRequired()
		.withValidator(new EmailValidator("Invalid Email Address"))
		.withValidator((s, c) -> {
			if (userRepo.findByEmail(s).isPresent())
				return ValidationResult.error("Email Address already in use");
			else
				return ValidationResult.ok();
		})
		.bind(User::getEmail, User::setEmail);

		binder.forField(userNameField).withNullRepresentation("")
		.withValidator(new RegexpValidator("Invalid Username", "\\p{Alnum}++")).withValidator((s, c) -> {
			if (s != null && userRepo.findByUserNameIgnoreCase(s).isPresent())
				return ValidationResult.error("Username already in use");
			else
				return ValidationResult.ok();
		}).bind(User::getUserName, User::setUserName);

		binder.forField(firstName).withNullRepresentation("").bind(User::getFirstName, User::setFirstName);
		
		binder
		.forField(apiKey)
		.asRequired()
		.withNullRepresentation("")
		.withValidator((s,c) -> {
			
			if(s.length()==64 && StringUtils.isAlphanumeric(s)) {
				return ValidationResult.ok();
			}
			else {
				return ValidationResult.error("Invalid API Key");
			}
			//CryptoCompare cc = context.getBean(CryptoCompare.class,s,null);
			//CryptoCompare cc = new CryptoCompare(s);
			//Map<String,BigDecimal> price = cc.getCurrentFiatPrice(Collections.singleton("BTC"));
			//LOGGER.info("get price: {}",price);
			
		})
		.bind(User::getApiKey, User::setApiKey);

		binder.forField(lastName).withNullRepresentation("").bind(User::getLastName, User::setLastName);

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
				//UI.getCurrent().getPage().reload();

			} catch (UserExistsException e) {

			}
		} catch (ValidationException e) {
			LOGGER.error("validation error", e);
		}
		
	}

	/*protected void showRegister() {

		if (login) {

			login = false;

			//loginButton.setVisible(false);
			//loginButton.addClassName("invisible");
			backButton.setVisible(true);

			binder = new Binder<>();
			//binder.removeBinding(emailField);

			registerUser = new User();

			emailField.setPlaceholder("Email");

			binder.forField(emailField).asRequired().withValidator(new EmailValidator("Invalid Email Address"))
					.withValidator((s, c) -> {
						if (userRepo.findByEmail(s).isPresent())
							return ValidationResult.error("Email Address already in use");
						else
							return ValidationResult.ok();
					}).bind(User::getEmail, User::setEmail);

			binder.forField(userNameField).withNullRepresentation("")
					.withValidator(new RegexpValidator("Invalid Username", "\\p{Alnum}++")).withValidator((s, c) -> {
						if (s != null && userRepo.findByUserNameIgnoreCase(s).isPresent())
							return ValidationResult.error("Username already in use");
						else
							return ValidationResult.ok();
					}).bind(User::getUserName, User::setUserName);

			//submitButton.setEnabled(false);
			names.setVisible(true);
			confirmPasswordField.setVisible(true);
			userNameField.setVisible(true);

			binder.forField(firstName).withNullRepresentation("").bind(User::getFirstName, User::setFirstName);

			binder.forField(lastName).withNullRepresentation("").bind(User::getLastName, User::setLastName);

			binder.forField(confirmPasswordField).asRequired().withValidator((u, s) -> {
				LOGGER.info("validate password {} vs {}", u, passwordField.getValue());
				if (u.equals(passwordField.getValue()))
					return ValidationResult.ok();
				else
					return ValidationResult.error("Passwords do not match");
			}).bind(User::getPassword, User::setPassword);

			names.add(firstName, lastName);
			names.setFlexGrow(1, firstName, lastName);

		} else {

			try {
				LOGGER.info("write bean");
				binder.writeBean(registerUser);
				try {
					userService.registerUser(registerUser);
					message.setText("Registration Successful");
					message.setVisible(true);

					showLogin();
					LOGGER.info("registration success");
					//UI.getCurrent().getPage().reload();

				} catch (UserExistsException e) {

				}
			} catch (ValidationException e) {
				LOGGER.error("validation error", e);
			}
			//User user = new User(firstName.getValue(),lastName.getValue(),emailField.getValue(),userNameField.getValue(),passwordField.getValue());
		}
	}*/

	@Override
	protected void onAttach(AttachEvent attachEvent) {

		LOGGER.info("on attach");



		super.onAttach(attachEvent);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		LOGGER.info("beforeEnter");
		//login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
		Map<String,List<String>> params = event.getLocation().getQueryParameters().getParameters();
		if (params.containsKey("error")) {
			
			String hash = params.get("error").get(0);
			
			String msg = SecurityConfiguration.ERROR_MESSAGES.inverseBidiMap().get(hash);
			
			LOGGER.info("setting error");
			emailLoginField.setErrorMessage(msg);
			emailLoginField.setInvalid(true);
			//message.setText("Username and Password do not match");
			//emailField.setValue("error");
		}
		if (params.containsKey("confirm")) {
			
			String token = params.get("confirm").get(0);
			userRepo.findByConfirmToken(token).ifPresent(u -> {
				
				u.setConfirmed(true);
				userRepo.save(u);
				
				
				
			});
			
			message.setText("Account confirmed. Login below.");
			message.setVisible(true);
			
			//String msg = SecurityConfiguration.ERROR_MESSAGES.inverseBidiMap().get(hash);
			
			//LOGGER.info("setting error");
			//emailLoginField.setErrorMessage(msg);
			//emailLoginField.setInvalid(true);
			//message.setText("Username and Password do not match");
			//emailField.setValue("error");
		}
	}
}
