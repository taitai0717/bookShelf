package com.demo.bookShelf.service

import com.demo.bookShelf.dto.response.BookDto
import com.demo.bookShelf.model.Book
import com.demo.bookShelf.repository.BookRepository
import org.assertj.core.api.Assertions
import org.example.db.tables.Author.AUTHOR
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class BookServiceTest {

    @Autowired
    private lateinit var dsl: DSLContext

    @MockBean
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var bookService: BookService

    /**
     * 各テスト終了後にデータをクリーンアップ
     */
    @AfterEach
    fun cleanup() {
        dsl.delete(AUTHOR).execute()
    }

    @Test
    fun `findAuthorById returns AuthorDto for existing author`() {

        val bookId = 1
        `when`(bookRepository.findById(bookId)).thenReturn(
            Book(
                id = 1,
                title = "テストタイトル１",
                price = 1000,
                authorId = 2,
                publicationStatus = true
            )
        )

        val expectedBook =
            BookDto(id = bookId, title = "テストタイトル１", price = 1000, authorId = 2, publicationStatus = true)
        val result = bookService.findBookById(bookId)

        Assertions.assertThat(result).isEqualTo(expectedBook)
    }

    @Test
    fun `findAuthorById returns AuthorDto for existing `() {

        `when`(bookRepository.findAll()).thenReturn(
            listOf(
                Book(
                    id = 1,
                    title = "テストタイトル１",
                    price = 1000,
                    authorId = 1,
                    publicationStatus = true
                ), Book(
                    id = 2,
                    title = "テストタイトル２",
                    price = 2000,
                    authorId = 2,
                    publicationStatus = true
                ), Book(
                    id = 3,
                    title = "テストタイトル３",
                    price = 3000,
                    authorId = 3,
                    publicationStatus = true
                )
            )


        )

        val result = bookService.findAllBooks()
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result.bookList.size).isEqualTo(3)
    }

    @Test
    fun `findBooksByAuthorId returns AuthorDto for existing `() {

        val authorId = 3
        `when`(bookRepository.findAllByAuthorId(authorId)).thenReturn(
            listOf(
                Book(
                    id = 1,
                    title = "テストタイトル１",
                    price = 1000,
                    authorId = 3,
                    publicationStatus = true
                ), Book(
                    id = 2,
                    title = "テストタイトル２",
                    price = 2000,
                    authorId = 3,
                    publicationStatus = true
                ), Book(
                    id = 3,
                    title = "テストタイトル３",
                    price = 3000,
                    authorId = 3,
                    publicationStatus = true
                )
            )


        )

        val result = bookService.findBooksByAuthorId(authorId)
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result.bookList.size).isEqualTo(3)
    }


    @Test
    fun `addAuthor  `() {

        // テスト未実装

    }

    @Test
    fun `updateAuthor  `() {

        // テスト未実装
    }

}
