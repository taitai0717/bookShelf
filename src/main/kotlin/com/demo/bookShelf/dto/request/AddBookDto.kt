package com.demo.bookShelf.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

// 書籍追加RequestDto
data class AddBookDto(

    // タイトル
    @field:Size(max = 100, message = "タイトルは100文字以内で入力してください")
    @field:NotBlank(message = "タイトルの入力は必須です")
    @field:JsonProperty("title", required = true)
    val title: String,

    // 価格
    @Min(value = 0, message = "価格の値は0以上を入力してください")
    @field:JsonProperty("price", required = true)
    val price: Long,

    // 著者ID
    @field:NotNull(message = "著者IDの入力は必須です")
    @Min(value = 1, message = "著者IDの値は1以上を入力してください")
    @field:JsonProperty("authorId", required = true)
    val authorId: Int,

    // 出版状況
    @field:JsonProperty("publicationStatus", required = true)
    val publicationStatus: Boolean = false
)