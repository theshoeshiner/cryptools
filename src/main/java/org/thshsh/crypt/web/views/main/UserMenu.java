package org.thshsh.crypt.web.views.main;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.view.UserDialog;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.UIScope;

@SuppressWarnings("serial")
@UIScope
@Component
public class UserMenu extends HorizontalLayout {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMenu.class);

	
	@Autowired
	AppSession appSession;
	
	@Autowired
	ApplicationContext context;


	@Autowired
	TaskExecutor executor;

	@PostConstruct
	public void postConstruct() {
		
         this.setPadding(false);
         this.setAlignItems(Alignment.CENTER);
          
         Icon i = VaadinIcon.USER.create();
         i.addClassName("profile");
         Button userMenuButton = new Button(appSession.getUser().getDisplayName(), i);
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
          

         Button profile = new Button("Profile");
         profile.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
         profile.addClickListener(click -> {        	  
       	  //UI.getCurrent().getPage().setLocation("/logout");
        	
         });
         MenuItem mi = cm.addItem(profile);
         mi.addClickListener(click -> {
        	 UserDialog ud = context.getBean(UserDialog.class,appSession.getUser(),true);
        	 ud.open();
         });
          
          Button b = new Button("Sign out");
          b.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
         
          MenuItem signout = cm.addItem(b);
          signout.addClickListener(click -> {        	  
        	  UI.getCurrent().getPage().setLocation("/logout");
          });
          
          add(userMenuButton,cm);
		
	}

}
