package org.test.db.testutil;

public class DbTest {
    private static final String DB_URL = "jdbc:h2:mem:testdb";

    public DbTest() {
        System.setProperty("db.url", DB_URL);
    }
}
