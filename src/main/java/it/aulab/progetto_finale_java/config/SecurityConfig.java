package it.aulab.progetto_finale_java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import it.aulab.progetto_finale_java.services.CustomUserDetailsService;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
        .csrf(csrf->csrf.disable())
        .authorizeHttpRequests((authorize)->
        authorize.requestMatchers("/register/**").permitAll()
        .requestMatchers("/admin/dashboard", "/categories/create", "/categories/edit/{id}", "/categories/update/{id}", "/categories/delete/{id}").hasRole("ADMIN")
        .requestMatchers("/revisor/dashboard", "/revisor/detail/{id}", "/accept").hasRole("REVISOR")
        .requestMatchers("/writer/dashboard", "/articles/create", "/articles/edit/{id}", "/articles/update/{id}", "/articles/delete/{id}").hasRole("WRITER")
        .requestMatchers("/login", "/register", "/" , "/articles", "/images/**", "/articles/detail/**", "/categories/search/{id}", "/search/{id}", "/articles/search", "/css/**", "/js/**", "/webjars/**").permitAll()
        .anyRequest().authenticated())
        .formLogin(form->
            form.loginPage("/login").
            loginProcessingUrl("/perform_login")
            .defaultSuccessUrl("/")
            .failureUrl("/login?error=true")
            .permitAll())
        .logout(logout -> logout
    .logoutUrl("/logout")
    .permitAll())
        .exceptionHandling(exception->exception.accessDeniedPage("/error/403"))
        .sessionManagement(session->session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .maximumSessions(1)
        .expiredUrl("/login?session-expired=true")
        );

        return http.build();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
    throws Exception{
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
