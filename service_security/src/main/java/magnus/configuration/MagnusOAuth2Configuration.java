package magnus.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

public class MagnusOAuth2Configuration {

    ClientRegistrationRepository clientRegistrationRepository;
    OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @Bean
    public OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager() {
        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode()
                                                                                       .build();
        ClientRegistration build = ClientRegistration.withRegistrationId("okta").clientId("okta")
                                                     .authorizationGrantType(AuthorizationGrantType.JWT_BEARER).build();
        InMemoryClientRegistrationRepository clientRegistrations = new InMemoryClientRegistrationRepository(build);
        OAuth2AuthorizedClientRepository authorizedClientRepository = new HttpSessionOAuth2AuthorizedClientRepository();
        DefaultOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrations, authorizedClientRepository);
        defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(provider);
        return defaultOAuth2AuthorizedClientManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.oauth2Client(oauth2 -> {
            oauth2.authorizedClientRepository(oAuth2AuthorizedClientRepository)
                  .clientRegistrationRepository(clientRegistrationRepository);
        });
        httpSecurity.oauth2Login(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
