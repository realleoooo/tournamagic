package com.tournamagic.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> migrateWithRecovery(flyway, false);
    }

    private void migrateWithRecovery(Flyway flyway, boolean repaired) {
        try {
            flyway.migrate();
        } catch (FlywayException exception) {
            if (repaired || !isRecoverableValidationFailure(exception)) {
                throw exception;
            }

            flyway.repair();
            migrateWithRecovery(flyway, true);
        }
    }

    private boolean isRecoverableValidationFailure(FlywayException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return false;
        }

        String normalized = message.toLowerCase();
        return normalized.contains("failed migration") || normalized.contains("failed validation");
    }
}
