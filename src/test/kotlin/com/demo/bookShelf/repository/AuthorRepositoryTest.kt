package com.demo.bookShelf.repository

import com.demo.bookShelf.model.Author
import org.assertj.core.api.Assertions
import org.example.db.tables.Author.AUTHOR
import org.example.db.tables.records.AuthorRecord
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
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

    /**
     * テストデータをセットアップするためのメソッド
     * ID: 1, 2, 3 の著者データを事前に挿入
     */
    fun setupTestData() {
        dsl.insertInto(AUTHOR)
            .columns(AUTHOR.ID, AUTHOR.NAME, AUTHOR.BIRTHDAY)
            .values(1, "テスト著者１", LocalDate.of(2024, 10, 21))
            .values(2, "テスト著者２", LocalDate.of(2024, 10, 21))
            .values(3, "テスト著者３", LocalDate.of(2024, 10, 21))
            .execute()
        dsl.execute("SELECT setval('author_id_seq', 4, false);")
    }

    /**
     * 各テスト終了後にデータをクリーンアップ
     */
    @AfterEach
    fun cleanup() {
        dsl.delete(AUTHOR).execute()
    }

    // 指定したIDの著者が存在する場合、正しい著者情報を返すことを確認するテスト
    @Test
    fun `findById should return author when ID is found`() {
        setupTestData()

        val authorId = 1
        val expectedAuthor = Author(1, "テスト著者１", LocalDate.of(2024, 10, 21))
        val result = authorRepository.findById(authorId)

        Assertions.assertThat(result).isEqualTo(expectedAuthor)
    }

    // 指定したIDの著者が存在しない場合、nullが返ることを確認するテスト
    @Test
    fun `findById should return null when ID does not exist`() {
        setupTestData()

        val authorId = 999
        val result = authorRepository.findById(authorId)

        Assertions.assertThat(result).isNull()
    }

    //    全著者情報を返すことを確認するテスト
    @Test
    fun `findAll should return all authors`() {
        setupTestData()

        val result = authorRepository.findAll()

        Assertions.assertThat(result).isNotEmpty
        Assertions.assertThat(result.size).isEqualTo(3)
    }

    // 新しい著者が保存されることを確認するテスト
    @Test
    fun `saveAuthor should insert new author when ID is null`() {
        setupTestData()

        val newAuthor = org.example.db.tables.pojos.Author()
        newAuthor.setName("新規登録著者")
        newAuthor.setBirthday(LocalDate.of(2000, 12, 1))

        val newAuthorRecord = AuthorRecord(newAuthor)

        authorRepository.saveAuthor(newAuthorRecord)

        val insertedRecord = dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(4))
            .fetchOne()

        Assertions.assertThat(insertedRecord).isNotNull
        Assertions.assertThat(insertedRecord!!.id).isEqualTo(4)
        Assertions.assertThat(insertedRecord.name).isEqualTo("新規登録著者")
        Assertions.assertThat(insertedRecord.birthday).isEqualTo(LocalDate.of(2000, 12, 1))
    }

    // 既存の著者情報が更新されることを確認するテスト
    @Test
    fun `saveAuthor should update existing author when ID is not null`() {
        setupTestData()

        val existingAuthor = org.example.db.tables.pojos.Author()
        existingAuthor.setId(2)
        existingAuthor.setName("既存登録著者")
        existingAuthor.setBirthday(LocalDate.of(1999, 8, 10))

        val existingAuthorRecord = AuthorRecord(existingAuthor)
        authorRepository.saveAuthor(existingAuthorRecord)

        val updatedRecord = dsl.selectFrom(AUTHOR)
            .where(AUTHOR.ID.eq(2))
            .fetchOne()

        Assertions.assertThat(updatedRecord).isNotNull
        Assertions.assertThat(updatedRecord!!.id).isEqualTo(2)
        Assertions.assertThat(updatedRecord.name).isEqualTo("既存登録著者")
        Assertions.assertThat(updatedRecord.birthday).isEqualTo(LocalDate.of(1999, 8, 10))
    }
}
