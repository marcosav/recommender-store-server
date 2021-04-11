package com.gmail.marcosav2010.db

import com.gmail.marcosav2010.db.dao.*
import com.gmail.marcosav2010.model.*
import com.gmail.marcosav2010.utils.BCryptEncoder
import com.google.gson.Gson
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.util.*
import kotlin.random.Random

fun SchemaUtils.populate() {
    if (!ProductCategories.selectAll().empty())
        return

    listOf(
        "fashion", // 1
        "phones", // 2
        "computers-electronic", // 3
        "sports", // 4
        "games", // 5
        "home", // 6
        "others" // 7
    ).forEachIndexed { i, c -> ProductCategoryEntity.new(i.toLong() + 1) { this.name = c } }

    val hPassword = BCryptEncoder.encode("1")

    val user = UserEntity.add(
        User(
            1,
            "Marcos",
            "AV",
            "a@a.a",
            hPassword,
            "marcos",
            DEFAULT_DESCRIPTION,
            images[0]
        )
    )

    /*(1..50L).forEach {
        UserEntity.add(
            User(null, "user$it", "S S$it", "u$it@a.a", hPassword, "u$it", "Nice boy n$it", images[0])
        )
    }*/

    UserRoles.insert {
        it[userId] = user.id.value
        it[role] = Role.ADMIN.id
    }

}

const val DATASET_NAME = "product_dataset"

fun SchemaUtils.importDatasets() {
    val f = File(DATASET_NAME)
    if (!f.exists()) return

    val gson = Gson()

    Files.newBufferedReader(f.toPath()).use { reader ->
        println("Importing dataset products, this may took some hours...")

        var count = 0
        while (reader.ready()) {
            val l = reader.readLine()

            if (count++ % 2500 == 0)
                println("Imported $count")

            val parsed = gson.fromJson(l, SavedProduct::class.java)

            p(
                parsed.name,
                parsed.brand,
                parsed.price,
                parsed.category,
                null,
                *parsed.images.toTypedArray(),
                description = parsed.description,
                external = true
            )
        }
    }
}

/*private val RENAMING_RULES = mapOf(
    Pair("_SS40_.jpg", "_SS600_.jpg"),
    Pair("_US40_.jpg", "_US600_.jpg"),
    Pair("_SR38,50_.jpg", "_SR450,600_.jpg"),
    Pair("_SX38_SY50_CR,0,0,38,50_.jpg", "_SX450_SY600_CR,0,0,450,600_.jpg"),
    Pair("_SS36_.jpg", "_SS600_.jpg")
)

data class DatasetProduct(
    val asin: String?,
    val title: String?,
    val description: List<String>?,
    val price: String?,
    val image: List<String>?,
    val brand: String?,
    val category: List<String>?,
    val feature: List<String>?
)

fun SchemaUtils.processDatasets() {
    val f = File(DATASET_NAME)
    if (!f.exists()) f.createNewFile()

    val gson = Gson()

    fun parse(category: Long, file: String) =
        Files.newBufferedReader(File("public/${file}").toPath()).use { reader ->
            Files.newBufferedWriter(f.toPath(), StandardOpenOption.APPEND).use { writer ->
                var count = 0
                while (reader.ready()) {
                    val l = reader.readLine()
                    val parsed = gson.fromJson(l, DatasetProduct::class.java)
                    if (parsed.description.isNullOrEmpty())
                        continue
                    if (parsed.title.isNullOrBlank())
                        continue
                    if (parsed.price.isNullOrBlank())
                        continue
                    val price = parsed.price.trim().replace("$", "").toDoubleOrNull() ?: continue
                    val images =
                        parsed.image?.filter { img -> img.startsWith("https://") }?.map { i ->
                            i.trim().let { s ->
                                var aux = s
                                RENAMING_RULES.forEach { (old, to) -> if (s.contains(old)) aux = s.replace(old, to) }
                                aux
                            }
                        } ?: emptyList()

                    if (images.isEmpty())
                        continue

                    val desc = parsed.description[0]
                    if (desc.length > Constants.MAX_PRODUCT_DESC_LENGTH)
                        continue

                    val p = SavedProduct(
                        parsed.title.trim(),
                        parsed.brand?.trim() ?: "",
                        price,
                        category,
                        images,
                        parsed.category?.map { it.trim() } ?: emptyList(),
                        parsed.feature?.map { it.trim() } ?: emptyList(),
                        desc.trim()
                    )

                    val newLine = gson.toJson(p)
                    writer.write(newLine)
                    writer.newLine()

                    count++
                    if (count % 10000 == 0)
                        println("SAVED $file $count")
                }
            }
        }

    /*parse(1L, "rfashion")
    parse(2L, "rphones")
    parse(3L, "electronics")
    parse(4L, "rsports")
    parse(5L, "rgames")
    parse(6L, "home")
    parse(7L, "rappliances")*/
}*/

data class SavedProduct(
    val name: String,
    val brand: String,
    val price: Double,
    val category: Long,
    val images: List<String>,
    val categories: List<String>,
    val features: List<String>,
    val description: String
)

private fun p(
    name: String,
    brand: String,
    price: Double,
    category: Long,
    user: UserEntity? = null,
    vararg images: String,
    description: String? = null,
    external: Boolean = false
) = ProductEntity.add(
    Product(
        null,
        name,
        brand,
        price,
        Random.nextInt(0, 50),
        ProductCategory(category),
        false,
        description ?: DEFAULT_DESCRIPTION,
        emptyList(),
        date = randomDate(),
        userId = user?.id?.value ?: 1
    )
).toProduct()
    .also { ProductImageEntity.add(it.id!!, images.mapIndexed { index, i -> i(index, i, external) }) }

private fun randomPrice() = "%.2f".format(Random.nextDouble(5.0, 1000.0)).toDouble()

private const val PRODUCTS_SINCE = 6 * 30 * 24 * 3600 * 1000L
private const val DEFAULT_DESCRIPTION =
    "Nibh cras pulvinar mattis nunc sed blandit libero volutpat sed cras ornare arcu dui vivamus arcu felis bibendum ut tristique et egestas quis ipsum suspendisse ultrices gravida dictum fusce ut placerat orci nulla pellentesque dignissim enim sit amet venenatis urna cursus eget nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi tincidunt augue interdum velit euismod in pellentesque massa placerat duis ultricies lacus sed turpis tincidunt id aliquet risus feugiat in ante metus dictum at tempor commodo ullamcorper a lacus vestibulum sed arcu non odio euismod lacinia at quis risus sed vulputate odio ut enim blandit volutpat maecenas volutpat blandit aliquam etiam erat velit scelerisque in dictum non consectetur a erat nam at lectus urna duis convallis convallis "

private fun randomDate(): Instant {
    val date = Date()
    date.time = date.time - Random.nextLong(PRODUCTS_SINCE)
    return date.toInstant()
}

private val imageUris1 = listOf("a.jpg", "b.jpg", "c.jpg", "d.jpg")
private val imageUris2 = listOf("c.jpg", "b.jpg")
private val imageUris3 = listOf("f.jpg")
private val imageUris4 = listOf(
    "b.jpg",
    "a.jpg",
    "c.jpg",
    "d.jpg",
    "e.jpg",
    "f.jpg",
    "a.jpg",
    "b.jpg"
)

private val pickableImgSets = listOf(imageUris1, imageUris2, imageUris3, imageUris4)

private val images
    get() =
        pickableImgSets[Random.nextInt(pickableImgSets.size)]

private fun i(index: Int, name: String, external: Boolean = false) =
    ProductImage(index.toByte(), if (external) name else "public/img/${name}")