package com.demo.bookShelf.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

// 書籍更新RequestDto
data class UpdateBookDto(

    // 書籍ID
    @field:Min(value = 1, message = "書籍IDの値は1以上を入力してください")
    @field:JsonProperty("bookId", required = true)
    val id: Int,

    // タイトル
    @field:NotEmpty(message = "タイトルの入力は必須です")
    @field:Size(max = 100, message = "タイトルは100文字以内で入力してください")
    @field:JsonProperty("title", required = true)
    val title: String,

    // 価格
    @field:Min(value = 0, message = "価格の値は0以上を入力してください")
    @field:JsonProperty("price", required = true)
    val price: Long,

    // 著者ID
    @field:Min(value = 1, message = "著者IDの値は1以上を入力してください")
    @field:JsonProperty("authorId", required = true)
    val authorId: Int,

    // 出版状況
    @field:JsonProperty("publicationStatus", required = true)
    val publicationStatus: Boolean
)