package org.thshsh.crypt.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.thshsh.crypt.CryptModel;
import org.thshsh.crypt.CryptmanModel;
import org.thshsh.crypt.web.view.portfolio.PortfolioSettingsDescriptor;
import org.thshsh.vaadin.entity.EntityGrid;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;

@SpringBootApplication(scanBasePackageClasses = {
		SpringVaadinApplication.class,
		CryptDataSourceConfiguration.class,
		CryptmanModel.class,
		CryptModel.class,
		EntityGrid.class,
},
exclude = ErrorMvcAutoConfiguration.class)
@Theme(themeClass = Lumo.class, variant = SpringVaadinApplication.THEME_VARIANT)
@PWA(name = "Cryptools", shortName = "Cryptools",iconPath = "icons/cryptools-icon.png")
@Push()
public class SpringVaadinApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	public static final String THEME_VARIANT = Material.LIGHT;

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringVaadinApplication.class);

	public static final String APP_NAME = "";
	

	/*public static void main(String[] args) {
		SpringApplication.run(SpringVaadinApplication.class, args);
	}*/

	 public static void main(String[] args) {
		 SpringApplication application = new SpringApplication(SpringVaadinApplication.class);
		 application.run(args);
	   }

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringVaadinApplication.class);
	}

	@Override
	public void configurePage(AppShellSettings settings) {
		AppShellConfigurator.super.configurePage(settings);
		 settings.addLink("shortcut icon", "icons/icon.png");
		settings.addFavIcon("icon", "icons/icon.png", "280x280");
	}




}
