package com.demo.bookShelf.repository

import com.demo.bookShelf.model.Author
import org.example.db.tables.Author.AUTHOR
import org.example.db.tables.records.AuthorRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class AuthorRepository(private val dsl: DSLContext) {
    // IDで著者を検索し、存在すればAuthorモデルを返す
    fun findById(id: Int): Author? {
        return dsl
            .select()
            .from(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOne()?.map { toModel(it) }
    }

    // 全ての著者を取得してList<Author>で返す
    fun findAll(): List<Author> {
        return dsl
            .select()
            .from(AUTHOR)
            .fetch().map { toModel(it) }
    }

    // 著者情報を新規追加または更新する
    fun saveAuthor(authorRecord: AuthorRecord) {
        if (authorRecord.id == null) {
            // 新規の場合はINSERT
            dsl.insertInto(AUTHOR)
                .set(AUTHOR.NAME, authorRecord.name)
                .set(AUTHOR.BIRTHDAY, authorRecord.birthday)
                .set(AUTHOR.CREATED_AT, LocalDateTime.now())
                .set(AUTHOR.UPDATED_AT, LocalDateTime.now())
                .execute()
        } else {
            // 既存の場合はUPDATE
            dsl.update(AUTHOR)
                .set(AUTHOR.NAME, authorRecord.name)
                .set(AUTHOR.BIRTHDAY, authorRecord.birthday)
                .set(AUTHOR.UPDATED_AT, LocalDateTime.now())
                .where(AUTHOR.ID.eq(authorRecord.id))
                .execute()
        }
    }

    // RecordをAuthorモデルに変換する
    private fun toModel(record: Record) = Author(
        record.getValue(AUTHOR.ID)!!,
        record.getValue(AUTHOR.NAME)!!,
        record.getValue(AUTHOR.BIRTHDAY)!!
    )
}
