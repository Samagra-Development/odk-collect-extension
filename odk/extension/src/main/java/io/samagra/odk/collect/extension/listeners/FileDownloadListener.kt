package io.samagra.odk.collect.extension.listeners

import java.io.File

/** A listener to to keep track of events during file downloads. */
interface FileDownloadListener {

    /** Called when there is update in download progress. */
    fun onProgress(progress: Int) {}

    /** Called when the download is complete.
     *  Takes the downloaded file as an argument. */
    fun onComplete(downloadedFile: File) {}

    /** Called if the download is cancelled due to any error.
     *  Takes the exception occurred as an argument. */
    fun onCancelled(exception: Exception) {}
}