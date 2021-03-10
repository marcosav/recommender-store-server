package com.gmail.marcosav2010.db

import com.gmail.marcosav2010.db.dao.ProductCategoryEntity
import com.gmail.marcosav2010.db.dao.ProductEntity
import com.gmail.marcosav2010.db.dao.UserEntity
import com.gmail.marcosav2010.db.dao.UserRoles
import com.gmail.marcosav2010.model.Product
import com.gmail.marcosav2010.model.ProductCategory
import com.gmail.marcosav2010.model.Role
import com.gmail.marcosav2010.model.User
import com.gmail.marcosav2010.utils.BCryptEncoder
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
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
    ).forEachIndexed { i, c -> ProductCategoryEntity.new(i.toLong()) { this.name = c } }

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
                Random.nextDouble(5.0, 1000.0),
                Random.nextInt(0, 20),
                ProductCategory(Random.nextLong(1, 8)),
                "[]",
                false,
                "Nice product very nice bery nice, i love me gusta mucho $it",
                userId = Random.nextLong(1, 100)
            )
        )
    }

    (1..15L).forEach {
        ProductEntity.add(
            Product(
                null,
                "P$it",
                Random.nextDouble(5.0, 1000.0),
                Random.nextInt(0, 20),
                ProductCategory(Random.nextLong(1, 8)),
                "[]",
                false,
                "hola este es mi primer segunto tercer y n-simo producto",
                userId = user.id.value
            )
        )
    }

    UserRoles.insert {
        it[userId] = user.id.value
        it[role] = Role.ADMIN.id
    }
}