package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login")
@Route(value = "reallogin")
public class RealLoginView extends LoginOverlay {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(RealLoginView.class);
	
    public RealLoginView() {
        setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        //i18n.getHeader().setTitle("My App");
        //i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);
        
        

        setForgotPasswordButtonVisible(true);
        setOpened(true);
    }
    
    
    @PostConstruct
	public void postConstruct() {
		LOGGER.info("postConstruct");
		
		//Span header = new Span("My Header");
		//add(header);
		
	}

}
