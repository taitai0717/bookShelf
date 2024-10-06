package com.demo.bookShelf.repository

import com.demo.bookShelf.model.Author
import org.assertj.core.api.Assertions
import org.example.db.tables.Author.AUTHOR
import org.example.db.tables.records.AuthorRecord
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private lateinit var dsl: DSLContext

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    // シーケンス保持用の変数
    private var currentSeq: Int = 0

    // テストデータのセットアップ
    @BeforeEach
    fun setupTestData() {
        // テスト前のシーケンスを取得
        currentSeq = (dsl.fetchValue("SELECT nextval('books_id_seq')") as Long).toInt()
        dsl.insertInto(AUTHOR)
            .columns(AUTHOR.ID, AUTHOR.NAME, AUTHOR.BIRTHDAY)
            .values(currentSeq + 1, "テスト著者１", LocalDate.of(2024, 10, 21))
            .values(currentSeq + 2, "テスト著者２", LocalDate.of(2024, 10, 21))
            .values(currentSeq + 3, "テスト著者３", LocalDate.of(2024, 10, 21))
            .execute()
        // テストデータ登録後のシーケンスを設定
        dsl.execute("SELECT setval('author_id_seq', ${currentSeq + 4}, false);")
    }


    @AfterEach
    fun cleanup() {
        // テストデータ削除
        dsl.delete(AUTHOR).execute()
        // シーケンスをテスト前に戻す
        dsl.execute("SELECT setval('books_id_seq', ${currentSeq}, false)")
    }

    // 指定したIDの著者が存在する場合、正しい著者情報を返すことを確認するテスト
    @Test
    fun `findById should return author when ID is found`() {

        val authorId = currentSeq + 1
        val expectedAuthor = Author(authorId, "テスト著者１", LocalDate.of(2024, 10, 21))
        val result = authorRepository.findById(authorId)

        Assertions.assertThat(result).isEqualTo(expectedAuthor)
    }

    // 指定したIDの著者が存在しない場合、nullが返ることを確認するテスト
    @Test
    fun `findById should return null when ID does not exist`() {
        val authorId = 2147483647
        val result = authorRepository.findById(authorId)

        Assertions.assertThat(result).isNull()
    }

    //    全著者情報を返すことを確認するテスト
    @Test
    fun `findAll should return all authors`() {
        val result = authorRepository.findAll()

        Assertions.assertThat(result).isNotEmpty
        Assertions.assertThat(result.size).isEqualTo(3)
    }

    // 新しい著者が保存されることを確認するテスト
    @Test
    fun `saveAuthor should insert new author when ID is null`() {
        val authorId = currentSeq + 4
        val newAuthor = org.example.db.tables.pojos.Author()
        newAuthor.name = "新規登録著者"
        newAuthor.birthday = LocalDate.of(2000, 12, 1)

        val newAuthorRecord = AuthorRecord(newAuthor)

        authorRepository.saveAuthor(newAuthorRecord)

        val insertedRecord = dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(authorId))
            .fetchOne()

        Assertions.assertThat(insertedRecord).isNotNull
        Assertions.assertThat(insertedRecord!!.id).isEqualTo(authorId)
        Assertions.assertThat(insertedRecord.name).isEqualTo("新規登録著者")
        Assertions.assertThat(insertedRecord.birthday).isEqualTo(LocalDate.of(2000, 12, 1))
    }

    // 既存の著者情報が更新されることを確認するテスト
    @Test
    fun `saveAuthor should update existing author when ID is not null`() {
        val authorId = currentSeq + 1
        val existingAuthor = org.example.db.tables.pojos.Author()
        existingAuthor.id = authorId
        existingAuthor.name = "既存登録著者"
        existingAuthor.birthday = LocalDate.of(1999, 8, 10)

        val existingAuthorRecord = AuthorRecord(existingAuthor)
        authorRepository.saveAuthor(existingAuthorRecord)

        val updatedRecord = dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(authorId))
            .fetchOne()

        Assertions.assertThat(updatedRecord).isNotNull
        Assertions.assertThat(updatedRecord!!.id).isEqualTo(authorId)
        Assertions.assertThat(updatedRecord.name).isEqualTo("既存登録著者")
        Assertions.assertThat(updatedRecord.birthday).isEqualTo(LocalDate.of(1999, 8, 10))
    }
}
