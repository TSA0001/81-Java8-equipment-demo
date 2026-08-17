# 備品管理システム

Java 8 / Servlet / JSP による社内備品管理 Web アプリケーション。

## 注意

- 本リポジトリは学習用・コードデモ用です。本番運用を前提としていません。
- 初期ログイン ID / パスワードはデモ用の固定値です。実運用環境では使用しないでください。
- パスワード保存方式は開発・検証向けの簡易実装です。本番ではより強い方式に置き換えてください。
- CSRF 対策など、実サービスとしては追加実装が必要な項目があります。

## 技術構成

| 項目 | 版・内容 |
|------|----------|
| Java | 8（Eclipse Temurin） |
| Web | Servlet 4.0 + JSP + JSTL 1.2 |
| コンテナ | Apache Tomcat 9.0.98 |
| ビルド | Maven 3.9.9（コンテナイメージ内） |
| 実行環境 | Podman（単一コンテナ） |
| DB | H2（ファイル DB、Volume 永続化） |

Spring / JSF / JPA は使用しません。

## 前提

- macOS
- Podman 5.x
- Podman Machine（既存 Machine を利用。削除・再作成しない）

### Podman Machine の確認・起動

```bash
podman machine list
podman machine start podman-machine-default
podman system connection default podman-machine-default
```

本作業では `podman-machine-default` を使用します。  
`equipment-dev` は初回起動が emergency mode のため、現状は使いません。

Compose は本環境では `podman-compose` を使用します。

## ビルドと起動

```bash
cd equipment-management
# HEALTHCHECK を有効にするため docker 形式でビルドする
BUILDAH_FORMAT=docker podman-compose up --build -d
```

## アクセス URL

| URL | 内容 | 権限 |
|-----|------|------|
| http://localhost:8080/equipment-management/ | トップ（`/home` へリダイレクト） | ログイン必須 |
| http://localhost:8080/equipment-management/login | ログイン画面 | 誰でも |
| http://localhost:8080/equipment-management/home | トップ画面 | ログイン必須 |
| http://localhost:8080/equipment-management/items | 備品一覧・検索 | ログイン必須 |
| http://localhost:8080/equipment-management/items/new | 備品登録 | 管理者のみ |
| http://localhost:8080/equipment-management/items/detail?id= | 備品詳細 | ログイン必須 |
| http://localhost:8080/equipment-management/items/edit?id= | 備品編集 | 管理者のみ |
| http://localhost:8080/equipment-management/items/delete?id= | 備品削除確認 | 管理者のみ |
| http://localhost:8080/equipment-management/loans/new?itemId= | 貸出 | ログイン必須 |
| http://localhost:8080/equipment-management/loans/return?itemId= | 返却 | ログイン必須 |
| http://localhost:8080/equipment-management/loans | 全貸出履歴 | 管理者のみ |
| http://localhost:8080/equipment-management/loans?mode=active | 全貸出中一覧 | 管理者のみ |
| http://localhost:8080/equipment-management/mypage/loans | 自分の貸出中一覧 | ログイン必須 |
| http://localhost:8080/equipment-management/mypage/history | 自分の貸出履歴 | ログイン必須 |
| http://localhost:8080/equipment-management/logout | ログアウト | ログイン必須 |
| http://localhost:8080/equipment-management/health | ヘルスチェック | 誰でも |

## 初期ユーザー

| ログイン ID | パスワード | 権限 | 表示名 |
|-------------|-----------|------|--------|
| `admin` | `admin123` | 管理者 | 管理者 |
| `user1` | `user1234` | 一般利用者 | 一般 太郎 |

> パスワードは SHA-256 ハッシュで保存しています（開発・検証用）。

## 実装済み機能

- **認証・認可**
  - ログイン / ログアウト（セッション固定化攻撃対策済み）
  - 未ログイン時は `/login` へリダイレクト（`AuthFilter`）
  - 管理者専用操作は `AdminFilter` および各 Servlet で二重チェック
- **備品管理（管理者）**
  - 備品登録（確認→完了フロー）
  - 備品編集
  - 論理削除
- **備品管理（全ログインユーザー）**
  - 備品一覧・検索（管理番号・備品名・カテゴリ・保管場所・状態）
  - 備品詳細
- **貸出・返却**
  - 利用可能備品の貸出
  - 返却（状態を「返却済み / 修理中」から選択）
  - 排他更新（VERSION）
- **履歴・マイページ**
  - 全貸出履歴 / 全貸出中一覧（管理者専用）
  - 自分の貸出中一覧・全履歴（マイページ）
  - 期限超過の識別
- **基盤**
  - H2 ファイル DB（`DB_PATH=/data/h2/equipment`、Volume 永続化）
  - 入力検証（管理番号形式、必須、日付など）

## よく使うコマンド

```bash
# 状態
podman-compose ps

# ログ
podman-compose logs -f
# または
podman logs -f equipment-app

# 停止（Volume は削除しない）
podman-compose down

# 再ビルド
podman-compose up --build -d
```

## コンテナ内の Java 確認

```bash
podman exec equipment-app java -version
```

`1.8.x` であること。

## Maven テスト（イメージビルド時）

Containerfile のビルドステージで `mvn clean package`（テスト含む）を実行します。

ローカルでテストのみ実行する場合:

```bash
cd equipment-management
mvn clean test
```

## ディレクトリ構成（抜粋）

```
equipment-management/
├─ Containerfile
├─ compose.yaml
├─ pom.xml
├─ src/main/java/.../
│   ├─ dao/        # DB アクセス（JDBC）
│   ├─ model/      # ドメインモデル / フォーム
│   ├─ service/    # ビジネスロジック
│   ├─ util/       # DB 初期化 / 接続 / パスワードハッシュ
│   ├─ validation/ # 入力検証
│   └─ web/        # Servlet / Filter
├─ src/main/resources/db/  # schema.sql / seed.sql
├─ src/main/webapp/        # JSP / CSS
└─ src/test/java/          # JUnit 4
```

## Volume について

`equipment-db` Volume は H2 永続化用です。  
`podman-compose down` では Volume を削除しません。  
**Volume を削除するとデータが失われます。明示指示がある場合のみ削除してください。**

## トラブルシューティング

- `connection refused` / Podman socket エラー → `podman machine start` を実行
- 8080 が使えない → 他プロセスを確認し、必要なら compose のポートを変更
- ヘルスチェック失敗 → `podman logs equipment-app` で Tomcat 起動を確認
