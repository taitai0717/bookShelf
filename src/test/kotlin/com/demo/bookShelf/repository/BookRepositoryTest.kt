package com.demo.bookShelf.repository

import com.demo.bookShelf.model.Book
import org.assertj.core.api.Assertions
import org.example.db.tables.Books.BOOKS
import org.example.db.tables.records.BooksRecord
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
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

    fun setupTestData() {
        dsl.insertInto(BOOKS)
            .columns(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.AUTHOR_ID, BOOKS.PUBLICATION_STATUS)
            .values(1, "テスト書籍１", 1000, 1, false)
            .values(2, "テスト書籍２", 2000, 1, false)
            .values(3, "テスト書籍３", 3000, 2, false)
            .values(4, "テスト書籍４", 4000, 3, false)
            .values(5, "テスト書籍５", 5000, 4, false)
            .execute()
        // DSLContext インスタンスを利用して SQL を直接実行
        dsl.execute("SELECT setval('books_id_seq', 6, false)")
    }

    @AfterEach
    fun cleanup() {
        dsl.delete(BOOKS).execute()


    }

    @Test
    fun `findById should return Book when ID is found and not deleted`() {
        setupTestData()

        val bookId = 1
        val expectedBook = Book(1, "テスト書籍１", 1000, 1, false)

        val result = bookRepository.findById(bookId)

        Assertions.assertThat(result).isEqualTo(expectedBook)
    }


    @Test
    fun `findById should return null when ID is not found`() {
        setupTestData()

        val bookId = 999

        val result = bookRepository.findById(bookId)

        Assertions.assertThat(result).isNull()
    }

    @Test
    fun `findAllByNotDeleted should return list of Books`() {
        setupTestData()

        val result = bookRepository.findAll()

        Assertions.assertThat(result).isNotEmpty
        Assertions.assertThat(result.size).isEqualTo(5)
    }

    @Test
    fun `saveBook should insert new record when id is null`() {
        setupTestData()
        val newBook = org.example.db.tables.pojos.Books()
        // 受け取ったデータをPOJOのフィールドに設定
        newBook.setTitle("新規登録書籍")
        newBook.setPrice(8000)
        newBook.setAuthorId(5)
        newBook.setPublicationStatus(false)
        // リポジトリを通じて書籍情報を保存
        bookRepository.saveBook(
            BooksRecord(newBook)
        )

        val insertedRecord = dsl.selectFrom(BOOKS).where(BOOKS.ID.eq(6)).fetchOne()

        Assertions.assertThat(insertedRecord).isNotNull
        Assertions.assertThat(insertedRecord!!.id).isEqualTo(6)
        Assertions.assertThat(insertedRecord.title).isEqualTo("新規登録書籍")
        Assertions.assertThat(insertedRecord.price).isEqualTo(8000)
        Assertions.assertThat(insertedRecord.authorId).isEqualTo(5)
        Assertions.assertThat(insertedRecord.publicationStatus).isEqualTo(false)
    }

    @Test
    fun `saveBook should update existing record when id is not null`() {
        setupTestData()

        val existingBook = org.example.db.tables.pojos.Books()
        // 受け取ったデータをPOJOのフィールドに設定

        existingBook.setId(2)
        existingBook.setTitle("既存登録書籍")
        existingBook.setPrice(10000)
        existingBook.setAuthorId(2)
        existingBook.setPublicationStatus(true)
        // リポジトリを通じて書籍情報を保存
        bookRepository.saveBook(BooksRecord(existingBook))

        val updatedRecord = dsl.selectFrom(BOOKS).where(BOOKS.ID.eq(2)).fetchOne()

        Assertions.assertThat(updatedRecord).isNotNull
        Assertions.assertThat(updatedRecord!!.id).isEqualTo(2)
        Assertions.assertThat(updatedRecord.title).isEqualTo("既存登録書籍")
        Assertions.assertThat(updatedRecord.price).isEqualTo(10000)
        Assertions.assertThat(updatedRecord.authorId).isEqualTo(2)
        Assertions.assertThat(updatedRecord.publicationStatus).isEqualTo(true)

    }

    @Test
    fun `findAllByAuthorIdAndNotDeleted should return list of Books when found and contains not deleted`() {
        setupTestData()

        val authorId = 1

        val result = bookRepository.findAllByAuthorId(authorId)

        Assertions.assertThat(result).isNotEmpty
        Assertions.assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun `findAllByAuthorIdAndNotDeleted should return list of Books when found and contains deleted`() {
        setupTestData()

        val authorId = 999

        val result = bookRepository.findAllByAuthorId(authorId)

        Assertions.assertThat(result).isEmpty()
    }

}
