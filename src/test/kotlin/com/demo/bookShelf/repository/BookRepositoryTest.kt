package com.demo.bookShelf.repository

import com.demo.bookShelf.model.Book
import org.assertj.core.api.Assertions
import org.example.db.tables.Books.BOOKS
import org.example.db.tables.records.BooksRecord
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var bookRepository: BookRepository

    // シーケンス保持用の変数
    private var currentSeq: Int = 0

    // テストデータのセットアップ
    @BeforeEach
    fun setupTestData() {
        // テスト前のシーケンスを取得
        currentSeq = (dsl.fetchValue("SELECT nextval('books_id_seq')") as Long).toInt()
        dsl.insertInto(BOOKS)
            .columns(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AUTHOR_ID, BOOKS.PUBLICATION_STATUS)
            .values(currentSeq + 1, "テスト書籍１", 1000, 1, false)
            .values(currentSeq + 2, "テスト書籍２", 2000, 1, false)
            .values(currentSeq + 3, "テスト書籍３", 3000, 2, false)
            .values(currentSeq + 4, "テスト書籍４", 4000, 3, false)
            .values(currentSeq + 5, "テスト書籍５", 5000, 4, false)
            .execute()
        // テストデータ登録後のシーケンスを設定
        dsl.execute("SELECT setval('books_id_seq', ${currentSeq + 6}, false)")
    }

    @AfterEach
    fun cleanup() {
        // テストデータ削除
        dsl.delete(BOOKS).execute()
        // シーケンスをテスト前に戻す
        dsl.execute("SELECT setval('books_id_seq', ${currentSeq}, false)")

    }

    @Test
    fun `findById should return Book when ID is found and not deleted`() {

        val bookId = currentSeq + 1
        val expectedBook = Book(bookId, "テスト書籍１", 1000, 1, false)

        val result = bookRepository.findById(bookId)

        Assertions.assertThat(result).isEqualTo(expectedBook)
    }


    @Test
    fun `findById should return null when ID is not found`() {

        val bookId = 2147483647

        val result = bookRepository.findById(bookId)

        Assertions.assertThat(result).isNull()
    }

    @Test
    fun `findAllByNotDeleted should return list of Books`() {

        val result = bookRepository.findAll()

        Assertions.assertThat(result).isNotEmpty
        Assertions.assertThat(result.size).isEqualTo(5)
    }

    @Test
    fun `saveBook should insert new record when id is null`() {
        val newBook = org.example.db.tables.pojos.Books()
        // 受け取ったデータをPOJOのフィールドに設定
        newBook.title = "新規登録書籍"
        newBook.price = 8000
        newBook.authorId = 5
        newBook.publicationStatus = false
        // リポジトリを通じて書籍情報を保存
        bookRepository.saveBook(
            BooksRecord(newBook)
        )
        val bookId = currentSeq + 6
        val insertedRecord = dsl.selectFrom(BOOKS).where(BOOKS.ID.eq(bookId)).fetchOne()

        Assertions.assertThat(insertedRecord).isNotNull
        Assertions.assertThat(insertedRecord!!.id).isEqualTo(bookId)
        Assertions.assertThat(insertedRecord.title).isEqualTo("新規登録書籍")
        Assertions.assertThat(insertedRecord.price).isEqualTo(8000)
        Assertions.assertThat(insertedRecord.authorId).isEqualTo(5)
        Assertions.assertThat(insertedRecord.publicationStatus).isEqualTo(false)
    }

    @Test
    fun `saveBook should update existing record when id is not null`() {

        val existingBook = org.example.db.tables.pojos.Books()
        // 受け取ったデータをPOJOのフィールドに設定
        val bookId = currentSeq + 2
        existingBook.id = bookId
        existingBook.title = "既存登録書籍"
        existingBook.price = 10000
        existingBook.authorId = 2
        existingBook.publicationStatus = true
        // リポジトリを通じて書籍情報を保存
        bookRepository.saveBook(BooksRecord(existingBook))

        val updatedRecord = dsl.selectFrom(BOOKS).where(BOOKS.ID.eq(bookId)).fetchOne()

        Assertions.assertThat(updatedRecord).isNotNull
        Assertions.assertThat(updatedRecord!!.id).isEqualTo(bookId)
        Assertions.assertThat(updatedRecord.title).isEqualTo("既存登録書籍")
        Assertions.assertThat(updatedRecord.price).isEqualTo(10000)
        Assertions.assertThat(updatedRecord.authorId).isEqualTo(2)
        Assertions.assertThat(updatedRecord.publicationStatus).isEqualTo(true)

    }

    @Test
    fun `findAllByAuthorIdAndNotDeleted should return list of Books when found and contains not deleted`() {
        val authorId = 1

        val result = bookRepository.findAllByAuthorId(authorId)

        Assertions.assertThat(result).isNotEmpty
        Assertions.assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun `findAllByAuthorIdAndNotDeleted should return list of Books when found and contains deleted`() {
        val authorId = 999

        val result = bookRepository.findAllByAuthorId(authorId)

        Assertions.assertThat(result).isEmpty()
    }

}
