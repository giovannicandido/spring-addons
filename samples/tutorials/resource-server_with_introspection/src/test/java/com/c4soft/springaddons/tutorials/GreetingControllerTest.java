package com.c4soft.springaddons.tutorials;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenId;
import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.MockMvcSupport;
import com.c4_soft.springaddons.security.oauth2.test.mockmvc.introspecting.AutoConfigureAddonsWebSecurity;

@WebMvcTest(controllers = GreetingController.class)
@AutoConfigureAddonsWebSecurity
@Import(WebSecurityConfig.class)
class GreetingControllerTest {

    @Autowired
    MockMvcSupport mockMvc;

    // @formatter:off
    @Test
    @OpenId(
            authorities = { "NICE",  "AUTHOR" },
            claims = @OpenIdClaims(usernameClaim = StandardClaimNames.PREFERRED_USERNAME, preferredUsername = "Tonton Pirate"))
	void givenUserIsGrantedWithNice_whenGreet_thenOk() throws Exception {
		mockMvc.get("/greet")
		    .andExpect(status().isOk())
		    .andExpect(jsonPath("$.body").value("Hi Tonton Pirate! You are granted with: [NICE, AUTHOR]."));
	}
    // @formatter:on

    @Test
    @OpenId(authorities = "AUTHOR", claims = @OpenIdClaims(preferredUsername = "Tonton Pirate"))
    void givenUserIsNotGrantedWithNice_whenGreet_thenForbidden() throws Exception {
        mockMvc.get("/greet").andExpect(status().isForbidden());
    }

    @Test
    void givenRequestIsAnonymous_whenGreet_thenUnauthorized() throws Exception {
        mockMvc.get("/greet").andExpect(status().isUnauthorized());
    }

}
