package com.demo.bookShelf.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

// 著者更新RequestDto
data class UpdateAuthorDto(

    // 著者ID
    @field:Min(value = 1, message = "著者IDの値は1以上を入力してください")
    @field:JsonProperty("authorId", required = true)
    val id: Int,

    // 著者名
    @field:NotEmpty(message = "著者名の入力は必須です")
    @field:Size(max = 50, message = "著者名は50文字以内で入力してください")
    @field:JsonProperty("name", required = true)
    val name: String,

    // 生年月日
    @field:Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "生年月日は'yyyy-mm-dd'形式で入力してください")
    @field:JsonProperty("birthday", required = true)
    val birthday: String

)