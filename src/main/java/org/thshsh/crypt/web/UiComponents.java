package org.thshsh.crypt.web;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thshsh.crypt.web.repo.AppUserRepository;

import com.vaadin.flow.component.grid.Grid.Column;

@Configuration
public class UiComponents {

	@Autowired
	AppUserRepository userRepo;



	/*@Bean
	@Scope("prototype")
	public FlowDialog getFlowDialog(Flow c) {
		return new FlowDialog(c);
	}*/

	/*@Bean
	@Scope("prototype")
	public UserDialog getUserDialog(User c) {
		return new UserDialog(c);
	}*/

	@Bean
	@Qualifier("hundred")
	public BigDecimal hundred() {
		return new BigDecimal(100l);
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

	public static void iconColumn(Column<?> col) {
		col.setWidth("38px")
		.setFlexGrow(0)
		.setClassNameGenerator(pe -> {
			return "icon";
		});
	}

	public static void iconLabelColumn(Column<?> col) {
		col
		//.setWidth("38px")
		//.setFlexGrow(0)
		.setClassNameGenerator(pe -> {
			return "icon-label";
		});
	}

}
