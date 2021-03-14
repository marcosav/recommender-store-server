package com.gmail.marcosav2010.utils

import com.gmail.marcosav2010.Constants
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.util.*


object ImageHandler {

    private const val B64_BYTES_FACTOR = 1.334

    val STATIC_FILES_ROUTE = System.getenv(Constants.PUBLIC_PATH_ENV) ?: Constants.DEFAULT_PUBLIC_PATH
    val IMG_FOLDER_ROUTE = System.getenv(Constants.IMG_PATH_ENV) ?: Constants.DEFAULT_IMG_PATH

    private val IMG_PATH = "${STATIC_FILES_ROUTE}/${IMG_FOLDER_ROUTE}"

    fun calculateOriginalByteSize(base64: String) = base64.length.toDouble() / B64_BYTES_FACTOR

    fun process(encoded: String, ext: String): String {
        val name = UUID.randomUUID()
        val f = File("$IMG_PATH/$name.$ext")
        if (!f.parentFile.exists())
            f.parentFile.mkdir()

        val decoded = Base64.getDecoder().decode(encoded)
        Files.write(f.toPath(), decoded)

        return f.path
    }

    fun resizeImage(originalImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resultingImage: Image = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT)
        val outputImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        outputImage.graphics.drawImage(resultingImage, 0, 0, null)
        return outputImage
    }
}