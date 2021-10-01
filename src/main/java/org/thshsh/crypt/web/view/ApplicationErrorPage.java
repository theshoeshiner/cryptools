package org.thshsh.crypt.web.view;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;


@SuppressWarnings("serial")
@ParentLayout(MainLayout.class)
@PageTitle("Error")
@CssImport("./styles/error-view.css")
public class ApplicationErrorPage extends VerticalLayout 
//implements HasErrorParameter<RuntimeException> 
{
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationErrorPage.class);
	
	public static final String HEADER = "Oops! Something went wrong...";

	@Autowired
	ApplicationContext context;
	
	@Autowired
	AppSession session;
	
	@PostConstruct
	public void postConstruct() {
		/*this.addClassName("error-page");
		this.setAlignItems(Alignment.CENTER);
		HorizontalLayout header = new HorizontalLayout();
		header.addClassName("header");
		H1 h1 = new H1();
		h1.add(VaadinIcon.EXCLAMATION_CIRCLE.create());
		h1.add(HEADER);
		header.add(h1);
		this.add(header);*/
	}
	

    public int setErrorParameterInternal(String header,BeforeEnterEvent event,ErrorParameter<?> parameter) {
    	
    	LOGGER.info("session: {}",session);
    	
    	String exceptionReference = ""+(System.currentTimeMillis()/1000);
    	Exception exception = parameter.getCaughtException();
    	LOGGER.error("Unhandled Exception: {}",exceptionReference,exception);
    	
    	ErrorLayout ed = context.getBean(ErrorLayout.class,header,exception,exceptionReference);
    	ed.setMargin(true);
    	ed.verticalScrollLayout.setHeightFull();
    	add(ed);
    	

    	return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
    
  
}