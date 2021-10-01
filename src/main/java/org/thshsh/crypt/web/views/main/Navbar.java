package org.thshsh.crypt.web.views.main;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.SpringVaadinApplication;
import org.thshsh.crypt.web.view.Breadcrumbs;
import org.thshsh.crypt.web.view.HomeView;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

@SuppressWarnings("serial")
@Component()
@Scope("prototype")
@CssImport("./styles/navbar.css")
public class Navbar extends VerticalLayout {
	
	@Autowired
	protected AppSession appSession;
	
	@Autowired
	protected Breadcrumbs breadcrumbs;
	
	@Autowired
	UserMenu userMenu;
	
	protected Span viewTitle;
	
	public Navbar() {}
	
	@PostConstruct
	public void postConstruct() {
		
		this.setPadding(false);
		this.setSpacing(false);
		

		HorizontalLayout navbar = new HorizontalLayout();
		this.add(navbar);
    	navbar.setWidthFull();
    	navbar.setId("header");
    	navbar.setSpacing(false);
    	navbar.setPadding(true);
    	navbar.setMargin(false);
    	navbar.setAlignItems(FlexComponent.Alignment.CENTER);
    	navbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
     
    	
    	VerticalLayout navbarleft = new VerticalLayout();
    	navbarleft.setPadding(false);
    	navbarleft.setSpacing(false);
    	
    	DrawerToggle drawerToggle = new DrawerToggle();
    	
    	navbar.add(navbarleft);
    	
		/* HorizontalLayout leftToggleAndTitle = new HorizontalLayout();
		 leftToggleAndTitle.setPadding(false);
		 
		 leftToggleAndTitle.setAlignItems(FlexComponent.Alignment.CENTER);
		 leftToggleAndTitle.setMargin(false);
		 navbarleft.add(leftToggleAndTitle);
		 
		 leftToggleAndTitle.add(new DrawerToggle());
		 viewTitle = new Span();
		 viewTitle.addClassName("h2");
		 
		 leftToggleAndTitle.add(viewTitle);*/
          
          HorizontalLayout bread = new HorizontalLayout();
          bread.setAlignItems(Alignment.START);
          bread.setJustifyContentMode(JustifyContentMode.CENTER);
          
          bread.addClassName("breadcrumb-layout");
          //bread.setPadding(true);
          
          bread.add(drawerToggle);
			/*  
			  RouterLink home = new RouterLink("",DashboardView.class);
			  home.addClassName("home");
			  home.addComponentAsFirst(MainLayout.HOME_ICON.create());
			  bread.add(home);
			  bread.add(breadcrumbs);*/
          
          navbarleft.add(bread);
          
          H1 title = new H1(SpringVaadinApplication.APP_NAME);
          bread.add(title);
          
          
          VerticalLayout right = new VerticalLayout();
          right.setAlignItems(Alignment.CENTER);
          right.setPadding(true);
          
          right.setSpacing(true);
          
         // rightV.add(right);
          navbar.add(right);
          right.addClassName("right");
          right.setAlignItems(Alignment.END);
          
          right.add(userMenu);
          
			/*HorizontalLayout userMenu = new HorizontalLayout();
			userMenu.setPadding(false);
			userMenu.setAlignItems(Alignment.CENTER);
			right.add(userMenu);
			
			 
			Button userMenuButton = new Button(appSession.getUser().getDisplayName(), VaadinIcon.USER.create());
			userMenuButton.setIconAfterText(true);
			userMenuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			userMenuButton.addClassName("profile");
			 //Icon userIcon = VaadinIcon.USER.create();
			 //userIcon.addClassName("clickable");
			 ContextMenu cm = new ContextMenu(userMenuButton);
			 cm.setOpenOnClick(true);
			
			 
			 if(appSession.getPrimaryRole()!=null) {
			  //primaryRole.setext(appSession.getPrimaryRole().getName());
			  
			  Span primaryRole = new Span();
			     if(appSession.getPrimaryRole()!=null)primaryRole.setText(appSession.getPrimaryRole().getName());
			     primaryRole.addClassName("role"); 
			  
			  MenuItem mi = cm.addItem(primaryRole);
			  mi.getElement().setAttribute("class", "role");
			  //mi.addClassName("role");
			  
			 }
			 
			 Button profileButton = new Button("Profile");
			 profileButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			 profileButton.addClickListener(click -> {
			 });
			 MenuItem profileItem = cm.addItem(profileButton);
			 
			
			 Button b = new Button("Sign out");
			 b.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			 b.addClickListener(click -> {
			  UI.getCurrent().getPage().setLocation(logoutUrl);
			 });
			 MenuItem logoutItem = cm.addItem(b);
			 
			 userMenu.add(userMenuButton,cm);
			 */
          
          
          HorizontalLayout lowerbread = new HorizontalLayout();
          lowerbread.addClassName("breadcrumb-holder");
          lowerbread.setWidthFull();
          lowerbread.setPadding(true);
          this.add(lowerbread);
          
          
          RouterLink home = new RouterLink("",HomeView.class);
          home.addClassName("home");
          home.addComponentAsFirst(HomeView.ICON.create());
          lowerbread.add(home);
          lowerbread.add(breadcrumbs);
		
	}
	
	public void setTitle(String text) {
		//viewTitle.setText(text);
	}

}
