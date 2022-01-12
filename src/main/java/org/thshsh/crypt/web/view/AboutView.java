package org.thshsh.crypt.web.view;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "about", layout = MainLayout.class)
@PageTitle("About")
@SecuredByFeatureAccess(feature=Feature.System,access=Access.Super)
public class AboutView extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(AboutView.class);

	//@Autowired
	//LdapTemplate ldapTemplate;

	//@Autowired
	//LdapUserRepository ldapRepo;

	@Autowired
	Breadcrumbs breadcrumbs;

	@Autowired
	JavaMailSender mailSender;

	//@Value("${ldap.user.base}")
	//String ldapUserBase;

    public AboutView() {

    }

    @PostConstruct
    public void postConstruct() {
		breadcrumbs.resetBreadcrumbs().addBreadcrumb("Home", HomeView.class).addBreadcrumb("About", AboutView.class);

		H1 h1 = new H1("This is an H1");
		add(h1);

		H3 h3 = new H3("This is an H3");
		add(h3);

		Button smallbutton = new Button("Small Primary");
		smallbutton.addThemeName("primary");
		smallbutton.addThemeName("small");
		add(smallbutton);

		Button button = new Button("Primary");
		button.addThemeName("primary");
		add(button);

		Button noclick = new Button("Unclickable");
		noclick.addClassName("unclickable");
		add(noclick);

		Button sendMail = new Button("Send mail",click -> {
			 SimpleMailMessage message = new SimpleMailMessage();
		        message.setFrom("cryptools@thshsh.org");
		        message.setTo("dcwatson84@gmail.com");
		        message.setSubject("Auto Test: "+System.currentTimeMillis());
		        message.setText("test");
		        mailSender.send(message);
		});
		add(sendMail);

		/*List<LdapUser> found = search("ab");
		LOGGER.info("found: {}",found);
		found.forEach(user -> {
			LOGGER.info("user: {}",user.getDisplayName());
		});*/

		/*ExampleFilterDataProvider<LdapUser,String> dataProvider = new ExampleFilterDataProvider<>(
				ldapRepo,
				ExampleMatcher.matchingAny()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
				.withIgnoreCase().withIgnoreNullValues()
				, QuerySortOrder.asc("surName").build()
				);*/

		//LdapUserMapper lum = new LdapUserMapper();

		//SimpleLdapRepository<LdapUser> slr = new SimpleLdapRepository<>(ldapTemplate, new DefaultObjectDirectoryMapper(), LdapUser.class);

		//DataProvider<LdapUser,LdapUser> dp = dataProvider;


		/*
				ComboBox<LdapUser> userSearch = new ComboBox<LdapUser>("User");
				userSearch.setWidth("250px");
				userSearch.setPlaceholder("User Name");
				userSearch.setPageSize(150);
				userSearch.setItemLabelGenerator(user -> {
					return user.getDisplayName();
				});
				userSearch.addValueChangeListener(change -> {

				});
				userSearch.setClearButtonVisible(true);

				//MessageFormat.format(ldapUserBase, null);

				userSearch.setItems(query -> {
					int pageSize = query.getPageSize();
					query.getPage();
					if(!query.getFilter().isPresent() || query.getFilter().get().length() == 0) {
						return Collections.EMPTY_LIST.stream();
					}
					LdapQuery q = LdapQueryBuilder
							.query()
							.base(ldapUserBase)
							.countLimit(pageSize)
							.filter("(cn=*{0}*)", query.getFilter().get());

					List<LdapUser> users = slr.findAll(q);

					Collections.sort(users, (u0,u1) -> {
						return u0.getDisplayName().toLowerCase().compareTo(u1.getDisplayName().toLowerCase());
					});
					return users.stream();
				});

				add(userSearch);*/
	}


}
