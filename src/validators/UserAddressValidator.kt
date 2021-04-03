package com.gmail.marcosav2010.validators

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.model.Address

class UserAddressValidator : Validator<Address>() {

    override fun validation(): ValidationContext.(Address) -> Unit = {
        mandatory("recipient", it.recipient)
        mandatory("code", it.code)
        mandatory("city", it.city)
        mandatory("region", it.region)
        mandatory("country", it.country)
        mandatory("address", it.address)
        mandatory("phone", it.phone)

        maxLength("recipient", it.recipient, Constants.MAX_ADDRESS_RECIPIENT_LENGTH)
        maxLength("code", it.code, Constants.MAX_ADDRESS_CODE_LENGTH)
        maxLength("city", it.city, Constants.MAX_ADDRESS_CITY_LENGTH)
        maxLength("region", it.region, Constants.MAX_ADDRESS_REGION_LENGTH)
        maxLength("country", it.country, Constants.MAX_ADDRESS_COUNTRY_LENGTH)
        maxLength("address", it.address, Constants.MAX_ADDRESS_LENGTH)
        maxLength("phone", it.phone, Constants.MAX_ADDRESS_PHONE_LENGTH)
    }
}