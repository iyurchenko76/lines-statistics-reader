package org.test.db.dao;

import org.junit.Test;
import org.test.db.testutil.DbTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FileDaoTest extends DbTest {
    private final FileDao fileDao = new FileDao();

//    @Before
//    public void setUp() throws Exception {
//        fileDao.beginTransaction();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        fileDao.rollbackTransaction();
//    }

    @Test
    public void shouldStoreFile() throws Exception {
        // GIVEN
        FileDao fileDao = new FileDao();
        String fileName = "testFileName";
        // WHEN
        int fileId = fileDao.saveFile(fileName);
        // THEN
        assertThat(fileId, is(1));
    }
}