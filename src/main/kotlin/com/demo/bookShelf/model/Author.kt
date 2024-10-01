package com.demo.bookShelf.model

import java.time.LocalDate

// 著者model
data class Author(
    // 著者ID
    val id: Int,
    // 著者名
    val name: String,
    // 生年月日
    val birthday: LocalDate,
)