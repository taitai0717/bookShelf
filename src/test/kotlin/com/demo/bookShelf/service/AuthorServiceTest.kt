package com.demo.bookShelf.service

import com.demo.bookShelf.dto.response.AuthorDto
import com.demo.bookShelf.model.Author
import com.demo.bookShelf.repository.AuthorRepository
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
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class AuthorServiceTest {

    @Autowired
    private lateinit var dsl: DSLContext

    @MockBean
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var authorService: AuthorService

    /**
     * 各テスト終了後にデータをクリーンアップ
     */
    @AfterEach
    fun cleanup() {
        dsl.delete(AUTHOR).execute()
    }

    @Test
    fun `findAuthorById returns AuthorDto for existing author`() {

        val authorId = 1
        val expectedAuthor = AuthorDto(id = authorId, name = "テスト著者１", birthday = LocalDate.of(2024, 1, 10))
        `when`(authorRepository.findById(authorId)).thenReturn(
            Author(
                id = 1,
                name = "テスト著者１",
                birthday = LocalDate.of(2024, 1, 10)
            )
        )

        val result = authorService.findAuthorById(authorId)

        Assertions.assertThat(result).isEqualTo(expectedAuthor)
    }

    @Test
    fun `findAuthorById returns AuthorDto `() {

        val authorId = 1
        `when`(authorRepository.findById(authorId)).thenReturn(null)

        val result = authorService.findAuthorById(authorId)

        Assertions.assertThat(result).isNull()
    }

    @Test
    fun `findAuthorById returns  `() {

        `when`(authorRepository.findAll()).thenReturn(
            listOf(
                Author(
                    id = 1,
                    name = "テスト著者１",
                    birthday = LocalDate.of(2000, 1, 1)
                ), Author(
                    id = 2,
                    name = "テスト著者２",
                    birthday = LocalDate.of(2001, 2, 11)
                ), Author(
                    id = 3,
                    name = "テスト著者３",
                    birthday = LocalDate.of(2002, 3, 21)
                )
            )
        )

        val result = authorService.findAllAuthors()
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result.authorList.size).isEqualTo(3)
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
