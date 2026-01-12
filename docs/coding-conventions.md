# コーディング規約（Mad Hatter Android）

本ドキュメントは、現行のコードベース構成（`app` / `home` / `core` など）と一般的な Android プロジェクト慣例を踏まえたコーディング規約です。

## 1. プロジェクト構成

- **モジュール分割**
  - `app`: アプリのエントリーポイント（`MainActivity`）とアプリ全体のテーマ・設定。
  - `home`: 画面単位の UI/Feature モジュール（例: `HomeScreen`）。
  - `core`: ドメインモデル、リポジトリ、DB/SQLDelight などの共通ロジック。
- **パッケージ命名**
  - ルートは `com.example.madhatter` を基準に、モジュールごとに `com.example.madhatter.<module>` とする。
  - 機能単位のパッケージを優先し、`ui`, `repository`, `di`, `database` などの役割別を明示する。

## 2. 命名規則

- **クラス/インターフェース/オブジェクト**: `UpperCamelCase`
  - 例: `HomeScreen`, `SqlDelightCategoryRepository`, `MetroDi`
- **関数/プロパティ/変数**: `lowerCamelCase`
  - 例: `getById`, `transactionRepository`
- **定数**: `UPPER_SNAKE_CASE`
- **SQLDelight 生成型との衝突回避**
  - SQLDelight の生成型は `DbCategory` のように `as` でエイリアスを使用する。

## 3. Kotlin コーディングスタイル

- **インデント**: 4 スペース。
- **改行/カンマ**: 複数行の引数・パラメータは**末尾カンマ**を付ける。
- **null 安全**: 可能な限り `?` を明示し、`requireNotNull` / `check` で早期失敗。
- **可視性は最小限に**:
  - 画面の公開 API 以外は `private` / `internal` を基本とする。
  - `Composable` の内部構成要素や変換関数（`toDomain()` など）は `private` に閉じる。
- **可読性優先**
  - ガード節・戻り値の早期リターンを活用する。
  - 長い処理は関数分割する。

## 4. Jetpack Compose

- **Composable 関数**
  - UI 要素は `@Composable` で明示し、画面のエントリポイントをパブリックに、内部の構成要素は `private` に分離する。
- **Modifier の扱い**
  - `modifier: Modifier = Modifier` を引数の先頭または末尾に定義し、必要に応じて呼び出し側から注入可能にする。
- **テーマ/Typography**
  - `MaterialTheme` の `colorScheme` / `typography` を優先利用する。

## 5. ドメイン/データレイヤ

- **データモデル**
  - `data class` を基本とし、`core` モジュールに配置する。
- **Repository パターン**
  - `CategoryRepository` / `TransactionRepository` のようにインターフェースを定義。
  - 実装は `SqlDelightCategoryRepository` のように `SqlDelight` などのストレージ実装を明示。
- **SQLDelight**
  - `.sq` ファイルは `core/src/main/sqldelight` 配下にまとめる。
  - DB 型からドメインへの変換関数（`toDomain()`）は `private` にし、ファイル内で閉じる。

## 6. DI（依存関係の解決）

- **MetroDi を中心に管理**
  - 初期化は `initialize(Context)` のみ。
  - `check` / `requireNotNull` を使用して未初期化時は明示的に失敗させる。
- **依存関係の注入**
  - 画面や ViewModel からの依存取得は `MetroDi` を経由する（現行構成に合わせる）。

## 7. リソース/文字列

- **UI テキスト**は可能な限り `strings.xml` へ移動する。
- 色やタイポグラフィは `ui/theme` に集約する。

## 8. Gradle/依存管理

- バージョン管理は `libs.versions.toml` に集約する。
- 新規依存追加時は `libs` を経由し、モジュール直書きを避ける。

## 9. チェック/ビルド

- 変更後は `./gradlew assembleDebug` を実行し、デバッグビルドが通ることを確認する。
