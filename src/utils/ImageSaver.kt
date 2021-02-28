package com.gmail.marcosav2010.utils

import java.io.File
import java.nio.file.Files
import java.util.*


object ImageSaver {

    private const val IMG_PATH = ""

    fun process(encoded: String, ext: String): String {
        val name = UUID.randomUUID()
        val f = File("$IMG_PATH/$name.$ext")
        if (!f.parentFile.exists())
            f.parentFile.mkdir()

        val decoded = Base64.getDecoder().decode(encoded)
        Files.write(f.toPath(), decoded)

        return f.path
    }
}