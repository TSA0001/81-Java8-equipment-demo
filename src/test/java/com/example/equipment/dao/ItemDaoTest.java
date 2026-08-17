package com.example.equipment.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.example.equipment.model.Item;
import com.example.equipment.model.ItemSearchCriteria;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.util.DatabaseInitializer;

public class ItemDaoTest {

    private final ItemDao itemDao = new ItemDao();

    @Before
    public void setUp() throws Exception {
        File dir = new File(System.getProperty("java.io.tmpdir"), "equipment-h2-" + UUID.randomUUID());
        dir.mkdirs();
        System.setProperty("equipment.db.path", new File(dir, "test").getAbsolutePath());
        DatabaseInitializer.initializeIfNeeded();
    }

    @Test
    public void findSeedItems() throws Exception {
        List<Item> items = itemDao.findAllActive();
        assertTrue(items.size() >= 3);
        Item first = itemDao.findById(items.get(0).getItemId());
        assertNotNull(first);
        assertEquals(ItemStatus.AVAILABLE, first.getStatus());
    }

    @Test
    public void insertAndDetectDuplicateManagementNo() throws Exception {
        Item item = new Item();
        item.setManagementNo("EQ-009999");
        item.setItemName("DAOテスト");
        item.setCategoryId(Long.valueOf(1L));
        item.setStorageLocation("テスト倉庫");
        item.setStatus(ItemStatus.AVAILABLE);
        long id = itemDao.insert(item);
        assertTrue(id > 0);
        assertTrue(itemDao.existsByManagementNo("EQ-009999", null));
        assertFalse(itemDao.existsByManagementNo("EQ-009999", Long.valueOf(id)));
    }

    @Test
    public void searchByItemNameAndStatus() throws Exception {
        ItemSearchCriteria criteria = new ItemSearchCriteria();
        criteria.setItemName("ノートPC");
        criteria.setStatus(ItemStatus.AVAILABLE.name());
        List<Item> items = itemDao.search(criteria);
        assertFalse(items.isEmpty());
        for (Item item : items) {
            assertTrue(item.getItemName().contains("ノートPC"));
            assertEquals(ItemStatus.AVAILABLE, item.getStatus());
        }
    }

    @Test
    public void searchByCategory() throws Exception {
        ItemSearchCriteria criteria = new ItemSearchCriteria();
        criteria.setCategoryId(Long.valueOf(2L));
        List<Item> items = itemDao.search(criteria);
        assertFalse(items.isEmpty());
        for (Item item : items) {
            assertEquals(Long.valueOf(2L), item.getCategoryId());
        }
    }
}
