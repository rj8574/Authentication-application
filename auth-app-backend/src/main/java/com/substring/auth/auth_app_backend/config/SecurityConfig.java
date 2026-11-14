package com.substring.auth.auth_app_backend.config;

import com.substring.auth.auth_app_backend.exceptions.ApiError;
import com.substring.auth.auth_app_backend.security.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;
import com.substring.auth.auth_app_backend.security.JwtAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private OAuth2SuccessHandler successHandler;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, OAuth2SuccessHandler successHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.successHandler = successHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf(AbstractHttpConfigurer::disable)
          .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable());
        http
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers(AppConstant.AUTH_PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()


                )
                .oauth2Login(oAuth2->{
                    oAuth2.successHandler(successHandler)
                            .failureHandler(null);
                })
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex->
                        ex.authenticationEntryPoint(((request, response, e) ->{
                            e.printStackTrace();
                            response.setStatus(401);
                            response.setContentType("application/json");
                            String message="unautherized access"+e.getMessage();
                            String error = (String) request.getAttribute("error");
                            if(error!=null)
                            {
                                message =error;
                            }

//                            Map<String,Object> errorMap = Map.of("message",message,"status",404,"statusCode",Integer.toString(401));
                            var apiError=ApiError.of(HttpStatus.UNAUTHORIZED.value(),"Unautherized Access",error,request.getRequestURI());
                            var objectMapper = new ObjectMapper();
                            response.getWriter().write(objectMapper.writeValueAsString(apiError));
                        } )))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
