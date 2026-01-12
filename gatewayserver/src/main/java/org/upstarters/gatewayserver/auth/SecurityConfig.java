package org.upstarters.gatewayserver.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;


@Configuration
@Profile("test")
public class SecurityConfig {

    // private final String idProject = "tw-project-479910";

    //private final String idProject = "universitydemo-479314";
    private final String idProject = "test-project-479314";
  
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http){
        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/login/**", "/oauth2/**").permitAll()

                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/courses/getCourseById/{courseId}").hasAnyRole("ADMIN", "STUDENT")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/courses/getCoursesByDepartment/{department}").hasAnyRole("ADMIN", "STUDENT")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/courses/getAllAvailableCourses").hasAnyRole("ADMIN", "STUDENT")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/courses/getCoursesSortedByCapacity").hasAnyRole("ADMIN", "STUDENT")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/courses/getByTitle/{title}").hasAnyRole("ADMIN", "STUDENT")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/courses/getAllStudents").hasAnyRole("ADMIN", "STUDENT")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/courses/getStudentsByDepartment/{department}").hasAnyRole("ADMIN", "STUDENT")
                        .pathMatchers(HttpMethod.PUT, "/Proiect_TW/courses/syncCapacityWithStudents/{title}").hasAnyRole("ADMIN", "STUDENT")

                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/students/countStudents").hasAnyRole("STUDENT", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/students/getByEmail/{email}").hasAnyRole("STUDENT", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/Proiect_TW/enrollments/create").hasAnyRole("STUDENT", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/enrollments/all").hasAnyRole("STUDENT", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/enrollments/enrollment/{id}").hasAnyRole("STUDENT", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/enrollments/students/{course}").hasAnyRole("STUDENT", "ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/Proiect_TW/enrollments/update/{id}").hasAnyRole("STUDENT", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/Proiect_TW/enrollments/student/enrollment/{enrollmentId}/student-details").hasAnyRole("STUDENT", "ADMIN")

                        .anyExchange().hasAnyRole("ADMIN"))
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

        return (userRequest -> delegate.loadUser(userRequest)
                .map(oidcUser -> {
                    Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());
                    String email = oidcUser.getEmail();

                    try {
                        Set<GrantedAuthority> iamRoles = getIamRoles(userRequest, oidcUser);
                        mappedAuthorities.addAll(iamRoles);

                    }catch (GeneralSecurityException | IOException e) {
                        System.out.println(e.getMessage());
                    }

                    System.out.println("User: " + email + " | Mapped authorities: " + mappedAuthorities);

                    return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                })
        );
    }

    private Set<GrantedAuthority> getIamRoles(OidcUserRequest userRequest, OidcUser oidcUser) throws GeneralSecurityException, IOException {

        String accessTokenValue = userRequest.getAccessToken().getTokenValue();
        System.out.println("accessTokenValue: " + accessTokenValue);

        AccessToken accessToken = new AccessToken(accessTokenValue, Date.from(userRequest.getAccessToken().getExpiresAt()));
        System.out.println("accessToken: " + accessToken.getTokenValue());

        GoogleCredentials credentials = GoogleCredentials.create(accessToken);
        System.out.println("credentials: " + credentials);

        CloudResourceManager handler = new CloudResourceManager.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("GatewayServer")
                .build();

        GetIamPolicyRequest policyRequest = new GetIamPolicyRequest();
        Policy policy =  handler.projects().getIamPolicy(idProject, policyRequest).execute();
        System.out.println("policy: " + policy);

        String email = oidcUser.getEmail();
        String identifier = "user:" + email;

        return policy.getBindings().stream()
                .filter(binding -> binding.getMembers() != null && binding.getMembers().contains(identifier))
                .map(Binding::getRole)
                .peek(role -> System.out.println("Role is: " + role))
                .map(this::mapIamRolesToApplicationRoles)
                .collect(Collectors.toSet());

    }
    private GrantedAuthority mapIamRolesToApplicationRoles(String role) {
            if ("roles/owner".equals(role))
                return new SimpleGrantedAuthority("ROLE_ADMIN");

            if ("roles/editor".equals(role))
                return new SimpleGrantedAuthority("ROLE_ADMIN");

            if ("roles/viewer".equals(role))
                return new SimpleGrantedAuthority("ROLE_STUDENT");

            return new SimpleGrantedAuthority("ROLE_STUDENT");
    }
}
