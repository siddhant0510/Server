package Server.skcoding.util

import Server.skcoding.util.Constants.PROFILE_PICTURE_PATH
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import java.io.File
import java.util.UUID

fun PartData.FileItem.save(path: String): String {
    val fileBytes = streamProvider().readBytes()
    val fileExtension  = originalFileName?.takeLastWhile { it != '.'}
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    val folder = File(path)
    folder.mkdirs()
    File("$path$fileName").writeBytes(fileBytes)
    return fileName
}