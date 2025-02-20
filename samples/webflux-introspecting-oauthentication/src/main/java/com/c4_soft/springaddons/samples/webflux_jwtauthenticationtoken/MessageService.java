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

package com.c4_soft.springaddons.samples.webflux_jwtauthenticationtoken;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.c4_soft.springaddons.security.oauth2.OAuthentication;
import com.c4_soft.springaddons.security.oauth2.OpenidClaimSet;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageService {
	private final SecretRepo fooRepo;

	@PreAuthorize("hasRole('AUTHORIZED_PERSONNEL')")
	public Mono<String> getSecret() {
		final var auth = (OAuthentication<OpenidClaimSet>) SecurityContextHolder.getContext().getAuthentication();
		return fooRepo.findSecretByUsername(auth.getAttributes().getPreferredUsername());
	}

	@PreAuthorize("isAuthenticated()")
	public Mono<String> greet(OAuthentication<OpenidClaimSet> who) {
		final String msg = String.format(
				"Hello %s! You are granted with %s.",
				who.getAttributes().getPreferredUsername(),
				who.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
		return Mono.just(msg);
	}

}