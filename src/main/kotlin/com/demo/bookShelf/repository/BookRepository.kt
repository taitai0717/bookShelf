package com.demo.bookShelf.repository

import org.jooq.DSLContext
import org.jooq.Record
import com.demo.bookShelf.model.Book
import org.example.db.tables.Books.BOOKS
import org.example.db.tables.records.BooksRecord
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val dsl: DSLContext) {
    // IDで書籍を検索し、存在すればBookモデルを返す
    fun findById(id: Int): Book? {
        return dsl
            .select()
            .from(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne()?.map { toModel(it) }
    }

    // 全ての書籍を取得してList<Book>で返す
    fun findAll(): List<Book> {
        return dsl
            .select()
            .from(BOOKS)
            .fetch().map { toModel(it) }
    }

    // 指定された著者IDの書籍を取得してList<Book>で返す
    fun findAllByAuthorId(id: Int): List<Book> {
        return dsl
            .select()
            .from(BOOKS)
            .where(BOOKS.AUTHOR_ID.eq(id))
            .fetch().map { toModel(it) }
    }

    // 書籍情報を新規追加または更新する
    fun saveBook(bookRecord: BooksRecord) {
        if (bookRecord.id == null) {
            // 新規の場合はINSERT
            dsl.insertInto(BOOKS)
                .set(BOOKS.TITLE, bookRecord.title)
                .set(BOOKS.PRICE, bookRecord.price)
                .set(BOOKS.AUTHOR_ID, bookRecord.authorId)
                .set(BOOKS.PUBLICATION_STATUS, bookRecord.publicationStatus)
                .execute()
        } else {
            // 既存の場合はUPDATE
            dsl.update(BOOKS)
                .set(BOOKS.TITLE, bookRecord.title)
                .set(BOOKS.PRICE, bookRecord.price)
                .set(BOOKS.AUTHOR_ID, bookRecord.authorId)
                .set(BOOKS.PUBLICATION_STATUS, bookRecord.publicationStatus)
                .where(BOOKS.ID.eq(bookRecord.id))
                .execute()
        }
    }

    // RecordをBookモデルに変換する
    private fun toModel(record: Record) = Book(
        record.getValue(BOOKS.ID)!!,
        record.getValue(BOOKS.TITLE)!!,
        record.getValue(BOOKS.PRICE)!!,
        record.getValue(BOOKS.AUTHOR_ID)!!,
        record.getValue(BOOKS.PUBLICATION_STATUS)
    )
}
