package com.gmail.marcosav2010.utils

import java.io.File
import java.nio.file.Files
import java.util.*


object ImageSaver {

    // TODO: set path (env var)
    private const val IMG_PATH = ""

    fun process(encoded: String, ext: String): String {
        val name = UUID.randomUUID()
        val f = File("$IMG_PATH/$name.$ext")
        if (!f.parentFile.exists())
            f.parentFile.mkdir()

        val decoded = Base64.getDecoder().decode(encoded)
        Files.write(f.toPath(), decoded)
        // TODO: improve saving with an stream to avoid overloads
        return f.path
    }
}