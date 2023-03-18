package io.samagra.odk.collect.extension.utilities

import io.samagra.odk.collect.extension.listeners.FileExtractorListener
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/** Utility to extract zip file. */
class ZipExtractor {

    /** Takes a zip file path and a target directory to extract the zip into.
        Returns IOException in case one occurs, else returns null.
     */
    fun extract(zipPath: String, targetDirectory: String, listener: FileExtractorListener): IOException? {
        try {
            val zis = ZipInputStream(BufferedInputStream(FileInputStream(zipPath)))
            var ze: ZipEntry?
            var count: Int
            val buffer = ByteArray(4096)
            val totalSize = File(zipPath).length()
            var totalExtracted = 0L
            while (zis.nextEntry.also { ze = it } != null) {
                val file = File(targetDirectory, ze!!.name)
                val dir = if (ze!!.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                    "Failed to ensure directory: " +
                            dir.absolutePath
                )
                if (ze!!.isDirectory) continue
                FileOutputStream(file).use { fout ->
                    while (zis.read(buffer).also { count = it } != -1) {
                        fout.write(buffer, 0, count)
                        totalExtracted += count
                        listener.onProgress(((100 * totalExtracted)/totalSize).toInt())
                    }
                }
            }
            listener.onExtractionComplete()
            zis.close()
        } catch (e: IOException) {
            e.printStackTrace()
            listener.onFailed(e)
            return e
        }

        return null
    }
}