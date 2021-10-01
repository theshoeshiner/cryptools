package org.thshsh.crypt.web.views.main;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.web.AppSession;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class UserMenu extends HorizontalLayout {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMenu.class);

	
	@Autowired
	AppSession appSession;
	

	@Autowired
	TaskExecutor executor;

	@PostConstruct
	public void postConstruct() {
		
         this.setPadding(false);
         this.setAlignItems(Alignment.CENTER);
          

         Button userMenuButton = new Button(appSession.getUser().getDisplayName(), VaadinIcon.USER.create());
         userMenuButton.setIconAfterText(true);
         userMenuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
         userMenuButton.addClassName("profile");
         ContextMenu cm = new ContextMenu(userMenuButton);
         cm.setOpenOnClick(true);

          
			/* if(appSession.getPrimaryRole()!=null) {
			  Span primaryRole = new Span();
			     if(appSession.getPrimaryRole()!=null)primaryRole.setText(appSession.getPrimaryRole().getName());
			     primaryRole.addClassName("role"); 
			  MenuItem mi = cm.addItem(primaryRole);
			  mi.getElement().setAttribute("class", "role");
			  
			 }*/
          
	      //TODO user profile menu
			/*  Button profileButton = new Button("Profile");
			  profileButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			  profileButton.addClickListener(click -> {
			  });
			  MenuItem profileItem = cm.addItem(profileButton);*/
          

          
          Button b = new Button("Sign out");
          b.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
          b.addClickListener(click -> {        	  
        	  UI.getCurrent().getPage().setLocation("/saml/logout");
          });
          cm.addItem(b);
          
          add(userMenuButton,cm);
		
	}

}
