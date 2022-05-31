/*
 * Copyright 2019 Jérôme Wacongne
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.c4_soft.springaddons.samples.webflux_oidcauthentication;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.c4_soft.springaddons.security.oauth2.oidc.OidcAuthentication;
import com.c4_soft.springaddons.security.oauth2.oidc.OidcToken;

import reactor.core.publisher.Mono;

@Service
public class MessageService {

	@PreAuthorize("hasRole('AUTHORIZED_PERSONNEL')")
	public Mono<String> getSecret() {
		return Mono.just("Secret message");
	}

	@PreAuthorize("authenticated")
	public Mono<String> greet(OidcAuthentication<OidcToken> who) {
		final String msg =
				String.format("Hello %s! You are granted with %s.", who.getName(), who.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
		return Mono.just(msg);
	}

}