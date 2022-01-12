package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.web.AppConfiguration;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.ApplicationException;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ch.carnet.kasparscherrer.VerticalScrollLayout;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ErrorLayout extends VerticalLayout {
	
	@Autowired
	AppSession session;

	@Autowired
	AppConfiguration config;
	
	String message;
	Throwable caught;
	String reference;
	VerticalScrollLayout verticalScrollLayout;
	
	public ErrorLayout(String message,Throwable t,String ref) { 
		this.message = message;
		this.caught = t;
		this.reference = ref;
		
		
	}
	@PostConstruct
	public void postConstruct() {
		
		this.setAlignItems(Alignment.CENTER);
		
		
		this.addClassName("error-layout");
		this.setAlignItems(Alignment.CENTER);
		HorizontalLayout header = new HorizontalLayout();
		header.addClassName("header");
		H1 h1 = new H1();
		h1.add(VaadinIcon.EXCLAMATION_CIRCLE.create());
		h1.add(ApplicationErrorPage.HEADER);
		header.add(h1);
		this.add(header);
		
		ErrorDiv ed = new ErrorDiv(message,caught,reference);
		add(ed);
		
		if(!config.getProductionMode() ) {
		
    		String trace = "<span>"+ExceptionUtils.getStackTrace(caught)
    		.replaceAll("\\n", "<br/>")
    		.replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
    		+"</span>";
    		verticalScrollLayout = new VerticalScrollLayout();
    		verticalScrollLayout.addClassName("stacktrace");
    		Html h = new Html(trace);
    		verticalScrollLayout.add(h);
    		verticalScrollLayout.setHeight("300px");
    		verticalScrollLayout.setWidthFull();
    		add(verticalScrollLayout);
    		
		}
		
	}

	public static class ErrorDiv extends Div {
    	Span message;
    	Span reference;
    	public ErrorDiv(Throwable exception,String exceptionReference) {
    		this(null,exception,exceptionReference);
    	}
    	public ErrorDiv(String header,Throwable t,String exceptionReference) {
    		super();
    		Div errorDiv = this;
        	errorDiv.addClassName("error-details");
        	//this.add(message);
        	message = new Span();
        	message.addClassName("error-message");
        	errorDiv.add(header);
        	
        	if(header == null) {
	        	if(t instanceof ApplicationException) header = t.getMessage();
	        	else header = "An unexpected error occured";
        	}
        	message.add(header);
        	
        	reference = new Span("Reference "+exceptionReference);
        	reference.addClassNames("error-ref","helper-text");
        	errorDiv.add(reference);
    	}
    }
}