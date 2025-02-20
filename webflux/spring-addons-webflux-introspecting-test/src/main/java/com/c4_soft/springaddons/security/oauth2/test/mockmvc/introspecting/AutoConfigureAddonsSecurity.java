package com.c4_soft.springaddons.security.oauth2.test.mockmvc.introspecting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.c4_soft.springaddons.security.oauth2.config.SpringAddonsSecurityProperties;
import com.c4_soft.springaddons.security.oauth2.config.reactive.AddonsSecurityBeans;

/**
 * <p>
 * To be used to test services or repositories but <b>not controllers</b>: auto-configures {@link AddonsSecurityBeans} only
 * </p>
 * See {@link AutoConfigureAddonsWebSecurity}
 *
 * @author Jérôme Wacongne &lt;ch4mp#64;c4-soft.com&gt;
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({ SpringAddonsSecurityProperties.class, AddonsSecurityBeans.class })
@ExtendWith(SpringExtension.class)
public @interface AutoConfigureAddonsSecurity {
}
