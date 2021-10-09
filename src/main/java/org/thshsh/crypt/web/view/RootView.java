package org.thshsh.crypt.web.view;

import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "", layout = MainLayout.class)
@PageTitle(HomeView.TITLE)
public class RootView extends Span implements BeforeEnterObserver, AfterNavigationObserver {

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		UI.getCurrent().navigate(HomeView.class);
		
	}

}
