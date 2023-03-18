package io.samagra.odk.collect.extension.interactors

import io.samagra.odk.collect.extension.listeners.FileDownloadListener

/** This interface is responsible for handling all the storage utilities
 *  over the network.
 */
interface NetworkStorageInteractor {

    /** Fetches the updated hash of the forms zip. */
    fun getUpdatedZipHash(downloadPath: String?): String?

    /** Downloads the forms zip from server. */
    fun downloadFormsZip(downloadPath: String, fileDownloadListener: FileDownloadListener)
}