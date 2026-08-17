# 画面遷移（第1段階）

```
/  (index.jsp)
  → redirect → /home  (HomeServlet → home.jsp)
                    ↓ 「備品一覧へ」
                 /items  (ItemListServlet → itemList.jsp)
                    ↓ 「トップへ戻る」
                 /home
```

`/health` はテキスト `OK` を返す（画面遷移なし）。
