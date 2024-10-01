package com.demo.bookShelf.dto.response

// 書籍ResponseDto
data class BookDto(
    // 書籍ID
    val id: Int,
    // タイトル
    val title: String,
    // 価格
    val price: Long,
    // 著者名
    val authorId: Int,
    // 出版状況
    val publicationStatus: Boolean
)