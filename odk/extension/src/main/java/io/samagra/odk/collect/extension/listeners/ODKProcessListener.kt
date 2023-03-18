package io.samagra.odk.collect.extension.listeners

interface ODKProcessListener {

    /** Called when the progress is updated for a process. */
    fun onProgress(progress: Int) = Unit

    /** Called when the ODK process completes successfully. */
    fun onProcessComplete()

    /** Called when the ODK process fails due to some error. */
    fun onProcessingError(exception: Exception)
}