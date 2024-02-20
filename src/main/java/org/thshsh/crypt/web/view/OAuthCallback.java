package org.thshsh.crypt.web.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.thshsh.coinbase.adv.AdvancedTradeApi;
import org.thshsh.coinbase.adv.AdvancedTradeApi.AccessToken;
import org.thshsh.crypt.serv.PortfolioHistoryService;
import org.thshsh.crypt.web.views.main.MainLayout;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "oauthcallback", layout = MainLayout.class)
//@PageTitle("System")
//@SecuredByFeatureAccess(feature=Feature.System,access=Access.Super)
public class OAuthCallback extends VerticalLayout implements HasUrlParameter<String> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(OAuthCallback.class);
	
	/*
	 GET https://example.com/oauth/callback?code=4c666b5c0c0d9d3140f2e0776cbe245f3143011d82b7a2c2a590cc7e20b79ae8&state=134ef5504a94



	 */
	
	@Autowired
	PortfolioHistoryService histService;
	
	@Autowired
	ApplicationContext context;

	@Autowired
	AsyncTaskExecutor executor;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	TransactionTemplate template;
	
	@Autowired
	AdvancedTradeApi atApi;
	
	@PostConstruct
	public void postConstruct() {
		

	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

		Map<String,List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
		Optional<String> codeOpt = parameters.getOrDefault("code", Collections.emptyList()).stream().findFirst();
		
		codeOpt.ifPresent(code -> {
			LOGGER.info("auth code for current user: {}",code);
			
			AccessToken atr = atApi.getToken(code);
			
			
			
		});
		
	}
	
}
