package io.samagra.odk.collect.extension.listeners

interface FormsProcessListener {

    /** Called when processing is complete. */
    fun onProcessed()

    /** Called when there is a processing error. */
    fun onProcessingError(e: Exception)
}
