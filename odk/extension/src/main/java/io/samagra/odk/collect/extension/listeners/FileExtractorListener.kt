package io.samagra.odk.collect.extension.listeners

/** Listener to keep track of events during extraction of a compressed file. */
interface FileExtractorListener {

    /** Called when the extraction process is complete. */
    fun onExtractionComplete()

    /** Called when the extraction process progresses. */
    fun onProgress(progress: Int)

    /** Called if the extraction process fails. */
    fun onFailed(e: Exception)
}
