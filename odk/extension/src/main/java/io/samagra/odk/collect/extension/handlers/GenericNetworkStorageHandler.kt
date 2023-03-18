package io.samagra.odk.collect.extension.handlers

import io.samagra.odk.collect.extension.AppConstants
import io.samagra.odk.collect.extension.interactors.NetworkStorageInteractor
import io.samagra.odk.collect.extension.interactors.StorageInteractor
import io.samagra.odk.collect.extension.listeners.FileDownloadListener
import io.samagra.odk.collect.extension.listeners.FileExtractorListener
import io.samagra.odk.collect.extension.utilities.ZipExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.odk.collect.android.storage.StoragePathProvider
import org.odk.collect.android.storage.StorageSubdirectory
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


class GenericNetworkStorageHandler @Inject constructor(
    private val storageInteractor: StorageInteractor,
    private val storagePathProvider: StoragePathProvider
    ): NetworkStorageInteractor {
    override fun getUpdatedZipHash(downloadPath: String?): String? {
        if (downloadPath == null) return null
        return try {
            val conn: HttpURLConnection = URL(downloadPath).openConnection() as HttpURLConnection
            conn.requestMethod = "HEAD"
            conn.connectTimeout = 2000
            conn.inputStream
            //TODO: check with last changed as well
            conn.headerFields["ETag"]?.get(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun downloadFormsZip(
        downloadPath: String,
        fileDownloadListener: FileDownloadListener,
    ) {
        CoroutineScope(Job()).launch {
            val tempFile = storageInteractor.createTempFile()
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val url = URL(downloadPath)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 2000
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    fileDownloadListener.onCancelled(FileNotFoundException())
                    return@launch
                }

                val fileLength: Int = connection.contentLength

                input = connection.inputStream
                output = FileOutputStream(tempFile)
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    if (fileLength > 0)
                        fileDownloadListener.onProgress((total * 100 / fileLength).toInt())
                    output.write(data, 0, count)
                }

                ZipExtractor().extract(
                    tempFile.absolutePath,
                    storagePathProvider.getProjectRootDirPath(),
                    object : FileExtractorListener {
                        override fun onExtractionComplete() {
                            tempFile.deleteOnExit()
                            //TODO: handle this
                            if (connection.headerFields["ETag"] != null)
                                storageInteractor.setPreference(AppConstants.ZIP_HASH_KEY, connection.headerFields["ETag"]!![0])
                            fileDownloadListener.onComplete(File(storagePathProvider.getOdkDirPath(StorageSubdirectory.FORMS)))
                        }
                        override fun onProgress(progress: Int) {
                            fileDownloadListener.onProgress(progress)
                        }
                        override fun onFailed(e: Exception) {
                            tempFile.deleteOnExit()
                            fileDownloadListener.onCancelled(e)
                        }
                    }
                )
            } catch (e: Exception) {
                fileDownloadListener.onCancelled(e)
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (ignored: IOException) {
                }
                connection?.disconnect()
            }
        }
    }
}