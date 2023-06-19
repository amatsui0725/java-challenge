package jp.co.axa.apidemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/h2-console/**", "/swagger-ui.html", "/swagger-ui/**", "/v2/api-docs/**", "/swagger-resources/**",
            "/webjars/**")
        .permitAll()
        .antMatchers("/api/v1/**").authenticated()
        .and().httpBasic()
        .and().headers().frameOptions().disable() // Disable X-Frame-Options for H2 Console
        .and().csrf().disable(); // Disable CSRF protection for H2 Console
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("akira").password("{noop}password").roles("USER").and().withUser("admin")
        .password("{noop}admin").credentialsExpired(true).accountExpired(true).accountLocked(true)
        .authorities("WRITE_PRIVILEGES", "READ_PRIVILEGES").roles("ADMIN");
  }
}