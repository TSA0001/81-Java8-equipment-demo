# 備品管理システム — アーキテクチャ（第2段階）

```
Browser
  → Filter (CharacterEncodingFilter)
  → Servlet
  → Service
  → DAO
  → JDBC / H2 (file: /data/h2/equipment)
  ↑
JSP
```

起動時に `AppBootstrapListener` が schema / seed を未初期化時のみ投入する。
