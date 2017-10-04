package org.test.migration;

import org.junit.Test;

public class LiquibaseMigrationsTest {
    @Test
    public void shouldSuccessfullyCreateNewSchema() throws Exception {
        // WHEN
        LiquibaseMigrations.update("jdbc:h2:mem:", null, null);
    }
}