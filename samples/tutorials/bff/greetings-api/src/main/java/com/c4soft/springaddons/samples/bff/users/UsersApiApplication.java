package com.c4soft.springaddons.samples.bff.users;

import java.util.Collection;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

import com.c4_soft.springaddons.security.oauth2.OAuthentication;
import com.c4_soft.springaddons.security.oauth2.OpenidClaimSet;
import com.c4_soft.springaddons.security.oauth2.config.SpringAddonsSecurityProperties;
import com.c4_soft.springaddons.security.oauth2.config.synchronised.OAuth2AuthenticationFactory;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Users API", version = "1.0.0"), security = { @SecurityRequirement(name = "OAuth2") })
public class UsersApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApiApplication.class, args);
	}

	@Configuration
	@EnableMethodSecurity
	static class WebSecurityConfig {
		@Bean
		OAuth2AuthenticationFactory authenticationFactory(
				Converter<Map<String, Object>, Collection<? extends GrantedAuthority>> authoritiesConverter,
				SpringAddonsSecurityProperties addonsProperties) {
			return (bearerString, claims) -> new OAuthentication<>(
					new OpenidClaimSet(claims, addonsProperties.getIssuerProperties(claims.get(JwtClaimNames.ISS)).getUsernameClaim()),
					authoritiesConverter.convert(claims),
					bearerString);
		}
	}
}
