package com.demo.bookShelf.service

import com.demo.bookShelf.dto.request.AddBookDto
import com.demo.bookShelf.dto.request.UpdateBookDto
import com.demo.bookShelf.dto.response.BookDto
import com.demo.bookShelf.dto.response.BookListDto
import com.demo.bookShelf.model.Book
import com.demo.bookShelf.repository.BookRepository
import org.example.db.tables.records.BooksRecord
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository) {

    // 指定されたIDで書籍を検索し、BookDtoとして返却するメソッド
    @Transactional(readOnly = true)
    fun findBookById(id: Int): BookDto? {
        val book = bookRepository.findById(id) // リポジトリから書籍を検索
        return if (book != null) {
            // 書籍が見つかった場合、BookDtoを作成して返却
            BookDto(
                id = book.id,
                title = book.title,
                price = book.price,
                authorId = book.authorId,
                publicationStatus = book.publicationStatus
            )
        } else {
            null // 書籍が見つからない場合はnullを返却
        }
    }

    // 全ての書籍を検索し、BookListDtoとして返却するメソッド
    @Transactional(readOnly = true)
    fun findAllBooks(): BookListDto {
        return BookListDto(bookList = bookRepository.findAll().map { book: Book ->
            // 各書籍をBookDtoに変換
            BookDto(
                id = book.id,
                title = book.title,
                price = book.price,
                authorId = book.authorId,
                publicationStatus = book.publicationStatus
            )
        })
    }

    // 指定された著者IDで書籍を検索し、BookListDtoとして返却するメソッド
    @Transactional(readOnly = true)
    fun findBooksByAuthorId(authorId: Int): BookListDto {
        return BookListDto(bookList = bookRepository.findAllByAuthorId(authorId).map { book: Book ->
            // 各書籍をBookDtoに変換
            BookDto(
                id = book.id,
                title = book.title,
                price = book.price,
                authorId = book.authorId,
                publicationStatus = book.publicationStatus
            )
        })
    }

    // 新しい書籍を追加するメソッド
    @Transactional
    fun addBook(addBookDto: AddBookDto) {
        val addBook = org.example.db.tables.pojos.Books()
        // 受け取ったデータをPOJOのフィールドに設定
        addBook.setTitle(addBookDto.title)
        addBook.setPrice(addBookDto.price)
        addBook.setAuthorId(addBookDto.authorId)
        addBook.setPublicationStatus(addBookDto.publicationStatus)
        // リポジトリを通じて書籍情報を保存
        bookRepository.saveBook(
            BooksRecord(addBook)
        )
    }

    // 既存の書籍情報を更新するメソッド
    @Transactional
    fun updateBook(updateBookDto: UpdateBookDto) {
        val updateBook = org.example.db.tables.pojos.Books()
        // 受け取ったデータをPOJOのフィールドに設定
        updateBook.setId(updateBookDto.id)
        updateBook.setTitle(updateBookDto.title)
        updateBook.setPrice(updateBookDto.price)
        updateBook.setAuthorId(updateBookDto.authorId)
        updateBook.setPublicationStatus(updateBookDto.publicationStatus)
        // リポジトリを通じて書籍情報を保存
        bookRepository.saveBook(
            BooksRecord(updateBook)
        )
    }

}
