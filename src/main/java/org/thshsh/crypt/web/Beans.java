package org.thshsh.crypt.web;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.thshsh.coinbase.adv.AdvancedTradeApi;
import org.thshsh.crypt.cryptocompare.CryptoCompare;

@Configuration
public class Beans {

	@Value("${cryptocompare.apikey}")
	String apiKey;

	@Bean
	@Scope("prototype")
	public CryptoCompare cryptoCompare() {
		return new CryptoCompare(apiKey,WebClient.builder());
	}
	
	
	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.thshsh.org");
	    mailSender.setPort(587);

	    mailSender.setUsername("cryptools@thshsh.org");
	    mailSender.setPassword("crypt00ls");

	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    //props.put("mail.debug", "true");


	    props.put("mail.smtps.ssl.checkserveridentity", "false");
	    props.put("mail.smtps.ssl.trust", "*");

	    props.put("mail.smtp.ssl.checkserveridentity", "false");
	    props.put("mail.smtp.ssl.trust", "*");

	    return mailSender;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public AdvancedTradeApi advancedtrade() {
		return new AdvancedTradeApi(
				"4008e661cd4e9c943ece898ccd3671050d852f03517341e453a5a7bc1a6f5e25",
				"d905f7b250a654762288560e694e246ebfc4546d5abc54d09c11275118c2030d",
				"https://cryptools.thshsh.org/oauthcallback");
	}
	

}
