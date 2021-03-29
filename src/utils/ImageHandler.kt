package com.gmail.marcosav2010.utils

import com.gmail.marcosav2010.Constants
import io.ktor.http.content.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*


object ImageHandler {

    val STATIC_FILES_ROUTE = System.getenv(Constants.PUBLIC_PATH_ENV) ?: Constants.DEFAULT_PUBLIC_PATH
    val IMG_FOLDER_ROUTE = System.getenv(Constants.IMG_PATH_ENV) ?: Constants.DEFAULT_IMG_PATH

    val IMG_PATH = "${STATIC_FILES_ROUTE}/${IMG_FOLDER_ROUTE}"

    suspend fun process(part: PartData.FileItem): String {
        val ext = File(part.originalFileName!!).extension

        val name = UUID.randomUUID()
        val file = File("$IMG_PATH/$name.$ext")
        if (!file.parentFile.exists())
            file.parentFile.mkdirs()

        part.streamProvider()
            .use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }

        return file.path
    }

    private suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 4 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Long {
        return withContext(dispatcher) {
            val buffer = ByteArray(bufferSize)
            var bytesCopied = 0L
            var bytesAfterYield = 0L

            while (true) {
                if (bytesCopied > Constants.MAX_IMAGE_BYTE_SIZE)
                    throw OversizeImageException()

                val bytes = read(buffer).takeIf { it >= 0 } ?: break
                out.write(buffer, 0, bytes)

                if (bytesAfterYield >= yieldSize) {
                    yield()
                    bytesAfterYield %= yieldSize
                }

                bytesCopied += bytes
                bytesAfterYield += bytes
            }

            return@withContext bytesCopied
        }
    }

    /*private fun handleImage(input: InputStream, out: OutputStream) {
        val originalImage = ImageIO.read(input)
        var rw = originalImage.width
        var rh = originalImage.height
        val s = max(rw, rh)
        if (s > Constants.MAX_IMAGE_SIZE) {
            val prop = Constants.MAX_IMAGE_SIZE.toDouble() / s.toDouble()
            rh = (rh * prop).toInt()
            rw = (rw * prop).toInt()

            ImageIO.write(resizeImage(originalImage, rw, rh), "jpg", out)
        }
    }

    private fun resizeImage(originalImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val resultingImage: Image = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_FAST)
        val outputImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        outputImage.graphics.drawImage(resultingImage, 0, 0, null)
        return outputImage
    }*/
}

class OversizeImageException : Exception()