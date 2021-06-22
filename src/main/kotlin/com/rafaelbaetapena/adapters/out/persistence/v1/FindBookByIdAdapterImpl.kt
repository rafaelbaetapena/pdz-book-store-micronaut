package com.rafaelbaetapena.adapters.out.persistence.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.rafaelbaetapena.adapters.out.persistence.v1.repositories.BookRepository
import com.rafaelbaetapena.application.domain.Book
import com.rafaelbaetapena.application.port.out.FindBookByIdAdapter
import io.lettuce.core.api.StatefulRedisConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Singleton


@Singleton
class FindBookByIdAdapterImpl(
        private val bookRepository: BookRepository,
        private val redis: StatefulRedisConnection<String, String>,
        private val objectMapper: ObjectMapper
): FindBookByIdAdapter {

    override fun execute(bookId: UUID): Book? {

        log.info("$CLASS_NAME starting find book by id")

        val cacheKey = "$PREFIX_CACHE_KEY::$bookId"

        val book = getBookFromCache(cacheKey) ?: getBookFromDb(cacheKey, bookId)

        log.info("$CLASS_NAME finalized find book by id")

        return book
    }

    private fun getBookFromCache(cacheKey: String): Book? {
        val cachedBookJson = redis.sync().get(cacheKey) ?: return null

        val cachedBookObj = objectMapper.readValue(cachedBookJson, Book::class.java)

        log.info("$CLASS_NAME book found in cache with key $cacheKey")

        return cachedBookObj
    }

    private fun getBookFromDb(cacheKey: String, bookId: UUID): Book? {

        val book = bookRepository.findById(bookId)
                .map { it.toDomain() }
                .orElse(null)

        setCache(cacheKey, book)

        log.info("$CLASS_NAME book found in db with id $bookId")

        return book
    }

    private fun setCache(cacheKey: String, book: Book) {
        val bookAsJson = objectMapper.writeValueAsString(book)
        val commands = redis.sync()
        commands.multi()
        commands.set(cacheKey, bookAsJson)
        commands.expire(cacheKey, 30)
        commands.exec()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(FindBookByIdAdapterImpl::class.java)
        private val CLASS_NAME = "[${FindBookByIdAdapterImpl::class.java}]"
        private const val PREFIX_CACHE_KEY = "FIND_BOOK_BY_ID"
    }
}