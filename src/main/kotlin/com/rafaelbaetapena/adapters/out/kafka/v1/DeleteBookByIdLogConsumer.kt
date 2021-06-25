package com.rafaelbaetapena.adapters.out.kafka.v1

import com.rafaelbaetapena.adapters.out.elasticsearch.v1.BookProducer
import com.rafaelbaetapena.application.domain.Book
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@KafkaListener(
    clientId = "pdz-book-store-micronaut-delete-book-by-id-log-consumer",
    groupId = "delete-book-by-id-log-consumer",
    batch = false,
    offsetReset = OffsetReset.EARLIEST
)
class DeleteBookByIdLogConsumer(
    private val elasticsearch: BookProducer
) {

    @Topic("delete-book-by-id-log")
    fun receive(book: Book) {
        log.info("$CLASS_NAME Consuming of deleted book by id $book")
        elasticsearch.send(LOG_INDEX, book)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(DeleteBookByIdLogConsumer::class.java)
        private val CLASS_NAME = "[${DeleteBookByIdLogConsumer::class.java}]"
        private const val LOG_INDEX = "delete-book-by-id-log-index"
    }
}