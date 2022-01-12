package org.thshsh.crypt.web.view;

import org.springframework.web.reactive.function.client.WebClient;


public class Coinbase {
	
	/*
	 	//coinbase oauth
	//clientid: 4008e661cd4e9c943ece898ccd3671050d852f03517341e453a5a7bc1a6f5e25
	//secret: d905f7b250a654762288560e694e246ebfc4546d5abc54d09c11275118c2030d

	//https://www.coinbase.com/oauth/authorize?client_id=4008e661cd4e9c943ece898ccd3671050d852f03517341e453a5a7bc1a6f5e25&redirect_uri=https%3A%2F%2Fcryptools.thshsh.org%2Foauthcallback&response_type=code&scope=wallet%3Aaccounts%3Aread
	//respose code 861655c572acef09c8a91064ee31afb3988f49e9c3b743940718806e1ddc7319
	//curl https://api.coinbase.com/v2/accounts \
	 https://api.coinbase.com/oauth/token
	 */
	
	//https://cryptools.thshsh.org/oauthcallback?code=dbf25f0b52e05a680863b008e3e0e9f85627707f225b796048bf9bcf74892e30
	
	WebClient webClient;
	
	public Coinbase() {
		webClient = WebClient.builder().baseUrl("https://api.coinbase.com").build();
		
		
		
	}
	
	public void call() {
		/*ServerOAuth2AuthorizedClientExchangeFilterFunction.
		
		  this.webClient.get().uri(apiUrl, uriBuilder -> uriBuilder
		            .path("/search/code")
		            .queryParam("q", keyword).build())
		            .attributes(oauth2AuthorizedClient(authorizedClient))
		            .retrieve()
		            .bodyToMono(SearchCount.class);*/
	}

	
}
