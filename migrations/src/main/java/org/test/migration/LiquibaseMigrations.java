package org.test.migration;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

public class LiquibaseMigrations {

    private LiquibaseMigrations() {}

    public static void update(String dbUrl, String dbUserName, String dbPassword) {
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(ClassLoader.getSystemClassLoader());
        try {
            DatabaseConnection databaseConnection = DatabaseFactory.getInstance().openConnection(dbUrl, dbUserName, dbPassword, null, resourceAccessor);
            try {
                Liquibase liquibase = new Liquibase("liquibase/changelog-master.xml", new ClassLoaderResourceAccessor(), databaseConnection);
                liquibase.update((String) null);
            } catch (LiquibaseException e) {
                throw new RuntimeException(e);
            } finally {
                databaseConnection.close();
            }
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
