package org.thshsh.crypt.web;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.User;
import org.thshsh.crypt.repo.UserRepository;
import org.thshsh.crypt.web.security.CryptUserPrincipal;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

import ch.carnet.kasparscherrer.VerticalScrollLayout;

@Component
@VaadinSessionScope
public class AppSession {

	public static final Logger LOGGER = LoggerFactory.getLogger(AppSession.class);

	@Autowired
	UserRepository userRepo;

	@Autowired
	AppConfiguration appConfig;

	User user;
	Map<Feature,Access> permissionsMap;



	@PostConstruct
	protected void postConstruct() {

		if(appConfig.getLogin()) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			LOGGER.info("Authentication: {}",authentication);
			LOGGER.info("Authentication: {}",authentication.getPrincipal());
			CryptUserPrincipal p = (CryptUserPrincipal) authentication.getPrincipal();
			user = p.getUser();
			user = userRepo.findById(user.getId()).get();
		}
		else {
			user = userRepo.findByUserNameIgnoreCase(appConfig.getUsername()).orElseThrow(() -> new ApplicationException("No user configured"));
		}

		LOGGER.info("ui: {}",UI.getCurrent());
		/*LOGGER.info("tracker: {}",GoogleAnalyticsTracker.getCurrent());
		GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getCurrent();
		
		//tracker.ga("set",null, "userId", user.getId().toString());
		
		//gtag('set', {'user_id': 'USER_ID'}); // Set the user ID using signed-in user_id.
		
		LOGGER.info("config: {}",GoogleAnalyticsTracker.getCurrent().getConfiguration());*/
		/* tracker.addInitListener(t -> {
			//have to use an init listener because we need the tag id from the config
			JsonObject jo = new JreJsonFactory().createObject();
			jo.put("user_id", user.getId().toString());
			tracker.sendEvent("config", tracker.getConfiguration().getTagId(),jo );
		});*/
		
		//JsonObject jo = new JreJsonFactory().createObject();
		//jo.put("ct_user_id", user.getId().toString());
		//tracker.sendEvent("set", "user_properties", jo);
		//tracker.set("user_id",user.getId().toString());
		
		
		
		
		
		
		//gtag('config', 'G-XXXXXXXX', {'user_id': 'USER_ID'});
	    //gtag('set', 'user_properties', { 'crm_id' : 'USER_ID' });


		//REMOVE
		/*user = userRepo.findAll().stream().findFirst().orElseGet(() -> {
			User u = new User("John","Doe","jd@email.com",null,null);
			userRepo.save(u);
			return u;
		});*/


		//permissionsMap = user.getPermissionsMap();

		VaadinSession.getCurrent().setErrorHandler(error -> {
			LOGGER.error("Vaadin error",error.getThrowable());
			//Notification.show("There was an error: "+error.getThrowable().getMessage());

			/*Notification notification = new Notification("error");
			notification.setDuration(3000);
			notification.open();*/

			//TODO only show stack trace and full error when user is developer?

			Notification notification = new Notification();


			//notification.setText("Internal Error: "+error.getThrowable().getMessage());
			notification.setPosition(Position.MIDDLE);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			notification.addThemeName("error");


			VerticalLayout layout = new VerticalLayout();
			notification.add(layout);
			layout.setWidth("800px");
			layout.setMargin(false);
			layout.setPadding(false);

			HorizontalLayout headerLayout = new HorizontalLayout();
			headerLayout.setWidthFull();
			headerLayout.setMargin(false);
			headerLayout.setSpacing(false);
			headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
			layout.add(headerLayout);

			H3 header = new H3("Internal Error: "+error.getThrowable().getMessage());
			headerLayout.add(header);

			Button dismiss= new Button(VaadinIcon.CLOSE.create(),click -> {
				notification.setOpened(false);
			});
			headerLayout.add(dismiss);

			Details component = new Details();
			component.addThemeName("stacktrace");
			component.setSummaryText("Stacktrace...");


			String trace = ExceptionUtils.getStackTrace(error.getThrowable());
			VerticalScrollLayout verticalScrollLayout = new VerticalScrollLayout();
			verticalScrollLayout.addClassName("stacktrace");


			verticalScrollLayout.add(new Text(trace));
			verticalScrollLayout.setHeight("400px");
			verticalScrollLayout.setWidthFull();


			component.addContent(verticalScrollLayout);
			layout.add(component);



			notification.open();

		});
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	public static User getCurrentUser() {
		return getCurrent().getUser();
	}
	
	public static AppSession getCurrent() {
		return ApplicationContextService.getApplicationContext().getBean(AppSession.class);
	}

	public Boolean hasAccess(Feature feature, Access ac) {
		if(user == null) return false;
		Access has = user.getPermissionsMap().get(feature);
		Boolean r = ac.isLessThanOrEqual(has);
		return r;
	}

	public void refresh() {
		user = userRepo.findById(user.getId()).get();
		user.getPermissionsMap();
	}

}
