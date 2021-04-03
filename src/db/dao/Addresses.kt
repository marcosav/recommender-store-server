package com.gmail.marcosav2010.db.dao

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Address
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Addresses : LongIdTable() {
    val recipient = varchar("recipient", Constants.MAX_ADDRESS_RECIPIENT_LENGTH)
    val code = varchar("code", Constants.MAX_ADDRESS_CODE_LENGTH)
    val city = varchar("city", Constants.MAX_ADDRESS_CITY_LENGTH)
    val region = varchar("region", Constants.MAX_ADDRESS_REGION_LENGTH)
    val country = varchar("country", Constants.MAX_ADDRESS_COUNTRY_LENGTH)
    val address = varchar("address", Constants.MAX_ADDRESS_LENGTH)
    val phone = varchar("phone", Constants.MAX_ADDRESS_PHONE_LENGTH)
}

class AddressEntity(id: EntityID<Long>) : LongEntity(id) {

    companion object : BaseEntityClass<AddressEntity>(Addresses) {
        fun add(address: Address) =
            new {
                recipient = address.recipient
                code = address.code
                city = address.city
                region = address.region
                country = address.country
                this.address = address.address
                phone = address.phone
            }
    }

    var recipient by Addresses.recipient
    var code by Addresses.code
    var city by Addresses.city
    var region by Addresses.region
    var country by Addresses.country
    var address by Addresses.address
    var phone by Addresses.phone

    fun toAddress() =
        Address(
            id.value,
            recipient,
            code,
            city,
            region,
            country,
            address,
            phone
        )
}