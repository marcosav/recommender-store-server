package com.gmail.marcosav2010.model

enum class Role(val id: Byte) {

    ANONYMOUS(0),
    MEMBER(1),
    ADMIN(10);

    companion object {
        fun byId(id: Byte) = values().find { it.id == id }
    }
}