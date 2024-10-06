# bookShelf
書籍管理システムのバックエンドAPI

# 目次
- [使用技術](#使用技術)
- [起動手順](#起動手順)
- [APIリファレンス](#APIリファレンス)

# 使用技術
- Kotlin version '1.9.25'
- Spring Boot version '3.3.4'
- jOOQ version '3.19.13'
- Flyway version '9.16.0'
- Postgres version '42.7.4'


# 起動手順
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

# APIリファレンス
以下は、APIの使用例です。
詳細については、各APIエンドポイントのリンクをクリックしてください。
<details><summary>書籍</summary>
<details><summary>全書籍取得</summary>

- **リクエスト**:
  `GET /api/books/`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例**:
  ```bash
   curl http://localhost:8080/api/books/
</details>

<details><summary>著者IDによる書籍取得</summary>

- **リクエスト**:
  `GET /api/books/{authorId}`
- **パラメータ**:
  authorId
- **レスポンス**:
  200 OK, JSON形式の書籍リスト 
- **cURLでの実行例(PowerShell)**:
  ```bash
  curl http://localhost:8080/api/books/1
</details>

<details><summary>書籍の追加</summary>

- **リクエスト**:
  `POST /api/books/add`
- **パラメータ**:
   ```bash
  {
    "title": "書籍タイトル",
    "price": "1000",
    "authorId": 1,
    "publicationStatus":true
   }
- **レスポンス**:
  200 OK, 追加後の書籍情報
- **cURLでの実行例(PowerShell)**:
  ```bash
  $headers = @{
    "accept" = "application/json"
    "Content-Type" = "application/json"
  }
  
  $body = '{
    "title": "書籍タイトル",
    "price": 1000,
    "authorId": 1,
    "publicationStatus": true
  }'
  
  Invoke-WebRequest -Uri "http://localhost:8080/api/books/add" -Method POST -Headers $headers -Body $body
</details>

<details><summary>書籍の更新</summary>

- **リクエスト**:
  `PUT /api/books/update/`
- **パラメータ**:
  ```bash
  {
    "id": 1,
    "title": "書籍タイトル",
    "price": "1000",
    "authorId": 1,
    "publicationStatus":true
  }
- **レスポンス**:
  200 OK, 更新後の書籍情報
- **cURLでの実行例(PowerShell)**:
  ```bash
  $headers = @{
    "accept" = "application/json"
    "Content-Type" = "application/json"
  }
  
  $body = '{
    "bookId": 1,
    "title": "新しい書籍タイトル",
    "price": 1500,
    "authorId": 1,
    "publicationStatus": true
  }'
  
  Invoke-WebRequest -Uri "http://localhost:8080/api/books/update" -Method PUT -Headers $headers -Body $body

</details>
</details>

<details><summary>書籍</summary>
<details><summary>全著者取得</summary>

- **リクエスト**:
  `GET /api/books/`
- **パラメータ**:
なし
- **レスポンス**:
  200 OK, JSON形式の著者リスト
- **cURLでの実行例(PowerShell)**:
  ```bash
  curl http://localhost:8080/api/author/
</details>


<details><summary>著者の追加</summary>

- **リクエスト**:
  `POST /api/author/add/`
- **パラメータ**:
  ```bash
  {
    "name": "著者名",
    "birthday": "2024-10-01"
   }
- **レスポンス**:
  200 OK, 追加後の著者情報
- **cURLでの実行例(PowerShell)**:
  ```bash
  $headers = @{
    "accept" = "application/json"
    "Content-Type" = "application/json"
  }
  
  $body = '{
    "name": "新しい著者名",
    "birthday": "1980-05-15"
  }'
  
  Invoke-WebRequest -Uri "http://localhost:8080/api/author/add" -Method POST -Headers $headers -Body $body

</details>

<details><summary>著者の更新</summary>

- **リクエスト**:
  `PUT /api/author/update`
- **パラメータ**:
  ```bash
  {
    "authorId": 1,
    "name": "変更後の著者名",
    "birthday": "2023-11-11"
  }
- **レスポンス**:
  200 OK, 更新後の著者情報
- **cURLでの実行例(PowerShell)**:
  ```bash
  $headers = @{
    "accept" = "application/json"
    "Content-Type" = "application/json"
  }
  
  $body = 
    '{
    "authorId": 1,
    "name": "変更後の著者名",
    "birthday": "2023-11-11"
  }'
  $bodyJson = $body | ConvertTo-Json
  
  Invoke-RestMethod -Uri "http://localhost:8080/api/author/update" -Method Put -Headers $headers -Body $body
</details>
</details>
