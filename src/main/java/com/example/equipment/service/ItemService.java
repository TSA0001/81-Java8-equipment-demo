package com.example.equipment.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.equipment.dao.CategoryDao;
import com.example.equipment.dao.ItemDao;
import com.example.equipment.model.Category;
import com.example.equipment.model.Item;
import com.example.equipment.model.ItemForm;
import com.example.equipment.model.ItemSearchCriteria;
import com.example.equipment.model.ItemStatus;
import com.example.equipment.validation.ItemFormValidator;

public class ItemService {

    private static final Logger LOGGER = Logger.getLogger(ItemService.class.getName());

    private final ItemDao itemDao = new ItemDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final ItemFormValidator validator = new ItemFormValidator();

    public List<Item> findAll() throws ServiceException {
        return search(new ItemSearchCriteria());
    }

    public List<Item> search(ItemSearchCriteria criteria) throws ServiceException {
        try {
            return itemDao.search(criteria == null ? new ItemSearchCriteria() : criteria);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search items", e);
            throw new ServiceException("備品の検索に失敗しました。");
        }
    }

    public Item findById(Long itemId) throws ServiceException {
        try {
            Item item = itemDao.findById(itemId);
            if (item == null) {
                throw new ServiceException("指定された備品が見つかりません。");
            }
            return item;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find item id=" + itemId, e);
            throw new ServiceException("備品の取得に失敗しました。");
        }
    }

    public List<Category> findCategories() throws ServiceException {
        try {
            return categoryDao.findAllActive();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to list categories", e);
            throw new ServiceException("カテゴリの取得に失敗しました。");
        }
    }

    public Map<String, String> validateForm(ItemForm form) {
        return validator.validate(form);
    }

    public void assertBusinessRules(ItemForm form) throws ServiceException {
        try {
            if (categoryDao.findById(form.getCategoryId()) == null) {
                throw new ServiceException("選択されたカテゴリが存在しません。");
            }
            if (itemDao.existsByManagementNo(form.getManagementNo(), form.getItemId())) {
                throw new ServiceException("管理番号が既に登録されています。");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Business rule check failed", e);
            throw new ServiceException("入力内容の確認に失敗しました。");
        }
    }

    public long create(ItemForm form) throws ServiceException {
        Map<String, String> errors = validateForm(form);
        if (!errors.isEmpty()) {
            throw new ServiceException(errors.values().iterator().next());
        }
        assertBusinessRules(form);
        Item item = toItem(form);
        try {
            return itemDao.insert(item);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to create item", e);
            throw new ServiceException("備品の登録に失敗しました。");
        }
    }

    public void update(ItemForm form) throws ServiceException {
        Map<String, String> errors = validateForm(form);
        if (!errors.isEmpty()) {
            throw new ServiceException(errors.values().iterator().next());
        }
        if (form.getItemId() == null) {
            throw new ServiceException("更新対象が指定されていません。");
        }
        assertBusinessRules(form);
        Item current = findById(form.getItemId());
        if (current.getStatus() == ItemStatus.LOANED && ItemStatus.DISPOSED.name().equals(form.getStatus())) {
            // 貸出中の廃棄は後続の貸出機能で厳密化する。ここでは状態変更は許可。
        }
        if (current.getStatus() == ItemStatus.LOANED) {
            // 貸出機能実装前は削除不可チェックのみ後段で実施
        }
        Item item = toItem(form);
        item.setItemId(form.getItemId());
        item.setVersion(form.getVersion());
        try {
            int updated = itemDao.update(item);
            if (updated == 0) {
                throw new ServiceException("他のユーザーにより更新されています。画面を再読み込みしてください。");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update item", e);
            throw new ServiceException("備品の更新に失敗しました。");
        }
    }

    public void delete(Long itemId, int version) throws ServiceException {
        Item current = findById(itemId);
        if (current.getStatus() == ItemStatus.LOANED) {
            throw new ServiceException("貸出中の備品は削除できません。");
        }
        try {
            int updated = itemDao.logicalDelete(itemId, version);
            if (updated == 0) {
                throw new ServiceException("他のユーザーにより更新されています。画面を再読み込みしてください。");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete item", e);
            throw new ServiceException("備品の削除に失敗しました。");
        }
    }

    public ItemForm toForm(Item item) {
        ItemForm form = new ItemForm();
        form.setItemId(item.getItemId());
        form.setManagementNo(item.getManagementNo());
        form.setItemName(item.getItemName());
        form.setCategoryId(item.getCategoryId());
        if (item.getPurchaseDate() != null) {
            form.setPurchaseDate(item.getPurchaseDate().toString());
        }
        form.setStorageLocation(item.getStorageLocation());
        form.setStatus(item.getStatus().name());
        form.setNote(item.getNote());
        form.setVersion(item.getVersion());
        return form;
    }

    private Item toItem(ItemForm form) {
        Item item = new Item();
        item.setManagementNo(form.getManagementNo());
        item.setItemName(form.getItemName());
        item.setCategoryId(form.getCategoryId());
        item.setPurchaseDate(validator.toSqlDate(form.getPurchaseDate()));
        item.setStorageLocation(form.getStorageLocation());
        item.setStatus(ItemStatus.fromCode(form.getStatus()));
        item.setNote(form.getNote());
        return item;
    }
}
