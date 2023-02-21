package com.c4soft.springaddons.tutorials;

import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c4_soft.springaddons.security.oauth2.config.SpringAddonsSecurityProperties;

@RestController
@RequestMapping("/greet")
@PreAuthorize("isAuthenticated()")
public class GreetingController {

    @GetMapping()
    @PreAuthorize("hasAuthority('NICE')")
    public String getGreeting(BearerTokenAuthentication auth, SpringAddonsSecurityProperties addonsProperties) {
        return "Hi %s! You are granted with: %s.".formatted(
                auth.getName(),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", ", "[", "]")));
    }
}
