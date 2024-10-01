package com.demo.bookShelf.service

import com.demo.bookShelf.dto.request.AddAuthorDto
import com.demo.bookShelf.dto.request.UpdateAuthorDto
import com.demo.bookShelf.dto.response.AuthorDto
import com.demo.bookShelf.dto.response.AuthorListDto
import com.demo.bookShelf.model.Author
import com.demo.bookShelf.repository.AuthorRepository
import org.example.db.tables.records.AuthorRecord
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AuthorService(private val authorRepository: AuthorRepository) {

    // 著者IDで著者を検索するメソッド
    @Transactional(readOnly = true)
    fun findAuthorById(id: Int): AuthorDto? {
        // リポジトリから著者情報を取得
        val author = authorRepository.findById(id)
        // 著者が存在する場合はAuthorDtoを返却
        return if (author != null) {
            AuthorDto(
                id = author.id, name = author.name, birthday = author.birthday
            )
        } else {
            // 存在しない場合はnullを返却
            null
        }
    }

    // すべての著者を取得するメソッド
    @Transactional(readOnly = true)
    fun findAllAuthors(): AuthorListDto {
        // リポジトリから全著者情報を取得し、AuthorDtoのリストに変換
        return AuthorListDto(authorList = authorRepository.findAll().map { author: Author ->
            AuthorDto(
                id = author.id, name = author.name, birthday = author.birthday
            )
        })
    }

    // 新しい著者を追加するメソッド
    @Transactional
    fun addAuthor(addAuthorDto: AddAuthorDto) {
        // 新しい著者を表すPOJOを作成
        val addAuthor = org.example.db.tables.pojos.Author()
        addAuthor.setName(addAuthorDto.name)
        addAuthor.setBirthday(LocalDate.parse(addAuthorDto.birthday))

        // AuthorRecordを作成
        val addRecord = AuthorRecord(
            addAuthor
        )
        // リポジトリを通じて著者情報を保存
        authorRepository.saveAuthor(
            addRecord
        )
    }

    // 著者情報を更新するメソッド
    @Transactional
    fun updateBook(updateAuthorDto: UpdateAuthorDto) {
        // 更新対象の著者を表すPOJOを作成
        val updateAuthor = org.example.db.tables.pojos.Author()
        updateAuthor.setId(updateAuthorDto.id)
        updateAuthor.setName(updateAuthorDto.name)
        updateAuthor.setBirthday(LocalDate.parse(updateAuthorDto.birthday))

        // AuthorRecordを作成し、リポジトリを通じて著者情報を保存
        authorRepository.saveAuthor(
            AuthorRecord(
                updateAuthor
            )
        )
    }

}
