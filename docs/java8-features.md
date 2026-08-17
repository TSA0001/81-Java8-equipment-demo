# Java 8 縛り — 使用機能まとめ

備品管理システム（Java 8 / Servlet / JSP / JDBC / H2）で使用・非使用の機能一覧。

---

## ✅ 使用した Java 8 固有の機能

| 機能 | 使用箇所（ファイル） | コード例・説明 |
|---|---|---|
| try-with-resources（Java 7+） | `DatabaseInitializer.java` `HealthServlet.java` 各 DAO | `try (Connection c = ...) { ... }` — AutoCloseable を実装したリソースを自動クローズ。全 DAO で JDBC リソースのリーク防止に活用。 |
| Diamond 演算子（Java 7+） | 各 DAO / Service | `new ArrayList<>()` — Java 7 で導入。プロジェクト全体でジェネリクスの型推論に使用。 |
| `StandardCharsets`（Java 7+） | `PasswordUtil.java` `DatabaseInitializer.java` `CharacterEncodingFilter.java` | `StandardCharsets.UTF_8` — 文字セット定数。`Charset.forName("UTF-8")` の代わりに使用し、例外を排除。 |
| enum（フィールド付き） | `ItemStatus.java` `LoanStatus.java` | `AVAILABLE("利用可能")` — 表示ラベルをフィールドとして持つ enum。`fromCode()` で文字列→enum 変換も実装。 |
| アノテーション（Servlet 3.x） | 全 Servlet / Filter `AppBootstrapListener.java` | `@WebServlet` `@WebFilter` `@WebListener` — web.xml に URL マッピングを書かず、アノテーションで Servlet・Filter・Listener を登録。 |
| `Statement.RETURN_GENERATED_KEYS` | `ItemDao.java` `LoanDao.java` | `ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);` `keys = ps.getGeneratedKeys();` — INSERT 後に自動採番キーを取得。 |
| `MessageDigest`（SHA-256） | `PasswordUtil.java` | `MessageDigest.getInstance("SHA-256")` — Java 標準 API でパスワードをハッシュ化。BCrypt 等の外部依存なしに実装。 |
| `Logger`（java.util.logging） | `DatabaseInitializer.java` `LoanService.java` `ItemService.java` | `Logger.getLogger(Foo.class.getName())` — JUL（Java Util Logging）を使用。外部ロギングライブラリなし。 |
| `synchronized` メソッド | `DatabaseInitializer.java` | `public static synchronized void initializeIfNeeded()` — DB 初期化を複数スレッドから呼ばれても 1 回だけ実行するよう同期。 |
| JDBC トランザクション（手動コミット） | `LoanService.java` | `connection.setAutoCommit(false); ... connection.commit();` — 貸出・返却で ITEMS と LOANS を同一トランザクション内で更新。楽観的排他（VERSION）と組み合わせ。 |

---

## ❌ 使わなかった Java 8 の新機能（縛りの核心）

| 機能 | 導入バージョン | 使わなかった理由・代替手段 |
|---|---|---|
| ラムダ式（`->`） | Java 8 | コレクション操作はすべて従来の `for` ループで記述。匿名クラスも不使用。 |
| Stream API（`.stream().filter()...`） | Java 8 | 一覧の絞り込み（例: 自分の ACTIVE 貸出）は `for` + `if` で実装（`MyPageServlet`）。 |
| `Optional<T>` | Java 8 | null チェックはすべて `if (x == null)` で記述。 |
| デフォルトメソッド（interface の `default`） | Java 8 | インターフェースは定義せず、具象クラス直接実装。 |
| メソッド参照（`::`） | Java 8 | ラムダ・Stream を使わないため不使用。 |
| Date/Time API（`LocalDate` など） | Java 8 | 日付は `java.sql.Date` と文字列（`"yyyy-MM-dd"`）で処理。新しい API は使わず。 |
| `Map.forEach()` / `Map.getOrDefault()` | Java 8 | Map の操作は `entrySet()` ループや `containsKey()` で記述。 |
| Collectors / 関数型インターフェース | Java 8 | `Predicate` / `Function` 等は一切使用なし。 |

---

## 📌 補足：外部依存を避けた選択

| 項目 | 選択内容 |
|---|---|
| パスワードハッシュ | BCrypt（外部ライブラリ）の代わりに `MessageDigest`（SHA-256）を採用。依存ゼロで Java 標準のみ。 |
| ロギング | SLF4J / Logback / Log4j の代わりに `java.util.logging`（JUL）を使用。 |
| DI / フレームワーク | Spring / CDI / Guice なし。Servlet の `new` で直接インスタンス生成。 |
| ORM | JPA / Hibernate / MyBatis なし。生 JDBC + `PreparedStatement` で実装。 |
