package org.thshsh.crypt.web.view;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/*@EnableOAuth2Client
@Configuration
class MyConfig{

    @Value("${oauth.resource:http://localhost:8082}")
    private String baseUrl;
    @Value("${oauth.authorize:http://localhost:8082/oauth/authorize}")
    private String authorizeUrl;
    @Value("${oauth.token:http://localhost:8082/oauth/token}")
    private String tokenUrl;

    @Bean
    protected OAuth2ProtectedResourceDetails resource() {
        ResourceOwnerPasswordResourceDetails resource;
        resource = new ResourceOwnerPasswordResourceDetails();

        List scopes = new ArrayList<String>(2);
        scopes.add("write");
        scopes.add("read");
        resource.setAccessTokenUri(tokenUrl);
        resource.setClientId("restapp");
        resource.setClientSecret("restapp");
        resource.setGrantType("password");
        resource.setScope(scopes);
        resource.setUsername("**USERNAME**");
        resource.setPassword("**PASSWORD**");
        return resource;
    }

    @Bean
    public OAuth2RestOperations restTemplate() {
        AccessTokenRequest atr = new DefaultAccessTokenRequest();
        return new OAuth2RestTemplate(resource(), new DefaultOAuth2ClientContext(atr));
    }
}

@Service
@SuppressWarnings("unchecked")
class MyService {

    @Autowired
    private OAuth2RestOperations restTemplate;

    public MyService() {
        restTemplate.getAccessToken();
    }
}*/