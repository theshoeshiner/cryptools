package org.thshsh.crypt.web.view;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;

@SuppressWarnings("serial")
@CssImport("./styles/breadcrumbs.css") 
@UIScope
@Component
public class Breadcrumbs extends HorizontalLayout {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Breadcrumbs.class);
	
	List<Breadcrumb> breadcrumbs;

	public Breadcrumbs() {
		super();
		breadcrumbs = new LinkedList<>();
		this.addClassName("breadcrumbs");
		this.setMargin(false);
		this.setAlignItems(Alignment.CENTER);
		this.setSpacing(false);
		
		LOGGER.info("created breadcrumbs: {}",this);
		
	}
	
	public void setBreadcrumbs() {
		
	}
	
	public Breadcrumbs resetBreadcrumbs() {
		this.removeAll();
		this.breadcrumbs.clear();
		return this;
	}
	

	public Breadcrumbs addBreadcrumb(String text,Class<? extends com.vaadin.flow.component.Component> view) {
		
		LOGGER.info("addBreadcrumb: {}",text);
		
		Breadcrumb bc = new Breadcrumb(text, view);
		if(this.breadcrumbs.size()>0) this.addSeparator();
		breadcrumbs.add(bc);
		if(view != null) {
			RouterLink link = new RouterLink(text,view);
			this.add(link);
		}
		else {
			Span link = new Span(text);
			this.add(link);
		}
		return this;
	}
	
	public void addSeparator() {
		Span sep = new Span("/");
		sep.addClassName("separator");
		this.add(sep);
		
		/*Span sep = new Span();
		sep.addClassName("separator");
		sep.addClassName("slash");
		this.add(sep);*/
	}
	
	public static class Breadcrumb {
		Class<? extends com.vaadin.flow.component.Component> view;
		String text;
		public Breadcrumb(String text, Class<? extends com.vaadin.flow.component.Component> view) {
			super();
			this.text = text;
			this.view = view;
		}
		
	}
	
}
