package com.example.equipment.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.example.equipment.model.UserAccount;
import com.example.equipment.util.DatabaseInitializer;

public class UserDaoTest {

    private final UserDao userDao = new UserDao();

    @Before
    public void setUp() throws Exception {
        File dir = new File(System.getProperty("java.io.tmpdir"), "equipment-user-" + UUID.randomUUID());
        dir.mkdirs();
        System.setProperty("equipment.db.path", new File(dir, "test").getAbsolutePath());
        DatabaseInitializer.initializeIfNeeded();
    }

    @Test
    public void authenticateAdminSuccess() throws Exception {
        UserAccount user = userDao.authenticate("admin", "admin123");
        assertNotNull(user);
        assertEquals("admin", user.getLoginId());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    public void authenticateUser1Success() throws Exception {
        UserAccount user = userDao.authenticate("user1", "user1234");
        assertNotNull(user);
        assertEquals("user1", user.getLoginId());
        assertEquals("USER", user.getRole());
    }

    @Test
    public void authenticateFailsWithWrongPassword() throws Exception {
        UserAccount user = userDao.authenticate("admin", "wrongpassword");
        assertNull(user);
    }

    @Test
    public void authenticateFailsWithUnknownLoginId() throws Exception {
        UserAccount user = userDao.authenticate("nobody", "admin123");
        assertNull(user);
    }

    @Test
    public void findAllActiveReturnsSeedUsers() throws Exception {
        assertEquals(2, userDao.findAllActive().size());
    }
}
