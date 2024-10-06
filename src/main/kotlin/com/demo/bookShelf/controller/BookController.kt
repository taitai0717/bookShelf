package com.demo.bookShelf.controller

import com.demo.bookShelf.dto.request.AddBookDto
import com.demo.bookShelf.dto.request.UpdateBookDto
import com.demo.bookShelf.dto.response.BookListDto
import com.demo.bookShelf.service.AuthorService
import com.demo.bookShelf.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService, private val authorService: AuthorService) {

    // 全書籍を取得するエンドポイント
    @GetMapping("/")
    fun bookList(): BookListDto {
        // サービスを呼び出して全書籍を取得し、返却
        return bookService.findAllBooks()
    }

    // 特定の著者IDに基づいて書籍を取得するエンドポイント
    @GetMapping("/{authorId}")
    fun bookListByAuthor(@PathVariable authorId: Int): BookListDto {
        // 著者IDに関連する書籍を取得し、返却
        return bookService.findBooksByAuthorId(authorId)
    }

    // 新しい書籍を追加するエンドポイント
    @PostMapping("/add")
    fun addBook(
        // リクエストボディからAddBookDtoにバインド
        @RequestBody @Validated addBookDto: AddBookDto,
        // バリデーションエラーの結果を受け取る
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        // 指定された著者IDが存在するか確認
        if (authorService.findAuthorById(addBookDto.authorId) == null) {
            // 存在しない場合、エラーを追加
            bindingResult.addError(FieldError("addBookDto", "authorId", "存在しない著者IDです"))
        }
        // バインドエラーがあれば、エラーメッセージを返す
        handleValidationErrors(bindingResult)?.let { return it }
        // サービスを呼び出して新しい書籍を追加
        bookService.addBook(addBookDto)

        // 成功時に追加した書籍情報を含むレスポンスを返す
        return ResponseEntity.ok("post:$addBookDto")
    }

    // 書籍情報を更新するエンドポイント
    @PutMapping("/update")
    fun updateBook(
        // リクエストボディからUpdateBookDtoにバインド
        @RequestBody @Validated updateBookDto: UpdateBookDto,
        // バリデーションエラーの結果を受け取る
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        // バインドエラーがあれば、エラーメッセージを返す
        handleValidationErrors(bindingResult)?.let { return it }

        // 更新する書籍IDがデータベースに存在するか確認
        val searchedBook = bookService.findBookById(updateBookDto.id)
        if (searchedBook == null) {
            // 存在しない場合、エラーを追加
            bindingResult.addError(FieldError("updateBookDto", "id", "存在しない書籍IDです"))
            // ビジネスロジックのエラーがあれば、エラーメッセージを返す
            handleValidationErrors(bindingResult)?.let { return it }
        }
        // 指定された著者IDが存在するか確認
        if (authorService.findAuthorById(updateBookDto.authorId) == null) {
            // 存在しない場合、エラーを追加
            bindingResult.addError(FieldError("updateBookDto", "authorId", "存在しない著者IDです"))
            // ビジネスロジックのエラーがあれば、エラーメッセージを返す
            handleValidationErrors(bindingResult)?.let { return it }
        }
        // 書籍が既に出版済みで、未出版に変更する場合のチェック
        if (searchedBook!!.publicationStatus == true && updateBookDto.publicationStatus == false) {
            // この変更が許可されない場合、エラーを追加
            bindingResult.addError(FieldError("updateBookDto", "id", "出版済を未出版には変更できません"))
            // ビジネスロジックのエラーがあれば、エラーメッセージを返す
            handleValidationErrors(bindingResult)?.let { return it }
        }

        // サービスを呼び出して書籍情報を更新
        bookService.updateBook(updateBookDto)

        // 成功時に更新した書籍情報を含むレスポンスを返す
        return ResponseEntity.ok("put:$updateBookDto")
    }

    // BindingResultのエラーを処理する共通メソッド
    fun handleValidationErrors(bindingResult: BindingResult): ResponseEntity<Any>? {
        // バインドエラーがあれば、エラーメッセージを返す
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.allErrors.map { it.defaultMessage }.joinToString(", ")
            return ResponseEntity.badRequest().body(errors)
        }
        // エラーがない場合はnullを返す
        return null
    }

}
