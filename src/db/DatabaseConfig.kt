package com.gmail.marcosav2010.db

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.db.dao.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Application.setupDB() {
    val props = Properties()

    props.set("dataSourceClassName", Constants.DATABASE_SOURCE)
    props.set("dataSource.serverName", Constants.DATABASE_HOST)
    props.set("dataSource.databaseName", Constants.DATABASE_NAME)
    props.set("dataSource.user", Constants.DATABASE_USER)
    props.set("dataSource.password", Constants.DATABASE_PASSWORD)

    val dbConfig = HikariConfig(props)
    val dataSource = HikariDataSource(dbConfig)

    Database.connect(dataSource)

    createTables()
}

private fun Properties.set(key: String, value: String) = put(key, System.getenv(value))

private fun createTables() = transaction {
    SchemaUtils.create(
        Users,
        Addresses,
        ProductCategories,
        Products,
        FavoriteProducts,
        FavoriteVendors,
        CartProducts,
        ProductReports,
        UserRoles,
        ProductImages,
        Orders,
        OrderedProducts
    )

    if (System.getenv(Constants.POPULATE) == "yes") {
        SchemaUtils.populate()
        commit()
        //SchemaUtils.processDatasets()
        SchemaUtils.importDatasets()
    }
}