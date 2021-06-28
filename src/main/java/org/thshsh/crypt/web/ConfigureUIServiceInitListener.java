package org.thshsh.crypt.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.web.view.LoginView;

@SuppressWarnings("serial")
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

	@Autowired
	AppConfiguration appConfig;

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(uiEvent -> {
			final UI ui = uiEvent.getUI();
			ui.addBeforeEnterListener(this::beforeEnter);
		});
	}

	/**
	 * Reroutes the user if (s)he is not authorized to access the view.
	 *
	 * @param event
	 *            before navigation event with event details
	 */
	private void beforeEnter(BeforeEnterEvent event) {
		if(appConfig.loginEnabled) {
			if (!LoginView.class.equals(event.getNavigationTarget())
			    && !SecurityUtils.isUserLoggedIn()) {
				event.rerouteTo(LoginView.class);
			}
		}
	}
}