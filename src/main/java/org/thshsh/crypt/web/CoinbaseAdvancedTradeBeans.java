package org.thshsh.crypt.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CoinbaseAdvancedTradeBeans {



	private static final Logger LOGGER = LoggerFactory.getLogger(CoinbaseAdvancedTradeBeans.class);

	
	@Bean
	@Qualifier("coinbase")
	WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
	 
	    ServletOAuth2AuthorizedClientExchangeFilterFunction filter = new ServletOAuth2AuthorizedClientExchangeFilterFunction( authorizedClientManager);
	    filter.setDefaultClientRegistrationId("mywebclient");
	    WebClient client = WebClient.builder().apply(filter.oauth2Configuration()).build();
	    
	    return client;
	}
	
	@Bean
    ReactiveClientRegistrationRepository getRegistration(
            @Value("${spring.security.oauth2.client.provider.mywebclient.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.registration.mywebclient.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.mywebclient.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.client.registration.mywebclient.scopes}") String scope
    ) {
		
		LOGGER.info("getRegistration");
		
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("mywebclient")
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(scope)
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

	
	@Bean
	public OAuth2AuthorizedClientManager authorizedClientManager(
	        ClientRegistrationRepository clientRegistrationRepository,
	        OAuth2AuthorizedClientRepository authorizedClientRepository) {
		
		LOGGER.info("authorizedClientManager");
	 
	    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
	            .clientCredentials()
	            .build();
	 
	    DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
	    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
	 
	    return authorizedClientManager;
	}
	
}
