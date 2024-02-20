package org.cryptools;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;
import org.thshsh.coinbase.CoinbaseApi;
import org.thshsh.coinbase.adv.AdvancedTradeApi;

public class CoinbaseApiTest {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CoinbaseApiTest.class);


	CoinbaseApi coinbase = new CoinbaseApi(
			"68d511513ea00b7db1c97775e4dcfe98",
			"VfYiRxLogDXUB0s1WbIP8Mx6q5TL1FP+S0Z+HxPJjao+JLKFq6P5O5EdGLImiatGKVTZT6fdAY54Kr6r4NcQYg==",
			"fgnlkhcquav");
	
	/*	AdvancedTradeApi advTrade = new AdvancedTradeApi(
				"PP3jK9Q1kvjFj5cP",
				"IS1wa4OBykSMg0Z0YYLVzm43x8v4dAtg");
		*/
	@Test
	public void test() {
		coinbase.test();
	}
	

	@Test
	public void testAuth() throws InvalidKeyException, NoSuchAlgorithmException, DecoderException {
		//coinbase.getAccounts();		
		//advTrade.getAccounts();
	}
	
	@Test
	public void oauthtest() {
		
		String regId = "mywebclient";
		
		   ClientRegistration registration = ClientRegistration
	                .withRegistrationId(regId)
	                .tokenUri("https://www.coinbase.com/oauth/token")
	                .clientId("4008e661cd4e9c943ece898ccd3671050d852f03517341e453a5a7bc1a6f5e25")
	                .clientSecret("d905f7b250a654762288560e694e246ebfc4546d5abc54d09c11275118c2030d")
	                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
	                .scope("wallet:accounts:read")
	                
	                .build();
		   
		   ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(registration);
		   
			LOGGER.info("clientRegistrationRepository: {}",clientRegistrationRepository);
			 
			
			OAuth2AuthorizedClientRepository authorizedClientRepository = new HttpSessionOAuth2AuthorizedClientRepository();
			
		    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
		            .clientCredentials()
		            .build();
		 
		    DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
		    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
		 
		    ServletOAuth2AuthorizedClientExchangeFilterFunction filter = new ServletOAuth2AuthorizedClientExchangeFilterFunction( authorizedClientManager);
		    filter.setDefaultClientRegistrationId(regId);
		    
		    WebClient client = WebClient.builder().apply(filter.oauth2Configuration()).build();
		    
		    
		
	}
}
