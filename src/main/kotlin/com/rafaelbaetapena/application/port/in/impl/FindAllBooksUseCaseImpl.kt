package com.rafaelbaetapena.application.port.`in`.impl

import com.rafaelbaetapena.application.domain.Book
import com.rafaelbaetapena.application.domain.BookFilter
import com.rafaelbaetapena.application.port.`in`.FindAllBooksUseCase
import com.rafaelbaetapena.application.port.out.FindAllBooksPort
import javax.inject.Singleton

@Singleton
class FindAllBooksUseCaseImpl(private val findAllBooksPort: FindAllBooksPort):
        FindAllBooksUseCase {

    override fun execute(filters: BookFilter): List<Book> {
        return findAllBooksPort.execute(filters)
    }
}