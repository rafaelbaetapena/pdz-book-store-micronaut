package com.rafaelbaetapena.adapters.out.persistence.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.rafaelbaetapena.adapters.out.persistence.v1.entities.BookEntity
import com.rafaelbaetapena.adapters.out.persistence.v1.repositories.BookRepository
import com.rafaelbaetapena.application.domain.BookCategory
import com.rafaelbaetapena.application.domain.BookFilter
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class FindAllBooksAdapterImplTest {

    companion object{
        private val LOG = LoggerFactory.getLogger(FindAllBooksAdapterImplTest::class.java)
    }

    @Mock
    lateinit var bookRepository: BookRepository

    @Mock
    lateinit var redis: StatefulRedisConnection<String, String>

    @Mock
    lateinit var redisCommands: RedisCommands<String, String>

    @Mock
    lateinit var jackson: ObjectMapper

    @InjectMocks
    lateinit var findAllBooksAdapterImpl: FindAllBooksAdapterImpl

    @Test
    fun `given the filter values then returns all found books`() {

        val bookEntity = BookEntity(
                id = UUID.randomUUID(),
                name = "The Hobbit",
                author = "J.R.R. Tolkien",
                publisher = "HarperCollins Publishers",
                numberOfPages = 400,
                category = BookCategory.FANTASY)

        val filters = BookFilter(
                name = "The Hobbit",
                author = "J.R.R. Tolkien",
                publisher = "HarperCollins Publishers",
                category = BookCategory.FANTASY
        )

        whenever(redis.sync()).thenReturn(redisCommands)

        whenever(bookRepository.findByFilters(filters)).thenReturn(listOf(bookEntity))

        val actual = findAllBooksAdapterImpl.execute(filters)
        assertNotNull(actual)
        assertFalse(actual.isEmpty())
        assertEquals(1, actual.size)
        LOG.info("Book found: $actual")
    }
}