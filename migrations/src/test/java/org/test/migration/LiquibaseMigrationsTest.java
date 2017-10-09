package org.test.migration;

import org.junit.Test;

import static org.junit.Assert.fail;

public class LiquibaseMigrationsTest {
    @Test
    public void shouldSuccessfullyCreateNewSchema() throws Exception {
        // GIVEN
        try (LiquibaseMigrations migrations = new LiquibaseMigrations("jdbc:h2:mem:", null, null)) {
            // WHEN
            migrations.update();
            // THEN
        } catch (Exception e) {
            fail("Exception shouldn't be thrown while applying changelog");
        }
    }
}