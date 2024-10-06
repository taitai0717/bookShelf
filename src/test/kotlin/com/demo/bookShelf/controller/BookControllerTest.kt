package com.demo.bookShelf.controller

import com.demo.bookShelf.dto.response.AuthorDto
import com.demo.bookShelf.dto.response.BookDto
import com.demo.bookShelf.dto.response.BookListDto
import com.demo.bookShelf.service.AuthorService
import com.demo.bookShelf.service.BookService
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var bookService: BookService

    @MockBean
    private lateinit var authorService: AuthorService

    @Test
    fun `get book test`() {
        // 書籍取得のテスト
        // テスト用の書籍リストを準備し、モックを設定
        `when`(bookService.findAllBooks()).thenReturn(
            BookListDto(
                bookList = listOf(
                    BookDto(1, "テストタイトル１", 1000, 1, true),
                    BookDto(2, "テストタイトル２", 2000, 2, false),
                    BookDto(3, "テストタイトル３", 3000, 3, true)
                )
            )
        )
        // 著者リスト取得APIのテスト
        mockMvc.perform(get("/api/books/"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.bookList[2].title", equalTo("テストタイトル３")))
    }


    @Test
    fun `get book test by specifying author`() {
        // 著者指定による書籍取得のテスト
        `when`(bookService.findBooksByAuthorId(1)).thenReturn(
            BookListDto(
                bookList = listOf(
                    BookDto(1, "テストタイトル１", 1000, 1, true),
                    BookDto(2, "テストタイトル２", 2000, 1, false),
                    BookDto(3, "テストタイトル３", 3000, 1, true)
                )
            )
        )
        // 著者リスト取得APIのテスト
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.bookList[2].title", equalTo("テストタイトル３")))
            .andExpect(jsonPath("$.bookList[2].authorId", equalTo(1)))
    }

    @Test
    fun `add book test`() {
        // 書籍追加のテスト
        val bookJson =
            "{\"title\":\"テストタイトル１\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "post:AddBookDto(title=テストタイトル１, price=9000, authorId=10, publicationStatus=true)"
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(10, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `add book test when title is not entered`() {
        // 書籍追加のタイトル未入力テスト
        val bookJson = "{\"title\":\"\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "タイトルの入力は必須です"
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(10, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )
        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `add book title character count test boundary value on`() {
        // 書籍追加のタイトル文字数テスト。境界値ON
        val bookJson =
            "{\"title\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse =
            "post:AddBookDto(title=１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０, price=9000, authorId=10, publicationStatus=true)"

        // テスト用の著者リストを準備し、モックを設定
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(10, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )


        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `add book title character count test boundary value out `() {
        // 書籍追加のタイトル文字数テスト。境界値OUT
        val bookJson =
            "{\"title\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "タイトルは100文字以内で入力してください"
        // テスト用の著者リストを準備し、モックを設定
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(10, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `add book price numeric range test boundary value on`() {
        // 書籍追加の価格数値範囲テスト。境界値ON
        val bookJson =
            "{\"title\":\"テストタイトル１\",\"price\":0,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "post:AddBookDto(title=テストタイトル１, price=0, authorId=10, publicationStatus=true)"

        // テスト用の著者リストを準備し、モックを設定
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(10, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )


        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `add book price numeric range test boundary value out`() {
        // 書籍追加の価格数値範囲テスト。境界値OUT
        val bookJson =
            "{\"title\":\"テストタイトル１\",\"price\":-1,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "価格の値は0以上を入力してください"
        // テスト用の著者リストを準備し、モックを設定
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(10, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `add book test when author id does not exist`() {
        // 書籍追加時の著者ID非存在のテスト
        val bookJson =
            "{\"title\":\"テストタイトル１\",\"price\":0,\"authorId\":999,\"publicationStatus\":\"true\"}"
        val expectedResponse = "存在しない著者IDです"

        // テスト用の著者リストを準備し、モックを設定
        `when`(authorService.findAuthorById(999)).thenReturn(
            null
        )


        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `add book author id numeric range test boundary value on`() {
        // 書籍追加の著者ID数値範囲テスト。境界値ON
        val bookJson =
            "{\"title\":\"テストタイトル１\",\"price\":0,\"authorId\":1,\"publicationStatus\":\"false\"}"
        val expectedResponse = "post:AddBookDto(title=テストタイトル１, price=0, authorId=1, publicationStatus=false)"

        // テスト用の著者リストを準備し、モックを設定
        `when`(authorService.findAuthorById(1)).thenReturn(
            AuthorDto(1, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )


        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `add book author id numeric range test boundary value out`() {
        // 書籍追加の著者ID数値範囲テスト。境界値OUT
        val bookJson =
            "{\"title\":\"テストタイトル１\",\"price\":0,\"authorId\":0,\"publicationStatus\":\"true\"}"
        val expectedResponse = "著者IDの値は1以上を入力してください"

        // テスト用の著者リストを準備し、モックを設定
        `when`(authorService.findAuthorById(0)).thenReturn(
            AuthorDto(0, "テスト著者１０", LocalDate.of(1990, 10, 11))
        )


        mockMvc.perform(
            post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `update book test`() {
        // 書籍更新のテスト
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse =
            "put:UpdateBookDto(id=1, title=テストタイトル更新後１, price=9000, authorId=10, publicationStatus=true)"

        // 更新対象書籍のモック。存在を保証
        `when`(bookService.findBookById(1)).thenReturn(
            BookDto(1, "テストタイトル更新前１", 1000, 1, true)
        )

        // 更新後の著者のモック。存在を保証
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(2, "テスト著者１", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `update book id numeric range test boundary value on`() {
        // 書籍更新の書籍ID数値範囲テスト。境界値ON
        val bookJson =
            "{\"bookId\":0,\"title\":\"テストタイトル更新後１\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "書籍IDの値は1以上を入力してください"

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `update book title character count test boundary value on`() {
        // 書籍更新のタイトル文字数テスト。境界値ON
        val bookJson =
            "{\"bookId\":1,\"title\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse =
            "put:UpdateBookDto(id=1, title=１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０, price=9000, authorId=10, publicationStatus=true)"

        // 更新対象書籍のモック。存在を保証
        `when`(bookService.findBookById(1)).thenReturn(
            BookDto(1, "テストタイトル更新前１", 1000, 1, true)
        )

        // 更新後の著者のモック。存在を保証
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(2, "テスト著者１", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `update book title character count test boundary value out`() {
        // 書籍更新のタイトル文字数テスト。境界値OUT
        val bookJson =
            "{\"bookId\":1,\"title\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "タイトルは100文字以内で入力してください"

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `update book test when title is not entered`() {
        // 書籍更新のタイトル未入力テスト
        val bookJson =
            "{\"bookId\":1,\"title\":\"\",\"price\":9000,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "タイトルの入力は必須です"

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `update book price numeric range test boundary value on`() {
        // 書籍更新の価格数値範囲テスト。境界値ON
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":0,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse =
            "put:UpdateBookDto(id=1, title=テストタイトル更新後１, price=0, authorId=10, publicationStatus=true)"

        // 更新対象書籍のモック。存在を保証
        `when`(bookService.findBookById(1)).thenReturn(
            BookDto(1, "テストタイトル更新前１", 1000, 1, true)
        )

        // 更新後の著者のモック。存在を保証
        `when`(authorService.findAuthorById(10)).thenReturn(
            AuthorDto(2, "テスト著者１", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `update book price numeric range test boundary value out`() {
        // 書籍更新の価格数値範囲テスト。境界値OUT
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":-1,\"authorId\":10,\"publicationStatus\":\"true\"}"
        val expectedResponse = "価格の値は0以上を入力してください"

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `update book author id numeric range test boundary value on`() {
        // 書籍更新の著者ID数値範囲テスト。境界値ON
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":0,\"authorId\":1,\"publicationStatus\":\"true\"}"
        val expectedResponse =
            "put:UpdateBookDto(id=1, title=テストタイトル更新後１, price=0, authorId=1, publicationStatus=true)"

        // 更新対象書籍のモック。存在を保証
        `when`(bookService.findBookById(1)).thenReturn(
            BookDto(1, "テストタイトル更新前１", 1000, 2, true)
        )

        // 更新後の著者のモック。存在を保証
        `when`(authorService.findAuthorById(1)).thenReturn(
            AuthorDto(1, "テスト著者１", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `update book author id numeric range test boundary value out`() {
        // 書籍更新の著者ID数値範囲テスト。境界値OUT
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":1000,\"authorId\":0,\"publicationStatus\":\"true\"}"
        val expectedResponse = "著者IDの値は1以上を入力してください"

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `update book test when book id exists`() {
        // 書籍更新の書籍ID存在テスト
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":0,\"authorId\":1,\"publicationStatus\":\"false\"}"
        val expectedResponse = "存在しない書籍IDです"

        // 更新対象書籍のモック。存在を保証
        // 更新前の出版ステータスはtrueである。
        `when`(bookService.findBookById(1)).thenReturn(
            null
        )

        // 更新後の著者のモック。存在を保証
        `when`(authorService.findAuthorById(1)).thenReturn(
            AuthorDto(1, "テスト著者１", LocalDate.of(1990, 10, 11))
        )


        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `update book test when author id exists`() {
        // 書籍更新の著者ID存在テスト
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":0,\"authorId\":1,\"publicationStatus\":\"false\"}"
        val expectedResponse = "存在しない著者IDです"

        // 更新対象書籍のモック。存在を保証
        // 更新前の出版ステータスはtrueである。
        `when`(bookService.findBookById(1)).thenReturn(
            BookDto(1, "テストタイトル更新前１", 1000, 2, true)
        )

        // 更新後の著者のモック。存在を保証
        `when`(authorService.findAuthorById(1)).thenReturn(
            null
        )

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }


    @Test
    fun `test when publication status is true but should be false`() {
        // 書籍更新のテスト
        // 出版ステータスがtrueのものをfalse場合のテスト
        val bookJson =
            "{\"bookId\":1,\"title\":\"テストタイトル更新後１\",\"price\":0,\"authorId\":1,\"publicationStatus\":\"false\"}"
        val expectedResponse = "出版済を未出版には変更できません"

        // 更新対象書籍のモック。存在を保証
        // 更新前の出版ステータスはtrueである。
        `when`(bookService.findBookById(1)).thenReturn(
            BookDto(1, "テストタイトル更新前１", 1000, 2, true)
        )

        // 更新後の著者のモック。存在を保証
        `when`(authorService.findAuthorById(1)).thenReturn(
            AuthorDto(1, "テスト著者１", LocalDate.of(1990, 10, 11))
        )

        mockMvc.perform(
            put("/api/books/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }
}
