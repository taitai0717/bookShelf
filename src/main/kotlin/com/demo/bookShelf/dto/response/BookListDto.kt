package com.demo.bookShelf.dto.response

// 書籍リストResponseDto
data class BookListDto(
    // 書籍リスト
    val bookList: List<BookDto>
)