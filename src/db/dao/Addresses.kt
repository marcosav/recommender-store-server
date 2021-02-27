package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.model.Address
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Addresses : LongIdTable() {
    val recipient = varchar("recipient", 75)
    val code = varchar("code", 10)
    val city = varchar("city", 63)
    val region = varchar("region", 63)
    val country = varchar("country", 24)
    val address = varchar("address", 63)
    val phone = varchar("phone", 63)
    val user = reference("user", Users)
}

class AddressEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<AddressEntity>(Addresses)

    var recipient by Addresses.recipient
    var code by Addresses.code
    var city by Addresses.city
    var region by Addresses.region
    var country by Addresses.country
    var address by Addresses.address
    var phone by Addresses.phone

    var user by UserEntity referencedOn Addresses.user

    fun toAddress() =
        Address(
            recipient,
            code,
            city,
            region,
            country,
            address,
            phone
        )
}