package magnus.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import magnus.utils.jwt.JwtTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@ConfigurationProperties(prefix = "magnus.jwt")
public class MagnusSecurityConfiguration {

    String tokenHeader;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, ObjectMapper objectMapper,
                                                   JwtTokenService jwtTokenService) throws Exception {
        httpSecurity.authorizeHttpRequests(
                request -> request.requestMatchers("/restful/**").permitAll().anyRequest().authenticated());
        httpSecurity.logout(LogoutConfigurer::permitAll);
        httpSecurity.addFilterAfter(magnusJwtAuthorizationFilter(jwtTokenService, objectMapper),
                                    SecurityContextHolderFilter.class);
        httpSecurity.addFilterAfter(magnusJwtAuthenticationFilter(jwtTokenService, objectMapper),
                                    SecurityContextHolderFilter.class);
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    public UsernamePasswordAuthenticationFilter magnusUsernamePasswordFilter(ObjectMapper objectMapper) {
        // 配置自定义的UsernamePasswordAuthenticationFilter
        MagnusUsernamePasswordFilter magnusUsernamePasswordFilter = new MagnusUsernamePasswordFilter();
        magnusUsernamePasswordFilter.setAuthenticationManager(providerManager());
        magnusUsernamePasswordFilter.setObjectMapper(objectMapper);
        magnusUsernamePasswordFilter.setTokenHeader(tokenHeader);
        magnusUsernamePasswordFilter.initialize();
        return magnusUsernamePasswordFilter;
    }

    public MagnusJwtAuthenticationFilter magnusJwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                                                       ObjectMapper objectMapper) {
        MagnusJwtAuthenticationFilter magnusJwtAuthenticationFilter = new MagnusJwtAuthenticationFilter();
        magnusJwtAuthenticationFilter.setTokenHeader(tokenHeader);
        magnusJwtAuthenticationFilter.setObjectMapper(objectMapper);
        magnusJwtAuthenticationFilter.setJwtTokenService(jwtTokenService);
        return magnusJwtAuthenticationFilter;
    }

    public MagnusJwtAuthorizationFilter magnusJwtAuthorizationFilter(JwtTokenService jwtTokenService,
                                                                     ObjectMapper objectMapper) {
        MagnusJwtAuthorizationFilter magnusJwtAuthorizationFilter = new MagnusJwtAuthorizationFilter(providerManager());
        magnusJwtAuthorizationFilter.setObjectMapper(objectMapper);
        magnusJwtAuthorizationFilter.setTokenHeader(tokenHeader);
        magnusJwtAuthorizationFilter.setJwtTokenService(jwtTokenService);
        magnusJwtAuthorizationFilter.initialize();
        return magnusJwtAuthorizationFilter;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails build = User.withUsername("username").password("{noop}password").roles("user").build();
        return new InMemoryUserDetailsManager(build);
    }

    @Bean
    @Qualifier("providerManager")
    public AuthenticationManager providerManager() {
        List<AuthenticationProvider> providers = new ArrayList<>();
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        MagnusJwtUserInfoProvider magnusJwtUserInfoProvider = new MagnusJwtUserInfoProvider();
        providers.add(provider);
        providers.add(magnusJwtUserInfoProvider);
        return new ProviderManager(providers);
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }
}
