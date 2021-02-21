package com.gmail.marcosav2010.db

import com.gmail.marcosav2010.db.dao.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

const val HIKARI_CONFIG = "ktor.hikariconfig"

fun Application.setupDB() {
    val configPath = environment.config.property(HIKARI_CONFIG).getString()

    val dbConfig = HikariConfig(configPath)
    val dataSource = HikariDataSource(dbConfig)

    Database.connect(dataSource)

    createTables()
}

private fun createTables() = transaction {
    SchemaUtils.create(
        Users,
        Products,
        Favorites,
        Carts,
        CartProducts
    )
}