package org.thshsh.crypt.web.views.main;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Contact;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.web.AppConfiguration;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.crypt.web.SpringVaadinApplication;
import org.thshsh.crypt.web.security.SecurityUtils;
import org.thshsh.crypt.web.view.AboutView;
import org.thshsh.crypt.web.view.ActivityView;
import org.thshsh.crypt.web.view.ContactDialog;
import org.thshsh.crypt.web.view.ContactsView;
import org.thshsh.crypt.web.view.CurrenciesView;
import org.thshsh.crypt.web.view.DarkModeButton;
import org.thshsh.crypt.web.view.ExchangesView;
import org.thshsh.crypt.web.view.HomeView;
import org.thshsh.crypt.web.view.MarketRatesView;
import org.thshsh.crypt.web.view.PortfolioAlertsView;
import org.thshsh.crypt.web.view.PortfoliosView;
import org.thshsh.crypt.web.view.SystemView;
import org.thshsh.crypt.web.view.TaxReportView;
import org.thshsh.crypt.web.view.TestingView;
import org.thshsh.crypt.web.view.TitleSpan;
import org.thshsh.crypt.web.view.UsersView;
import org.thshsh.vaadin.ClickableAnchor;
import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics;
import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics.LogLevel;
import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics.SendMode;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("./styles/application.css")
@CssImport("./styles/global.css")
@CssImport("./styles/theme-lumo.css")
@CssImport("./styles/menu.css")
@CssImport("./styles/vaadin-grid.css")
@CssImport("./styles/link.css")
@CssImport("./styles/login.css")
@CssImport("./styles/toggle-button.css")
@CssImport("./styles/dashboard.css")
@CssImport("./styles/grid-buttons.css")
@CssImport("./styles/confirm-dialog.css")
@CssImport(value = "./styles/components/vaadin-grid.css",themeFor = "vaadin-grid")
@CssImport(value = "./styles/components/vaadin-checkbox.css",themeFor = "vaadin-checkbox")
@CssImport(value = "./styles/components/vaadin-button.css",themeFor = "vaadin-button")
@CssImport(value = "./styles/components/vaadin-textfield.css",themeFor = "vaadin-textfield")
@CssImport(value = "./styles/components/vaadin-textfield.css",themeFor = "vaadin-text-field")
@CssImport(value = "./styles/components/vcf-popup.css",themeFor = "vcf-popup")
@CssImport(value = "./styles/components/vcf-popup.css",themeFor = "vcf-popup-overlay")
@CssImport(value = "./styles/components/vcf-toggle-button.css", themeFor = "vcf-toggle-button")
@UIScope
@EnableGoogleAnalytics(value = "UA-114384488-4",debugMode = false,sendMode = SendMode.ALWAYS,devLogging = LogLevel.TRACE,productionLogging = LogLevel.TRACE)
public class MainLayout extends AppLayout {

	protected static final Logger LOGGER = LoggerFactory.getLogger(MainLayout.class);

	public static final String TITLE_PREFIX = "Cryptools";

    private VerticalLayout menu;
    private Span viewTitle;

    @Autowired
    GenericWebApplicationContext context;

    @Autowired
    AppSession appSession;
    
    @Autowired
    AppConfiguration config;

    //@Autowired
    //Breadcrumbs breadcrumbs;
    //String themeVariant = Material.LIGHT;

    @Autowired
    Navbar navbar;
    
    public MainLayout() {

    	LOGGER.info("MainLayout()");

    }

    @PostConstruct
    public void postConstruct() {
    	setPrimarySection(Section.DRAWER);

        //VerticalLayout navbar = new VerticalLayout();

       // navbar.add(createHeaderContent());

        //Breadcrumbs bc = new Breadcrumbs();
        //HorizontalLayout test = new HorizontalLayout();
        //test.add(new Span("Home"),new Span("/"),new Span("Here"));
        //navbar.add(breadcrumbs);

    	LOGGER.info("navbar: {}",navbar);
        addToNavbar(true, navbar);



        menu = createMenu();
        addToDrawer(createDrawerContent(menu));


    }

    private Component createHeaderContent() {

    	ThemeList tl = UI.getCurrent().getElement().getThemeList();
    	LOGGER.info("themelist: {}",tl);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setId("header");
        layout.getThemeList().set(SpringVaadinApplication.THEME_VARIANT, true);
        //layout.setWidthFull();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        //layout.setJustifyContentMode(JustifyContentMode.);

        HorizontalLayout left = new HorizontalLayout();
        left.setAlignItems(FlexComponent.Alignment.CENTER);
        left.setMargin(false);
        layout.add(left);

        left.add(new DrawerToggle());
        viewTitle = new Span();
        viewTitle.addClassName("h2");

        left.add(viewTitle);

        //layout.add(new Image("images/user.svg", "Avatar"));


		/*Icon changeTheme = VaadinIcon.MOON.create();
		changeTheme.addClickListener(change -> {

		});*/

		/*Button theme = new Button(VaadinIcon.MOON.create());
		theme.addClickListener(click -> {
			if(themeVariant.equals(Lumo.LIGHT)) {
				getUI().get().getPage().executeJs("document.documentElement.setAttribute(\"theme\",\"dark\")");
				themeVariant = Lumo.DARK;
				theme.setIcon(VaadinIcon.SUN_O.create());
				theme.getElement().setProperty("title", "Light Mode");
			}
			else {
				getUI().get().getPage().executeJs("document.documentElement.setAttribute(\"theme\",\"light\")");
				themeVariant = Lumo.LIGHT;
				theme.setIcon(VaadinIcon.MOON.create());
				theme.getElement().setProperty("title", "Dark Mode");
			}



		});
		theme.addClassName("large");
		theme.addClassName("icon-only");
		layout.add(theme);*/

       // layout.add(new Span(appSession.getUser().getDisplayName()));


        DarkModeButton theme = new DarkModeButton();
        layout.add(theme);


		/*  ToggleButton tb = new ToggleButton("Mode");

		layout.add(tb);*/

        //layout.set

        return layout;
    }

    private Component createDrawerContent(VerticalLayout menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setWidth("100%");

        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        //Icon logo = VaadinIcon.DATABASE.create();
        //logoLayout.add(logo);


        TitleSpan title = new TitleSpan();
        title.addClassName("h2");


        logoLayout.add(title);
        layout.add(logoLayout, menu);
        return layout;
    }

    private VerticalLayout createMenu() {



        VerticalLayout tabs = new VerticalLayout();
        tabs.setPadding(true);
        tabs.setMargin(true);
        tabs.setSpacing(false);
		/* tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);*/
        //tabs.setId("tabs");
        tabs.addClassName("menu");
        //tabs.add(createMenuItems());
        tabs.add((Component[]) createMenuItems().toArray(new Component[] {}));
        return tabs;

		/*
		final Tabs tabs = new Tabs();
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
		tabs.setId("tabs");
		tabs.add(createMenuItems());
		return tabs;*/
    }

    private List<Component> createMenuItems() {

    	List<Component> items = new ArrayList<>();

    	RouterLink home = createMenuItem(VaadinIcon.DASHBOARD.create(),HomeView.TITLE, HomeView.class);
    	//RouterLink browse = createMenuItem(VaadinIcon.FILE_TREE.create(),MetaDataBrowser.TITLE, MetaDataBrowser.class);
    	//RouterLink form = createMenuItem(VaadinIcon.FILE_TREE.create(),FormMetaDataBrowserView.TITLE, FormMetaDataBrowserView.class);

    	items.add(home);


    	
    	
    	
		/*if(appSession.hasAccess(Feature.User, Access.Read)) {
			items.add(createMenuItem(VaadinIcon.CLOUD_DOWNLOAD.create(),"Data Flows", FlowsView.class));
		}*/
    	//items.add(flows);
		/*if(appSession.hasAccess(Feature.Profile, Access.Read)) {
			RouterLink profiles = createMenuItem(VaadinIcon.FILE_TABLE.create(),ProfileConfigurationsView.TITLE, ProfileConfigurationsView.class);
			items.add(profiles);
		}*/


    	//items.add(createMenuItem(VaadinIcon.TABLE.create(),"Studies", MdmStudiesView.class));

		/*if(appSession.hasAccess(Feature.User, Access.ReadWrite)) {
			items.add(createMenuItem(VaadinIcon.USERS.create(),"User Administration", UsersView.class));
		}
		*/
    	//RouterLink mdm = createMenuItem("MDM", MdmTestView.class);
    	//RouterLink load = createMenuItem(LoadTestView.PAGE_TITLE, LoadTestView.class);


    	//items.add(createMenuItem(VaadinIcon.TOOLS.create(),"Balances", BalancesView.class));

    	if(SecurityUtils.hasAccess(Feature.Portfolio, Access.Read)) {
    		items.add(createMenuItem(VaadinIcon.CHART_GRID.create(),"Portfolios", PortfoliosView.class));
    	}
    	
    	if(SecurityUtils.hasAccess(Feature.Portfolio, Access.Super)) {
    		items.add(createMenuItem(VaadinIcon.BELL.create(),"Alerts", PortfolioAlertsView.class));
    	}
    	
    	if(SecurityUtils.hasAccess(Feature.Exchange, Access.ReadWrite)) {
    		items.add(createMenuItem(VaadinIcon.INSTITUTION.create(),"Exchanges", ExchangesView.class));
    	}

    	if(SecurityUtils.hasAccess(Feature.Currency, Access.ReadWrite)) {
    		items.add(createMenuItem(VaadinIcon.MONEY.create(),"Currencies", CurrenciesView.class));
    		items.add(createMenuItem(VaadinIcon.CHART_LINE.create(),"Market Rates", MarketRatesView.class));
    	}
    	
    	if(SecurityUtils.hasAccess(Feature.User, Access.ReadWrite)) items.add(createMenuItem(VaadinIcon.USERS.create(),"Users", UsersView.class));
 
		/*Tab home = createTab("Home", HomeView.class);
		Tab browse = createTab("Browser", MetaDataBrowser.class);
		Tab about = createTab("About", AboutView.class);
		Tab studies = createTab("Studies", StudiesView.class);
		Tab spon = createTab("Sponsors", SponsorsView.class);
		Tab load = createTab(LoadTestView.PAGE_TITLE, LoadTestView.class);*/

    	//TestingView

    	if(SecurityUtils.hasAccess(Feature.System, Access.Read)) {
    		
    		items.add(createMenuItem(VaadinIcon.CURSOR.create(),"Activity", ActivityView.class));

    		items.add(createMenuItem(VaadinIcon.COG.create(),"System", SystemView.class));
    		
    		items.add(createMenuItem(VaadinIcon.MONEY.create(),"Tax Report", TaxReportView.class));
    		
    		items.add(createMenuItem(VaadinIcon.ENVELOPE.create(),"Contacts", ContactsView.class));
    		

    	}
    	
    	
    	ClickableAnchor contactUs = new ClickableAnchor("","Contact Us");
    	contactUs.getElement().setAttribute("router-link", "");
    	contactUs.addClassName("h3");
    	contactUs.addComponentAsFirst(VaadinIcon.ENVELOPE.create());
    	contactUs.addClickListener(click -> {
    		  Contact c = new Contact();
        	  c.setUser(appSession.getUser());
        	  c.setCreated(ZonedDateTime.now());
        	  c.setAnswered(false);
        	  ContactDialog cd = context.getBean(ContactDialog.class,c);
        	  cd.open();
    	});
    	
    	items.add(contactUs);
    	
    	
    	
    	if(SecurityUtils.hasAccess(Feature.System, Access.Read) && !config.getProductionMode()) {

    		
    		//TODO REMOVE
    		
    		items.add(createMenuItem(VaadinIcon.FLASK.create(),"Test", TestingView.class));
	
	    	items.add(createMenuItem(VaadinIcon.TOOLS.create(),"Component Test", AboutView.class));
    	
	    	
    	}

    	return items;

	
    }

    private static RouterLink createMenuItem(Icon icon,String text, Class<? extends Component> navigationTarget) {
        //final Tab tab = new Tab();
    	RouterLink link = new RouterLink(null, navigationTarget);
    	link.addClassName("h3");
    	if(icon != null) {
    		icon.setSize("20px");
    		link.add(icon);
    	}
    	//link.setText(text);
    	link.add(text);

        ComponentUtil.setData(link, Class.class, navigationTarget);
        return link;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
       // getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        //viewTitle.setText(getCurrentPageTitle());
        navbar.setTitle(getCurrentPageTitle());
        
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
    	return "";
        //return getContent().getClass().getAnnotation(PageTitle.class).value();
    }

    //TODO figure out a way to register this instance as a UIScoped bean
    public static MainLayout getInstance() {
        return (MainLayout) UI.getCurrent().getChildren()
              .filter(component -> component.getClass() == MainLayout.class).findFirst().orElse(null);
     }

    @Override
    public void setDrawerOpened(boolean drawerOpened) {
    	this.setDrawerOpened(drawerOpened, false);
    }

    Boolean lastDrawerUserState = null;

	public void setDrawerOpened(boolean drawerOpened, boolean auto) {
		if(this.isDrawerOpened() != drawerOpened) {
			//auto changed state
			if(auto) lastDrawerUserState = this.isDrawerOpened();
			super.setDrawerOpened(drawerOpened);
		}

	}

    public void undoDrawerAutoState() {
    	if(lastDrawerUserState != null) {
    		super.setDrawerOpened(lastDrawerUserState);
    		lastDrawerUserState = null;
    	}
    }
}
