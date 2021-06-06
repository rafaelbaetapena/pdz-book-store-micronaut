package com.rafaelbaetapena.application.`in`.domain

data class Book(
        val name: String,
        val author: String,
        val publisher: String,
        val numberOfPages: Int,
        val category: BookCategory
)