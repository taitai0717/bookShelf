package com.demo.bookShelf.controller

import com.demo.bookShelf.dto.request.AddAuthorDto
import com.demo.bookShelf.dto.request.UpdateAuthorDto
import com.demo.bookShelf.dto.response.AuthorListDto
import com.demo.bookShelf.service.AuthorService
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/author")
class AuthorController(private val authorService: AuthorService) {

    // 著者リストを取得するエンドポイント
    @GetMapping("/")
    fun authorList(): AuthorListDto {
        // 全ての著者情報を取得し、返却
        return authorService.findAllAuthors()
    }

    // 著者を新規追加するエンドポイント
    @PostMapping("/add")
    fun addAuthor(
        // @RequestBodyでリクエストボディからデータを取得し、DTOにバインド
        @RequestBody @Validated addAuthorDto: AddAuthorDto,
        // BindingResultでバリデーションエラーの結果を受け取る
        bindingResult: BindingResult
    ): ResponseEntity<Any> {

        // バリデーションエラーがあれば、エラーメッセージを返す
        handleValidationErrors(bindingResult)?.let { return it }

        // 更新後の生年月日が未来日でないか確認
        if (!LocalDate.now().isAfter(LocalDate.parse(addAuthorDto.birthday))) {
            // 存在しない場合、エラーを追加
            bindingResult.addError(FieldError("addAuthorDto", "birthday", "生年月日は現在の日付よりも過去でなければなりません"))
            // ビジネスロジックのエラーがあれば、エラーメッセージを返す
            handleValidationErrors(bindingResult)?.let { return it }
        }
        // 著者を追加するサービスメソッドを呼び出す
        authorService.addAuthor(addAuthorDto)
        // 成功時に追加した著者情報を含むレスポンスを返す
        return ResponseEntity.ok("post:$addAuthorDto")
    }

    // 著者情報を更新するエンドポイント
    @PutMapping("/update")
    fun updateAuthor(
        // リクエストボディからDTOにバインド
        @RequestBody @Validated updateAuthorDto: UpdateAuthorDto,
        // バリデーションエラーの結果を受け取る
        bindingResult: BindingResult
    ): ResponseEntity<Any> {

        // バリデーションエラーがあれば、エラーメッセージを返す
        handleValidationErrors(bindingResult)?.let { return it }
        // 更新後の生年月日が未来日でないか確認
        if (!LocalDate.now().isAfter(LocalDate.parse(updateAuthorDto.birthday))) {
            // 存在しない場合、エラーを追加
            bindingResult.addError(FieldError("updateAuthorDto", "birthday", "生年月日は現在の日付よりも過去でなければなりません"))
            // ビジネスロジックのエラーがあれば、エラーメッセージを返す
            handleValidationErrors(bindingResult)?.let { return it }
        }
        // 更新する著者IDがデータベースに存在するか確認
        if (authorService.findAuthorById(updateAuthorDto.id) == null) {
            // 存在しない場合、エラーを追加
            bindingResult.addError(FieldError("addBookDto", "authorId", "存在しない著者IDです"))
            // ビジネスロジックのエラーがあれば、エラーメッセージを返す
            handleValidationErrors(bindingResult)?.let { return it }
        }
        // 著者情報を更新するサービスメソッドを呼び出す
        authorService.updateAuthor(updateAuthorDto)
        // 成功時に更新した著者情報を含むレスポンスを返す
        return ResponseEntity.ok("put:$updateAuthorDto")
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
