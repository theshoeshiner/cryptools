package org.thshsh.crypt.web.view;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

@SuppressWarnings("serial")
public class RouteNotFoundErrorPage extends ApplicationErrorPage implements HasErrorParameter<NotFoundException>{

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
		setErrorParameterInternal("Page Not Found",event, parameter);
		return HttpServletResponse.SC_NOT_FOUND;
	}

}
