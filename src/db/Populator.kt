package com.gmail.marcosav2010.db

import com.gmail.marcosav2010.db.dao.ProductCategoryEntity
import com.gmail.marcosav2010.db.dao.ProductEntity
import com.gmail.marcosav2010.db.dao.UserEntity
import com.gmail.marcosav2010.db.dao.UserRoles
import com.gmail.marcosav2010.model.*
import com.gmail.marcosav2010.utils.BCryptEncoder
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*
import kotlin.random.Random

fun SchemaUtils.populate() {
    listOf(
        "fashion",
        "multimedia",
        "phones",
        "computers-electronic",
        "sports",
        "games",
        "home",
        "others"
    ).forEachIndexed { i, c -> ProductCategoryEntity.new(i.toLong() + 1) { this.name = c } }

    val hPassword = BCryptEncoder.encode("1")

    val user = UserEntity.add(
        User(1, "Marcos", "AV", "a@a.a", hPassword, "marcos", "Hola soy Marcos")
    )

    (1..100L).forEach {
        UserEntity.add(
            User(null, "U$it", "S S$it", "u$it@a.a", hPassword, "u$it", "Nice boy n$it")
        )
    }

    (1..750L).forEach {
        ProductEntity.add(
            Product(
                null,
                "P$it",
                genPrice(),
                Random.nextInt(0, 20),
                ProductCategory(Random.nextLong(1, 8)),
                false,
                "Nice product very nice bery nice, i love me gusta mucho $it",
                date = randomDate(),
                userId = Random.nextLong(1, 100)
            )
        )
    }

    val imageUris = listOf("a", "b")
    val images = imageUris.mapIndexed { i, it -> ProductImage(i.toByte(), it) }

    (1..15L).forEach {
        ProductEntity.add(
            Product(
                null,
                "P$it",
                genPrice(),
                Random.nextInt(0, 20),
                ProductCategory(Random.nextLong(1, 8)),
                false,
                "hola este es mi primer segunto tercer y n-simo producto",
                images,
                date = randomDate(),
                userId = user.id.value
            )
        )
    }

    UserRoles.insert {
        it[userId] = user.id.value
        it[role] = Role.ADMIN.id
    }
}

private fun genPrice() = "%.2f".format(Random.nextDouble(5.0, 1000.0)).toDouble()

const val PRODUCTS_SINCE = 6 * 30 * 24 * 3600 * 1000L

private fun randomDate(): Instant {
    val date = Date()
    date.time = date.time - Random.nextLong(PRODUCTS_SINCE)
    return date.toInstant()
}