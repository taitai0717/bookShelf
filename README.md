# bookShelf
書籍管理システムのバックエンドAPI

## 使用方法
下記手順を実行してください：

1. リポジトリをクローンする：
   ```bash
   git clone https://github.com/poulenc51/Bookshelf.git

2. プロジェクトディレクトリに移動する：
    ```bash
   cd bookShelf

3. dockerを起動する :
    ```bash
    docker-compose up -d

4. DBマイグレーションを実行する：
    ```bash
    ./gradlew flywayMigrate

5. jOOQコードによるコード生成を実行する：
    ```bash
    ./gradlew jooqCodegen

6. アプリケーションを実行する :
    ```bash
    ./gradlew bootRun

## API一覧
以下は、APIの使用例です。詳細なドキュメントは、各APIエンドポイントのリンクをクリックしてください。

<details><summary>全書籍取得</summary>

- **リクエスト**:
  `GET /api/books/`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
  curl -X GET "http://localhost:8080/api/books/ -H "accept: application/json"
</details>

<details><summary>著者IDによる書籍取得</summary>

- **リクエスト**:
  `GET /api/books/{authorId}`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
  curl -X GET "http://localhost:8080/api/books/1 -H "accept: application/json"
</details>

<details><summary>書籍の追加</summary>

- **リクエスト**:
  `POST /api/books/add`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
  curl -X GET "http://localhost:8080/api/book/add/1 -H "accept: application/json"
</details>

<details><summary>書籍の更新</summary>

- **リクエスト**:
  `PUT /api/books/update/`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
  curl -X GET "http://localhost:8080/api/books/update/ -H "accept: application/json"
</details>



<details><summary>全著者取得</summary>

- **リクエスト**:
  `GET /api/books/`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
  curl -X GET "http://localhost:8080/api/books/ -H "accept: application/json"
</details>


<details><summary>著者の追加</summary>

- **リクエスト**:
  `POST /api/books/add`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
  curl -X GET "http://localhost:8080/api/book/add/1 -H "accept: application/json"
</details>

<details><summary>著者の更新</summary>

- **リクエスト**:
  `PUT /api/books/update/`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
  curl -X GET "http://localhost:8080/api/books/update/ -H "accept: application/json"
</details>

## 使用技術
- Kotlin version '1.9.25'
- Spring Boot version '3.3.4'
- jOOQ version '3.19.13'  
- Flyway version '9.16.0'
- Postgres version '42.7.4'
