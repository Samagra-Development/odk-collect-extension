package io.samagra.odk.collect.extension.handlers

import android.app.Application
import io.samagra.odk.collect.extension.components.DaggerFormsNetworkInteractorComponent
import io.samagra.odk.collect.extension.interactors.FormsNetworkInteractor
import io.samagra.odk.collect.extension.interactors.ODKInteractor
import io.samagra.odk.collect.extension.listeners.FileDownloadListener
import io.samagra.odk.collect.extension.listeners.ODKProcessListener
import io.samagra.odk.collect.extension.utilities.ConfigHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ODKHandler @Inject constructor(
    private val application: Application
) : ODKInteractor {

    private lateinit var formsNetworkInteractor: FormsNetworkInteractor

    override fun setupODK(
        settingsJson: String,
        lazyDownload: Boolean,
        listener: ODKProcessListener
    ) {
        try {
            ConfigHandler(application).configure(settingsJson)
            formsNetworkInteractor =
                DaggerFormsNetworkInteractorComponent.factory().create(application)
                    .getFormsNetworkInteractor()

            if (!lazyDownload) {
                formsNetworkInteractor.downloadRequiredForms(object : FileDownloadListener {
                    override fun onProgress(progress: Int) {
                        listener.onProgress(progress)
                    }

                    override fun onComplete(downloadedFile: File) {
                        listener.onProcessComplete()
                    }

                    override fun onCancelled(exception: Exception) {
                        listener.onProcessingError(exception)
                    }
                })
            } else {
                listener.onProcessComplete()
            }
        } catch (e: IllegalStateException) {
            listener.onProcessingError(e)
        }
    }

    override fun resetODK(listener: ODKProcessListener) {
        CoroutineScope(Job()).launch { ConfigHandler(application).reset(listener) }
    }

}
