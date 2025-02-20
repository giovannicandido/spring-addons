package com.c4soft.springaddons.tutorials;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;

import com.c4_soft.springaddons.security.oauth2.OAuthentication;
import com.c4_soft.springaddons.security.oauth2.OpenidClaimSet;
import com.c4_soft.springaddons.security.oauth2.config.synchronised.HttpServletRequestSupport;
import com.c4_soft.springaddons.security.oauth2.config.synchronised.HttpServletRequestSupport.InvalidHeaderException;
import com.c4_soft.springaddons.security.oauth2.config.synchronised.OAuth2AuthenticationFactory;
import com.c4_soft.springaddons.security.oauth2.config.synchronised.ResourceServerExpressionInterceptUrlRegistryPostProcessor;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	public static final String ID_TOKEN_HEADER_NAME = "X-ID-Token";
	private static final Map<String, JwtDecoder> idTokenDecoders = new ConcurrentHashMap<>();

	private JwtDecoder getJwtDecoder(Map<String, Object> accessClaims) {
		if (accessClaims == null) {
			return null;
		}
		final var iss = Optional.ofNullable(accessClaims.get(JwtClaimNames.ISS)).map(Object::toString).orElse(null);
		if (iss == null) {
			return null;
		}
		if (!idTokenDecoders.containsKey(iss)) {
			idTokenDecoders.put(iss, JwtDecoders.fromIssuerLocation(iss));
		}
		return idTokenDecoders.get(iss);
	}

	@Bean
	OAuth2AuthenticationFactory authenticationFactory(Converter<Map<String, Object>, Collection<? extends GrantedAuthority>> authoritiesConverter) {
		return (accessBearerString, accessClaims) -> {
			try {
				final var jwtDecoder = getJwtDecoder(accessClaims);
				final var authorities = authoritiesConverter.convert(accessClaims);
				final var idTokenString = HttpServletRequestSupport.getUniqueRequestHeader(ID_TOKEN_HEADER_NAME);
				final var idToken = jwtDecoder == null ? null : jwtDecoder.decode(idTokenString);

				return new MyAuth(authorities, accessBearerString, new OpenidClaimSet(accessClaims), idTokenString, new OpenidClaimSet(idToken.getClaims()));
			} catch (JwtException e) {
				throw new InvalidHeaderException(ID_TOKEN_HEADER_NAME);
			}
		};
	}

	@Bean
	ResourceServerExpressionInterceptUrlRegistryPostProcessor expressionInterceptUrlRegistryPostProcessor() {
		// @formatter:off
        return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) -> registry
                .requestMatchers(HttpMethod.GET, "/actuator/**").hasAuthority("OBSERVABILITY:read")
                .requestMatchers("/actuator/**").hasAuthority("OBSERVABILITY:write")
                .anyRequest().authenticated();
        // @formatter:on
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class MyAuth extends OAuthentication<OpenidClaimSet> {
		private static final long serialVersionUID = 1734079415899000362L;
		private final String idTokenString;
		private final OpenidClaimSet idClaims;

		public MyAuth(
				Collection<? extends GrantedAuthority> authorities,
				String accessTokenString,
				OpenidClaimSet accessClaims,
				String idTokenString,
				OpenidClaimSet idClaims) {
			super(accessClaims, authorities, accessTokenString);
			this.idTokenString = idTokenString;
			this.idClaims = idClaims;
		}

	}
}