package org.thshsh.crypt.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

@SuppressWarnings("serial")
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

	public static final Logger LOGGER = LoggerFactory.getLogger(ConfigureUIServiceInitListener.class);
	
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
		//TODO dont need this
		if(appConfig.getLogin()) {
			
			//LOGGER.info("getNavigationTarget: {}",event.getNavigationTarget());
			
			//List<RouteData> routes = RouteConfiguration.forSessionScope().getAvailableRoutes();
			//LOGGER.info("routes: {}",routes);
			
			/*if (!LoginFormView.class.equals(event.getNavigationTarget())
			    && !SecurityUtils.isUserLoggedIn()) {
				//event.rerouteTo("login");
				//event.rerouteTo(LoginFormView.class);
				event.forwardTo(LoginFormView.class);
			}*/
		}
	}
}