package org.thshsh.crypt.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.thshsh.crypt.CryptModel;
import org.thshsh.cryptman.CryptmanModel;
import org.vaadin.artur.helpers.LaunchUtil;

import com.vaadin.flow.component.Component;
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
		CryptModel.class
},
exclude = ErrorMvcAutoConfiguration.class)
@Theme(value = Lumo.class, variant = SpringVaadinApplication.THEME_VARIANT)
@PWA(name = "Cryptools", shortName = "Cryptools",iconPath = "icons/cryptools-icon.png")
@Push(transport = Transport.LONG_POLLING)
public class SpringVaadinApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	public static final String THEME_VARIANT = Material.LIGHT;

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringVaadinApplication.class);

	public static final String APP_NAME = "";
	

	/*public static void main(String[] args) {
		SpringApplication.run(SpringVaadinApplication.class, args);
	}*/

	 public static void main(String[] args) {
	        LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(SpringVaadinApplication.class, args));


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
