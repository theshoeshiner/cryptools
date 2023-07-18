package org.thshsh.crypt.web.view;

import javax.servlet.http.HttpServletResponse;

import org.thshsh.crypt.web.security.UnauthorizedException;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

@SuppressWarnings("serial")
@Tag(Tag.DIV)
public class UnauthorizedExceptionView extends Component implements HasErrorParameter<UnauthorizedException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,ErrorParameter<UnauthorizedException> parameter) {
		/*getElement().setText("Could not navigate to '"
		            + event.getLocation().getPath()
		            + "'");*/
    	event.forwardTo(HomeView.class);
    	//event.forwardTo(HomeView.class);
    	//event
    	//UI.getCurrent().navigate("/dashboard", QueryParameters.empty());
        return HttpServletResponse.SC_NOT_FOUND;
        
    }
}