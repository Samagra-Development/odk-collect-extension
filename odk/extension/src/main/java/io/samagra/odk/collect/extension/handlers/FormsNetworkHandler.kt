package io.samagra.odk.collect.extension.handlers

import io.samagra.odk.collect.extension.AppConstants
import io.samagra.odk.collect.extension.interactors.FormsNetworkInteractor
import io.samagra.odk.collect.extension.interactors.NetworkStorageInteractor
import io.samagra.odk.collect.extension.interactors.StorageInteractor
import io.samagra.odk.collect.extension.listeners.FileDownloadListener
import io.samagra.odk.collect.extension.utilities.FormsDownloadUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.odk.collect.android.events.FormEventBus
import org.odk.collect.android.formmanagement.FormDownloadException
import org.odk.collect.android.formmanagement.ServerFormDetails
import org.odk.collect.android.listeners.DownloadFormsTaskListener
import org.odk.collect.android.listeners.FormListDownloaderListener
import org.odk.collect.android.storage.StoragePathProvider
import org.odk.collect.android.storage.StorageSubdirectory
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class FormsNetworkHandler @Inject constructor(
    private val formsDownloadUtil: FormsDownloadUtil,
    private val networkStorageInteractor: NetworkStorageInteractor,
    private val storageInteractor: StorageInteractor,
    private val storagePathProvider: StoragePathProvider
    ): FormsNetworkInteractor {

    override fun getNewForms(listener: FormListDownloaderListener) {
        formsDownloadUtil.downloadFormsList{formsList, exception ->
            if (exception != null || formsList == null) {
                listener.formListDownloadingComplete(null, exception)
            }
            else {
                listener.formListDownloadingComplete(formsList.filter { form ->
                    form.value.isUpdated || form.value.isNotOnDevice
                } as HashMap, null)
            }
        }
    }

    override fun hasZipChanged(zipPath: String?): Boolean {
        val updatedHash = networkStorageInteractor.getUpdatedZipHash(zipPath)
        val localHash = storageInteractor.getPreference(AppConstants.ZIP_HASH_KEY)
        return updatedHash == null || updatedHash != localHash
    }

    override fun downloadRequiredForms(listener: FileDownloadListener) {
        CoroutineScope(Job()).launch {
            if (hasZipChanged(AppConstants.ZIP_PATH)) {
                networkStorageInteractor.downloadFormsZip(AppConstants.ZIP_PATH, object:
                    FileDownloadListener {
                    override fun onProgress(progress: Int) {
                        listener.onProgress(progress)
                    }
                    override fun onComplete(downloadedFile: File) {
                        checkIndividualForms(listener)
                    }
                    override fun onCancelled(exception: Exception) {
                        checkIndividualForms(listener)
                    }
                })
            }
            else {
                checkIndividualForms(listener)
            }
        }
    }

    private fun checkIndividualForms(listener: FileDownloadListener) {
        getNewForms { formList, exception ->
            if (exception != null || formList == null) {
                listener.onCancelled(exception)
                return@getNewForms
            }
            else
                formsDownloadUtil.downloadFormsFromList(ArrayList(formList.values), object: DownloadFormsTaskListener{
                    override fun formsDownloadingComplete(result: MutableMap<ServerFormDetails, FormDownloadException?>?) {
                        /* Warning: Failed downloads have not been checked here.
                        *  Some forms might not have been downloaded due to some error,
                        *  make sure to check a form exists before opening any form. */
                        listener.onComplete(File(storagePathProvider.getOdkDirPath(StorageSubdirectory.FORMS)))
                    }
                    override fun progressUpdate(
                        currentFile: String?,
                        progress: Int,
                        total: Int
                    ) {
                        listener.onProgress((progress * 100)/total)
                    }
                    override fun formsDownloadingCancelled() {
                        listener.onCancelled(FormDownloadException.DownloadingInterrupted())
                    }
                })
        }
    }

    override fun downloadFormsFromList(
        formsList: ArrayList<ServerFormDetails>,
        listener: DownloadFormsTaskListener
    ) {
        CoroutineScope(Job()).launch {
            val filteredList = formsList.filter { form-> form.isUpdated || form.isNotOnDevice } as ArrayList
            formsDownloadUtil.downloadFormsFromList(filteredList, listener)
        }
    }

    override fun downloadFormsFromIdList(
        formIds: ArrayList<String>,
        listener: DownloadFormsTaskListener
    ) {
        val formsToDownload = HashSet(formIds)
        formsDownloadUtil.downloadFormsList { formsList, exception ->
            if (exception != null || formsList == null) {
                listener.formsDownloadingCancelled()
                return@downloadFormsList
            }
            val filteredList = formsList.filter { form -> formsToDownload.contains(form.key) }
            downloadFormsFromList(ArrayList(filteredList.values), listener)
        }
    }

    override fun downloadFormsList(listener: FormListDownloaderListener) {
        formsDownloadUtil.downloadFormsList(listener)
    }

    override fun downloadFormById(formId: String, listener: FileDownloadListener) {
        formsDownloadUtil.downloadFormsList { formList, exception ->
            if (exception != null || formList == null) {
                listener.onCancelled(exception)
                FormEventBus.formDownloadFailed(formId, exception.message ?: "Form list download failed!")
                return@downloadFormsList
            }
            val requiredForms = ArrayList(formList.filter { form -> form.value.formId == formId }.values)
            requiredForms.sortWith { form1, form2 ->
                if (form1.formVersion == null) 1
                else if (form2.formVersion == null) -1
                else form1.formVersion!!.compareTo(form2.formVersion!!)
            }
            val latestForm = if (requiredForms.size > 0) requiredForms[0] else null
            if (latestForm == null) {
                listener.onCancelled(FileNotFoundException("Form does not exist on server!"))
                return@downloadFormsList
            }
            formsDownloadUtil.downloadFormsFromList(arrayListOf(latestForm), object: DownloadFormsTaskListener {
                override fun formsDownloadingComplete(result: MutableMap<ServerFormDetails, FormDownloadException>?) {
                    val exceptionsList = result?.values?.toList()
                    val downloadException = if (exceptionsList != null && exceptionsList.isNotEmpty()) exceptionsList[0] else null
                    if (downloadException != null) listener.onCancelled(downloadException)
                    listener.onComplete(File(storagePathProvider.getOdkDirPath(StorageSubdirectory.FORMS) + File.separator + latestForm.manifest))
                }
                override fun progressUpdate(currentFile: String?, progress: Int, total: Int) {
                    listener.onProgress((progress * 100)/total)
                }
                override fun formsDownloadingCancelled() {
                    listener.onCancelled(FormDownloadException.DownloadingInterrupted())
                }
            })
        }
    }

    override fun downloadFormWithServerDetail(form: ServerFormDetails, listener: DownloadFormsTaskListener) {
        if (form.isUpdated || form.isNotOnDevice) {
            formsDownloadUtil.downloadFormsFromList(arrayListOf(form), listener)
        }
    }
}