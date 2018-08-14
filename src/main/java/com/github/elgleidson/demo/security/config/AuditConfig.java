package com.github.elgleidson.demo.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.github.elgleidson.demo.security.audit.UserAuditorAware;

@Configuration
@EnableJpaAuditing
public class AuditConfig {
	
	@Bean
    public AuditorAware<Long> auditorProvider() {
        return new UserAuditorAware();
    }

}
