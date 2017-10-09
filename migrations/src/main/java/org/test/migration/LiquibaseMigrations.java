package org.test.migration;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

public class LiquibaseMigrations implements AutoCloseable {
    private static final String CHANGELOG_MASTER_RESOURCE = "liquibase/changelog-master.xml";
    private final ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(ClassLoader.getSystemClassLoader());
    private final DatabaseConnection databaseConnection;
    private final Liquibase liquibase;

    public LiquibaseMigrations(String dbUrl, String dbUserName, String dbPassword) throws Exception {
        databaseConnection = DatabaseFactory.getInstance().openConnection(dbUrl, dbUserName, dbPassword, null, resourceAccessor);
        liquibase = new Liquibase(CHANGELOG_MASTER_RESOURCE, new ClassLoaderResourceAccessor(), databaseConnection);
    }

    public void update() throws Exception {
        liquibase.update((String)null);
    }

    @Override
    public void close() throws Exception {
        databaseConnection.close();
    }
}