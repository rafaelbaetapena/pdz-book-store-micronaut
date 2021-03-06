package com.rafaelbaetapena.adapters.out.persistence.v1

import com.rafaelbaetapena.adapters.out.kafka.v1.DeleteBookByIdLogProducer
import com.rafaelbaetapena.adapters.out.persistence.v1.entities.BookEntity
import com.rafaelbaetapena.adapters.out.persistence.v1.repositories.BookRepository
import com.rafaelbaetapena.application.domain.Book
import com.rafaelbaetapena.application.domain.BookCategory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class DeleteBookByIdAdapterImplTest {

    companion object{
        private val LOG = LoggerFactory.getLogger(DeleteBookByIdAdapterImplTest::class.java)
    }

    @Mock
    lateinit var bookRepository: BookRepository

    @Mock
    lateinit var deleteBookByIdLogProducer: DeleteBookByIdLogProducer

    @InjectMocks
    lateinit var deleteBookByIdAdapterImpl: DeleteBookByIdAdapterImpl

    @Test
    fun `given an existing book id then the book data should be deleted`() {
        val book = Book(
                id = UUID.fromString("f680eba8-d0ab-4a95-8ec7-ee6ed4716606"),
                name = "The Hobbit",
                author = "J.R.R. Tolkien",
                publisher = "HarperCollins Publishers",
                numberOfPages = 400,
                category = BookCategory.FANTASY)

        doNothing().whenever(bookRepository).delete(BookEntity(book))
        doNothing().whenever(deleteBookByIdLogProducer).send(any(), any())

        deleteBookByIdAdapterImpl.execute(book)
        LOG.info("Deleted book: $book")
    }
}