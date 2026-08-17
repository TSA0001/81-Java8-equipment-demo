# Claude によるビルド・テスト実行結果

> **本ドキュメントについて**
> 本結果は AI アシスタント **Claude（Anthropic, Sonnet 5）** が実際に `podman-compose` を用いてコンテナ内 Java 8 でビルド・単体テスト・起動確認を実行した記録です。
> 静的なコードレビューである [`claude-evaluation-2026-08-10.md`](claude-evaluation-2026-08-10.md) の内容を、実際の実行結果として裏付けるために作成しました。
> 実行日: 2026-08-10

---

## 実行環境

このホスト（macOS）には Podman が未インストールだったため、`brew install podman podman-compose` で新規インストールし、`podman machine init` で新規 Machine（`podman-machine-default`）を作成・起動した上で実行しました。既存の Machine / Volume はホスト上に存在しなかったため、削除・上書きは発生していません。

| 項目 | 値 |
|---|---|
| podman | 6.0.2 |
| podman-compose | 1.6.0 |
| Podman Machine | `podman-machine-default`（applehv, 5 CPU, 2GiB, 新規作成） |
| ビルドコマンド | `BUILDAH_FORMAT=docker podman-compose up --build -d` |
| ビルドステージ Java | `eclipse-temurin:8-jdk` |
| 実行ステージ Java | `eclipse-temurin:8-jre` |
| Tomcat | 9.0.98 |

## 1. Maven ビルド（コンテナ内、Java 8）

```
[INFO] BUILD SUCCESS
[INFO] Total time:  8.239 s
```

WAR (`equipment-management.war`) の生成に成功。

## 2. 単体テスト結果（コンテナ内、Java 8、`mvn -B -DskipTests=false clean package` 実行時）

| クラス | 結果 |
|---|---|
| `LoanServiceTest` | Tests run: 4, Failures: 0, Errors: 0 |
| `ItemFormValidatorTest` | Tests run: 5, Failures: 0, Errors: 0 |
| `UserDaoTest` | Tests run: 5, Failures: 0, Errors: 0 |
| `ItemDaoTest` | Tests run: 4, Failures: 0, Errors: 0 |
| `PasswordUtilTest` | Tests run: 8, Failures: 0, Errors: 0 |
| `AdminFilterTest` | Tests run: 3, Failures: 0, Errors: 0 |
| `AuthFilterTest` | Tests run: 5, Failures: 0, Errors: 0 |
| `SmokeTest` | Tests run: 1, Failures: 0, Errors: 0 |

**合計: `Tests run: 35, Failures: 0, Errors: 0, Skipped: 0`**

`docs/remaining-work.md` に記載の「35 tests」と一致し、全件成功。

## 3. コンテナイメージ

```
localhost/equipment-management_equipment-app  latest  352 MB
```

マルチステージビルドにより、最終イメージには Maven やソースは含まれず JRE 8 + Tomcat + WAR のみ（想定通り）。

## 4. コンテナ内 Java バージョン確認

```
$ podman exec equipment-app java -version
openjdk version "1.8.0_492"
OpenJDK Runtime Environment (Temurin)(build 1.8.0_492-b09)
OpenJDK 64-Bit Server VM (Temurin)(build 25.492-b09, mixed mode)
```

`1.8.x` であることを確認（README §「コンテナ内の Java 確認」の完了条件を満たす）。

Tomcat 起動ログでも一致:
```
JVM Version:           1.8.0_492-b09
JVM Vendor:            Temurin
Server version number: 9.0.98.0
```

## 5. コンテナ状態

```
$ podman ps -a
CONTAINER ID  IMAGE                                                 STATUS                   PORTS                   NAMES
964779025c9d  localhost/equipment-management_equipment-app:latest  Up (healthy)             0.0.0.0:8080->8080/tcp  equipment-app
```

HEALTHCHECK（`/health` への curl、15秒間隔）が `healthy` を報告。

## 6. HTTP 疎通確認

| URL | 期待値 | 結果 |
|---|---|---|
| `http://127.0.0.1:8080/equipment-management/health` | `OK` (200) | `OK` / `HTTP_STATUS:200` ✅ |
| `http://127.0.0.1:8080/equipment-management/login` | ログイン画面 (200) | `HTTP_STATUS:200` ✅ |
| `http://127.0.0.1:8080/equipment-management/` | 未ログイン時 `/login` へリダイレクト (302) | `HTTP_STATUS:302` ✅ |

`AuthFilter` によるリダイレクト動作、`DatabaseInitializer` による H2 スキーマ・シード投入（起動ログで確認）も正常に機能。

## 総合結果

README §「最初の完了条件」相当の項目をすべて満たすことを実機（Podman コンテナ）で確認した:

- [x] Podman でビルド可能、コンテナ内 Java 8
- [x] Tomcat 起動、コンテナ 1 個
- [x] Maven WAR ビルド成功
- [x] Maven ユニットテスト 35 件全成功
- [x] `localhost:8080` から到達、ヘルスチェック `healthy`
- [x] Docker Desktop 不要（Podman のみで完結）

## 補足・後片付けについて

検証後もコンテナ（`equipment-app`）とボリューム（`equipment-db`）は起動したままにしてあります。停止する場合は以下（ボリュームは削除されない）:

```bash
cd equipment-management
podman-compose down
```

ボリュームを含め完全に削除する場合は明示的な指示がある場合のみ実施してください（データが失われます）。
