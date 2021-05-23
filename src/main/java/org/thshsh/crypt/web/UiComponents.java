package org.thshsh.crypt.web;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.repo.AppUserRepository;
import org.thshsh.crypt.web.view.UserDialog;

@Configuration
public class UiComponents {

	@Autowired
	AppUserRepository userRepo;



	/*@Bean
	@Scope("prototype")
	public FlowDialog getFlowDialog(Flow c) {
		return new FlowDialog(c);
	}*/

	@Bean
	@Scope("prototype")
	public UserDialog getUserDialog(User c) {
		return new UserDialog(c);
	}

	/*	@Bean
		@Scope("prototype")
		public UserRolesDialog getUserRolesDialog(User c) {
			return new UserRolesDialog(c);
		}*/

	/*	@Bean
		@Scope("prototype")
		public PublishProfileDialog getPublishProfileDialog(ProfileConfiguration c) {
			return new PublishProfileDialog(c);
		}*/




	/*@Bean
	@SessionScope
	public AppSession getAppSession() {


		return new AppSession();
	}
	*/

	@Bean
	public DateTimeFormatter getDateTimeFormat() {
		return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
	}

	@Bean
	public PeriodFormatter getPeriodFormatter() {
		PeriodFormatter formatter = new PeriodFormatterBuilder()
			     .appendHours()
			     .appendSuffix("h")
			     .appendMinutes()
			     .appendSuffix("m")
			     .toFormatter();
		return formatter;
	}
}
