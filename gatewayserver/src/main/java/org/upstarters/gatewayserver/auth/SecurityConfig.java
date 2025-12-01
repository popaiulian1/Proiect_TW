package org.upstarters.gatewayserver.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http){
        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
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
}
