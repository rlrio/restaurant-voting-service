package com.rlrio.voting.config.clock;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.Clock.systemDefaultZone;

@Configuration
public class ClockConfig {
    @Bean
    public Clock clock() {
        return systemDefaultZone();
    }
}
