package org.upstarters.gatewayserver.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http){
        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/login/**", "/oauth2/**").permitAll()

                        .pathMatchers("/Proiect_TW/courses/**").hasAnyRole("ADMIN", "STUDENT")

                        .anyExchange().authenticated())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public ServerAuthenticationSuccessHandler successHandler() {
        return (webFilterExchange, authentication) -> {
            System.out.println("Authenticated authorities at success: " + authentication.getAuthorities());

            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .setStatusCode(org.springframework.http.HttpStatus.FOUND);

            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .getHeaders().set("Location", "http://localhost:8072/university");

            return webFilterExchange.getExchange().getResponse().setComplete();
        };
    }

    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return (userRequest) -> delegate.loadUser(userRequest)
                .map(oidcUser -> {
                    Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());
                    String email = oidcUser.getEmail();

                    if (email != null) {
                        String emailLower = email.toLowerCase();

                        if (emailLower.endsWith("@gmail.com")) {
                            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
                        }

                        if (emailLower.endsWith("@university-admin.ro") || emailLower.equals("flaviu.petre09@gmail.com")) {
                            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        }
                    }

                    System.out.println("User: " + email + " | Mapped authorities: " + mappedAuthorities);

                    return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                });
    }
}
