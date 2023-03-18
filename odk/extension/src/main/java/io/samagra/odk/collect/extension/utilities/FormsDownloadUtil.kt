package io.samagra.odk.collect.extension.utilities

import org.odk.collect.android.formmanagement.FormDownloader
import org.odk.collect.android.formmanagement.ServerFormDetails
import org.odk.collect.android.formmanagement.ServerFormsDetailsFetcher
import org.odk.collect.android.listeners.DownloadFormsTaskListener
import org.odk.collect.android.listeners.FormListDownloaderListener
import org.odk.collect.android.tasks.DownloadFormListTask
import org.odk.collect.android.tasks.DownloadFormsTask
import javax.inject.Inject

class FormsDownloadUtil @Inject constructor(
    private val serverFormsDetailsFetcher: ServerFormsDetailsFetcher,
    private val formDownloader: FormDownloader
) {
    fun downloadFormsList(formListDownloaderListener: FormListDownloaderListener) {
        val downloadFormListTask = DownloadFormListTask(serverFormsDetailsFetcher)
        downloadFormListTask.setDownloaderListener(formListDownloaderListener)
        downloadFormListTask.execute()
    }

    fun downloadFormsFromList(
        formsList: ArrayList<ServerFormDetails>,
        downloadFormsTaskListener: DownloadFormsTaskListener
    ) {
        val downloadFormsTask = DownloadFormsTask(formDownloader)
        downloadFormsTask.setDownloaderListener(downloadFormsTaskListener)
        downloadFormsTask.execute(formsList)
    }
}