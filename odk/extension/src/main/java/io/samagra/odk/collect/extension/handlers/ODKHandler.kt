package io.samagra.odk.collect.extension.handlers

import android.app.Application
import android.content.Context
import io.samagra.odk.collect.extension.interactors.FormInstanceInteractor
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.samagra.odk.collect.extension.components.DaggerFormInstanceInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerFormsDatabaseInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerFormsInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerFormsNetworkInteractorComponent
import io.samagra.odk.collect.extension.interactors.FormsDatabaseInteractor
import io.samagra.odk.collect.extension.interactors.FormsInteractor
import io.samagra.odk.collect.extension.interactors.FormsNetworkInteractor
import io.samagra.odk.collect.extension.interactors.ODKInteractor
import io.samagra.odk.collect.extension.listeners.FileDownloadListener
import io.samagra.odk.collect.extension.listeners.ODKProcessListener
import io.samagra.odk.collect.extension.utilities.ConfigHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.odk.collect.android.events.FormEventBus
import org.odk.collect.android.events.FormStateEvent
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import org.odk.collect.forms.Form
import org.odk.collect.forms.instances.Instance
import org.odk.collect.forms.instances.InstancesRepository
import java.io.File
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory

class ODKHandler @Inject constructor(
    private val application: Application
): ODKInteractor {

    private lateinit var formsNetworkInteractor: FormsNetworkInteractor

    override fun setupODK(settingsJson: String, lazyDownload: Boolean, listener: ODKProcessListener) {
        try {
            ConfigHandler(application).configure(settingsJson)
            formsNetworkInteractor = DaggerFormsNetworkInteractorComponent.factory().create(application).getFormsNetworkInteractor()
            if (!lazyDownload) {
                formsNetworkInteractor.downloadRequiredForms(object: FileDownloadListener {
                    override fun onProgress(progress: Int) { listener.onProgress(progress) }
                    override fun onComplete(downloadedFile: File) { listener.onProcessComplete() }
                    override fun onCancelled(exception: Exception) { listener.onProcessingError(exception) }
                })
            }
            else {
                listener.onProcessComplete()
            }
        } catch (e: IllegalStateException) {
            listener.onProcessingError(e)
        }
    }

    override fun resetODK(listener: ODKProcessListener) {
        CoroutineScope(Job()).launch{ ConfigHandler(application).reset(listener) }
    }

}
