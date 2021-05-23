package org.thshsh.crypt.web.view;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;

@CssImport("./styles/breadcrumbs.css") 
@UIScope
@Component
public class Breadcrumbs extends HorizontalLayout {
	
	List<Breadcrumb> breadcrumbs;

	public Breadcrumbs() {
		breadcrumbs = new LinkedList<>();
		this.addClassName("breadcrumbs");
		this.setMargin(false);
		//RouterLink home = new RouterLink("Home",HomeView.class);
		//RouterLink middle = new RouterLink("Middle",AboutView.class);
		//RouterLink end = new RouterLink("End",FlowsView.class);
		/*
		this.add(home);
		this.addSeparator();
		this.add(middle);
		this.addSeparator();
		this.add(end);*/
		
	}
	
	public void setBreadcrumbs() {
		
	}
	
	public Breadcrumbs resetBreadcrumbs() {
		this.removeAll();
		this.breadcrumbs.clear();
		return this;
	}
	

	public Breadcrumbs addBreadcrumb(String text,Class<? extends com.vaadin.flow.component.Component> view) {
		
		
		
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
		this.add(new Span("/"));
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
