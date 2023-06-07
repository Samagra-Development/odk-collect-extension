package org.odk.collect.android.formmanagement

import org.odk.collect.android.utilities.FileUtils.copyFile
import org.odk.collect.android.utilities.FileUtils.interuptablyWriteFile
import org.odk.collect.async.OngoingWorkListener
import org.odk.collect.forms.*
import org.odk.collect.shared.strings.Md5.getMd5Hash
import java.io.File
import java.io.IOException
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class FormMediaDownloader(
    private val formsRepository: FormsRepository,
    private val formSource: FormSource
) {

    private val lock: Lock = ReentrantLock()
    @Throws(IOException::class, FormSourceException::class, InterruptedException::class)
    fun download(
        formToDownload: ServerFormDetails,
        files: List<MediaFile>,
        tempMediaPath: String,
        tempDir: File,
        stateListener: OngoingWorkListener
    ): Boolean {
        var atLeastOneNewMediaFileDetected = false
        val tempMediaDir = File(tempMediaPath).also { it.mkdir() }

        files.forEachIndexed { i, mediaFile ->
            stateListener.progressUpdate(i + 1)

            val tempMediaFile = File(tempMediaDir, mediaFile.filename)

            searchForExistingMediaFile(formToDownload, mediaFile).let {
                if (it != null) {
                    copyFile(it, tempMediaFile)
                } else {
                    atLeastOneNewMediaFileDetected = true
                    val file = formSource.fetchMediaFile(mediaFile.downloadUrl)
                    lock.lock()
                    interuptablyWriteFile(file, tempMediaFile, tempDir, stateListener)
                    lock.unlock()
                }
            }
        }
        return atLeastOneNewMediaFileDetected
    }

    private fun searchForExistingMediaFile(
        formToDownload: ServerFormDetails,
        mediaFile: MediaFile
    ): File? {
        val allFormVersions = formsRepository.getAllByFormId(formToDownload.formId)
        return allFormVersions.map { form: Form ->
            File(form.formMediaPath, mediaFile.filename)
        }.firstOrNull { file: File ->
            val currentFileHash = getMd5Hash(file)
            val downloadFileHash = mediaFile.hash
            file.exists() && currentFileHash.contentEquals(downloadFileHash)
        }
    }
}
