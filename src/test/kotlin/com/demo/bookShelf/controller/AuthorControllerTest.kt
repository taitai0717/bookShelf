package com.demo.bookShelf.controller

import com.demo.bookShelf.dto.response.AuthorDto
import com.demo.bookShelf.dto.response.AuthorListDto
import com.demo.bookShelf.service.AuthorService
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

@WebMvcTest(AuthorController::class)
class AuthorControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var authorService: AuthorService

    @Test
    fun `get author test`() {
        // 著者取得のテスト
        `when`(authorService.findAllAuthors()).thenReturn(
            AuthorListDto(
                authorList = listOf(
                    AuthorDto(1, "テスト著者１", LocalDate.of(2001, 1, 10)),
                    AuthorDto(2, "テスト著者２", LocalDate.of(2002, 1, 20)),
                    AuthorDto(3, "テスト著者３", LocalDate.of(2003, 3, 30))
                )
            )
        )

        // 著者リスト取得APIのテスト
        mockMvc.perform(get("/api/author/"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.authorList[2].name", equalTo("テスト著者３")))
    }

    @Test
    fun `add author test`() {
        // 著者追加のテスト
        val authorJson = "{\"name\":\"テスト著者１\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "post:AddAuthorDto(name=テスト著者１, birthday=2002-09-12)"

        mockMvc.perform(
            post("/api/author/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `test when author name is not entered`() {
        // 著者名未入力時のテスト
        val authorJson = "{\"name\":\"\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "著者名の入力は必須です"

        mockMvc.perform(
            post("/api/author/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author name character count test boundary value on`() {
        // 著者名追加の文字数テスト。境界値ON
        val authorJson = "{\"name\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse =
            "post:AddAuthorDto(name=１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０, birthday=2002-09-12)"

        mockMvc.perform(
            post("/api/author/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author name character count test boundary value out`() {
        // 著者名の文字数テスト。境界値OUT
        val authorJson =
            "{\"name\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "著者名は50文字以内で入力してください"

        mockMvc.perform(
            post("/api/author/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `date of birth format test`() {
        // 生年月日フォーマットのテスト
        val authorJson = "{\"name\":\"テスト著者１\",\"birthday\":\"20020912\"}"
        val expectedResponse = "生年月日は'yyyy-mm-dd'形式で入力してください"

        mockMvc.perform(
            post("/api/author/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author add test when date of birth future boundary value on`() {
        // 生年月日の未来日のテスト
        // 前日を指定
        val birthday = LocalDate.now().minusDays(1).toString()
        val authorJson = "{\"name\":\"テスト著者１\",\"birthday\":\"$birthday\"}"
        val expectedResponse = "post:AddAuthorDto(name=テスト著者１, birthday=2002-09-12)"

        mockMvc.perform(
            post("/api/author/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))

    }


    @Test
    fun `author add test when date of birth future boundary value out`() {
        // 生年月日の未来日のテスト
        // 前日を指定
        val birthday = LocalDate.now().toString()
        val authorJson = "{\"name\":\"テスト著者１\",\"birthday\":\"$birthday\"}"
        val expectedResponse = "生年月日は現在の日付よりも過去でなければなりません"

        mockMvc.perform(
            post("/api/author/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `update author information test`() {
        // 著者情報の更新テスト
        val authorJson = "{\"authorId\":4,\"name\":\"テスト著者４変更後\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "put:UpdateAuthorDto(id=4, name=テスト著者４変更後, birthday=2002-09-12)"

        `when`(authorService.findAuthorById(4)).thenReturn(
            AuthorDto(4, "テスト著者４変更前", LocalDate.of(2001, 10, 15))
        )

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author id numeric range test`() {
        // 著者IDの数値範囲のテスト
        val authorJson = "{\"authorId\":0,\"name\":\"テスト著者１変更後\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "著者IDの値は1以上を入力してください"

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author name update character count test boundary value on`() {
        // 著者名更新の文字数テスト。境界値ON
        val authorJson =
            "{\"authorId\":1,\"name\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse =
            "put:UpdateAuthorDto(id=1, name=１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０, birthday=2002-09-12)"

        `when`(authorService.findAuthorById(1)).thenReturn(
            AuthorDto(4, "テスト著者４変更前", LocalDate.of(2001, 10, 15))
        )

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author name update character count test boundary value out`() {
        // 著者名更新の文字数テスト。境界値OUT
        val authorJson =
            "{\"authorId\":1,\"name\":\"１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "著者名は50文字以内で入力してください"

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author name update test when author name is not entered`() {
        // 著者名更新の著者名未入力のテスト
        val authorJson = "{\"authorId\":1,\"name\":\"\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "著者名の入力は必須です"

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author name update date of birth format test`() {
        // 著者名更新の生年月日フォーマットのテスト
        val authorJson = "{\"authorId\":1,\"name\":\"テスト著者１変更後\",\"birthday\":\"20020912\"}"
        val expectedResponse = "生年月日は'yyyy-mm-dd'形式で入力してください"

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author update test when date of birth future boundary value on`() {
        // 生年月日の未来日のテスト
        // 当日を指定
        val birthday = LocalDate.now().toString()
        val authorJson = "{\"authorId\":1,\"name\":\"テスト著者１\",\"birthday\":\"$birthday\"}"
        val expectedResponse = "生年月日は現在の日付よりも過去でなければなりません"

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }

    @Test
    fun `author update test when date of birth future boundary value out`() {
        // 生年月日の未来日のテスト
        // 前日を指定
        val birthday = LocalDate.now().minusDays(1).toString()
        val authorJson = "{\"authorId\":1,\"name\":\"テスト著者１\",\"birthday\":\"$birthday\"}"
        val expectedResponse =
            "put:UpdateAuthorDto(id=1, name=テスト著者１, birthday=$birthday)"

        `when`(authorService.findAuthorById(1)).thenReturn(
            AuthorDto(4, "テスト著者４変更前", LocalDate.of(2001, 10, 15))
        )

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(expectedResponse))

    }

    @Test
    fun `author update test when author does not exist`() {
        // 著者更新の著者非存在のテスト
        val authorJson = "{\"authorId\":4,\"name\":\"テスト著者４変更後\",\"birthday\":\"2002-09-12\"}"
        val expectedResponse = "存在しない著者IDです"

        `when`(authorService.findAuthorById(4)).thenReturn(
            null
        )

        mockMvc.perform(
            put("/api/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(expectedResponse))
    }
}
