package com.demo.bookShelf.dto.response

import java.time.LocalDate

// 著者ResponseDto
data class AuthorDto(
    // 著者ID
    val id: Int,
    // 著者名
    val name: String,
    // 生年月日
    val birthday: LocalDate
)