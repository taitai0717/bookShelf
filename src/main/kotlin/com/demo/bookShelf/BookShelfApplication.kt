package com.demo.bookShelf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookShelfApplication

fun main(args: Array<String>) {
	runApplication<BookShelfApplication>(*args)
}
