package org.thshsh.crypt.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thshsh.crypt.User;
import org.thshsh.crypt.UserRepository;
import org.thshsh.crypt.serv.UserService;
import org.thshsh.crypt.serv.UserService.UserExistsException;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
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
@PageTitle("Login")
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
	 UserService userService;

	 @Autowired
	 UserRepository userRepo;

	TextField emailField;
	VerticalLayout formLayout;
	User registerUser;

	public LoginView() {

		Binder<User> binder = new Binder();


		this.setSizeFull();

		formLayout = new VerticalLayout();
		formLayout.setAlignItems(Alignment.CENTER);
		formLayout.setWidth("300px");

		HorizontalLayout names = new HorizontalLayout();
		names.setVisible(false);
		names.setWidthFull();

		TextField firstName = new TextField();
		firstName.setMinWidth("0px");
		firstName.setPlaceholder("First Name");
		binder
		.forField(firstName)
		.bind(User::getFirstName, User::setFirstName);

		TextField lastName = new TextField();
		lastName.setMinWidth("0px");
		lastName.setPlaceholder("Last Name");
		binder
		.forField(lastName)
		.bind(User::getLastName, User::setLastName);

		names.add(firstName, lastName);
		names.setFlexGrow(1, firstName, lastName);

		//formLayout.add(names);

		emailField = new TextField();
		emailField.setPlaceholder("Username / Email");
		emailField.getElement().setAttribute("name", "username"); //
		emailField.setWidthFull();


		PasswordField passwordField = new PasswordField();
		passwordField.setPlaceholder("Password");
		passwordField.getElement().setAttribute("name", "password"); //
		passwordField.setWidthFull();

		TextField userNameField = new TextField();
		userNameField.setPlaceholder("Username (Optional)");
		userNameField.setVisible(false);
		userNameField.setWidthFull();

		PasswordField confirmPasswordField = new PasswordField();
		confirmPasswordField.setPlaceholder("Confirm Password");
		//passwordField.getElement().setAttribute("name", "password"); //
		confirmPasswordField.setWidthFull();
		confirmPasswordField.setVisible(false);

		binder
		.forField(confirmPasswordField)
		.asRequired().withValidator((u,s) -> {
			if(u.equals(passwordField.getValue())) return ValidationResult.ok();
			else return ValidationResult.error("Passwords do not match");
		})
		.bind(User::getPassword, User::setPassword);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.CENTER);
		buttons.setMargin(false);

		Button submitButton = new Button("Login");
		submitButton.setId("submitbutton"); //
		buttons.add(submitButton);

		Button register = new Button("Register");
		buttons.add(register);
		register.addClickListener(click -> {

			if(submitButton.isEnabled()) {
				registerUser = new User();

				emailField.setPlaceholder("Email");

				binder
				.forField(emailField)
				.asRequired()
				.withValidator(new EmailValidator("Invalid Email Address"))
				.withValidator((s,c) -> {
					if(userRepo.findByEmail(s).isPresent()) return ValidationResult.error("Email Address already in use");
					else return ValidationResult.ok();
				})
				.bind(User::getEmail, User::setEmail);

				binder
				.forField(userNameField)
				//.asRequired()
				//.withValidator(new EmailValidator("Invalid Email Address"))
				.withValidator(new RegexpValidator("Invalid Username", "\\p{Alnum}++"))
				.withValidator((s,c) -> {
					if(userRepo.findByUserNameIgnoreCase(s).isPresent()) return ValidationResult.error("Username already in use");
					else return ValidationResult.ok();
				})
				.bind(User::getUserName, User::setUserName);


				submitButton.setEnabled(false);
				names.setVisible(true);
				confirmPasswordField.setVisible(true);
				userNameField.setVisible(true);
				register.setText("Register");
			}
			else {
				try {
					LOGGER.info("write bean");
					binder.writeBean(registerUser);
					try {
						userService.registerUser(registerUser);
					} catch (UserExistsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				catch (ValidationException e) {
					LOGGER.error("",e);
				}
				//User user = new User(firstName.getValue(),lastName.getValue(),emailField.getValue(),userNameField.getValue(),passwordField.getValue());
			}
		});

		formLayout.add(names,emailField, userNameField, passwordField, confirmPasswordField, buttons);
		//UI.getCurrent().getPage().executeJs(ROUTE, null)

		//FormLayout formLayout = new FormLayout(); //
		// formLayout.add(userNameTextField, passwordField, submitButton);

		this.setAlignItems(Alignment.CENTER);
		this.setJustifyContentMode(JustifyContentMode.CENTER);

		setClassName("login-view");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {

		LOGGER.info("on attach");

		UI.getCurrent().getPage().executeJs(
				"document.getElementById('submitbutton').addEventListener('click', () => document.getElementById('ironform').submit());"); //

		Element formElement = new Element("form"); //
		formElement.setAttribute("method", "post");
		formElement.setAttribute("action", "login");
		formElement.appendChild(formLayout.getElement());

		Element ironForm = new Element("iron-form"); //
		ironForm.setAttribute("id", "ironform");
		ironForm.setAttribute("allow-redirect", true); //
		ironForm.appendChild(formElement);

		getElement().appendChild(ironForm); //

		super.onAttach(attachEvent);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		LOGGER.info("beforeEnter");
		//login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
		if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
			LOGGER.info("setting error");
			emailField.setErrorMessage("Error");
			emailField.setValue("error");
		}
	}
}
