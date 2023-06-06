package io.samagra.odk.collect.extension.handlers

import android.content.Context
import android.content.Intent
import android.util.Log
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.samagra.odk.collect.extension.interactors.FormInstanceInteractor
import io.samagra.odk.collect.extension.interactors.FormsDatabaseInteractor
import io.samagra.odk.collect.extension.interactors.FormsInteractor
import io.samagra.odk.collect.extension.interactors.FormsNetworkInteractor
import io.samagra.odk.collect.extension.listeners.FileDownloadListener
import io.samagra.odk.collect.extension.listeners.FormsProcessListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.javarosa.core.model.FormDef
import org.odk.collect.android.activities.FormEntryActivity
import org.odk.collect.android.events.FormEventBus
import org.odk.collect.android.events.FormStateEvent
import org.odk.collect.android.external.FormsContract
import org.odk.collect.android.formentry.loading.FormInstanceFileCreator
import org.odk.collect.android.formentry.saving.DiskFormSaver
import org.odk.collect.android.listeners.FormLoaderListener
import org.odk.collect.android.projects.CurrentProjectProvider
import org.odk.collect.android.storage.StoragePathProvider
import org.odk.collect.android.tasks.FormLoaderTask
import org.odk.collect.android.tasks.SaveFormToDisk
import org.odk.collect.android.utilities.ApplicationConstants
import org.odk.collect.android.utilities.MediaUtils
import org.odk.collect.entities.EntitiesRepository
import org.odk.collect.forms.Form
import org.odk.collect.forms.instances.Instance
import org.odk.collect.forms.instances.InstancesRepository
import org.w3c.dom.Document
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class ODKFormsHandler @Inject constructor(
    private val currentProjectProvider: CurrentProjectProvider,
    private val formsDatabaseInteractor: FormsDatabaseInteractor,
    private val storagePathProvider: StoragePathProvider,
    private val mediaUtils: MediaUtils,
    private val entitiesRepository: EntitiesRepository,
    private val formsNetworkInteractor: FormsNetworkInteractor,
    private val instancesRepository: InstancesRepository,
    private val formInstanceInteractor: FormInstanceInteractor
): FormsInteractor {

    override fun openFormWithFormId(formId: String, context: Context) {
        val form = formsDatabaseInteractor.getLatestFormById(formId)
        // Note: If the given form does not exist, it is the responsibility
        // of the caller to download it.
        if (form == null) {
            Log.e("FORMS ERROR", "The given formId does not exist!")
            return
        }
        openForm(form, context)
    }

    override fun openFormWithMd5Hash(md5Hash: String, context: Context) {
        val form = formsDatabaseInteractor.getFormByMd5Hash(md5Hash)
        if (form == null) {
            Log.e("FORMS ERROR", "The given formId does not exist!")
            return
        }
        openForm(form, context)
    }

    override fun prefillForm(formId: String, tagValueMap: HashMap<String, String>) {
        CoroutineScope(Job()).launch {
            val form = formsDatabaseInteractor.getLatestFormById(formId)
            val formInstanceUri = FormsContract.getUri(currentProjectProvider.getCurrentProject().uuid, form?.dbId)
            if (form != null && formInstanceUri != null) {
                val formLoaderTask = FormLoaderTask(null, null, null)
                formLoaderTask.setFormLoaderListener(object: FormLoaderListener {
                    override fun onProgressStep(stepMessage: String?) {}
                    override fun loadingComplete(task: FormLoaderTask?, fd: FormDef?, warningMsg: String?) {
                        val formController = formLoaderTask.formController
                        if (formController != null) {
                            val formInstanceFileCreator = FormInstanceFileCreator(
                                storagePathProvider
                            ) { System.currentTimeMillis() }
                            val instanceFile = formInstanceFileCreator.createInstanceFile(form.formFilePath)
                            if (instanceFile != null) {
                                formController.setInstanceFile(instanceFile)
                                val saveToDiskResult = DiskFormSaver().save(
                                    formInstanceUri, formController, mediaUtils, false,
                                    false, null, {}, null, arrayListOf(),
                                    currentProjectProvider.getCurrentProject().uuid, entitiesRepository
                                )
                                if (saveToDiskResult.saveResult == SaveFormToDisk.SAVED) {
                                    updateForm(instanceFile.absolutePath, tagValueMap, null)
                                    FormEventBus.formSaved(formId, instanceFile.absolutePath)
                                }
                                else {
                                    FormEventBus.formSaveError(formId, "Form could not be saved!")
                                }
                            } else {
                                FormEventBus.formOpenFailed(formId, "Form instance could not be created!")
                            }
                        }
                        else {
                            FormEventBus.formOpenFailed(formId, "FormController is null!")
                        }
                    }
                    override fun loadingError(errorMsg: String?) {
                        FormEventBus.formOpenFailed(formId, errorMsg ?: "Form cannot be loaded!")
                    }
                })
                formLoaderTask.execute(form.formFilePath)
            }
            else {
                FormEventBus.formOpenFailed(formId, "Form does not exist in database!")
            }
        }
    }

    override fun prefillForm(formId: String, tag: String, value: String) {
        prefillForm(formId, hashMapOf(tag to value))
    }

    private fun openForm(form: Form, context: Context) {
        val contentUri = FormsContract.getUri(currentProjectProvider.getCurrentProject().uuid, form.dbId)
        val formEntryIntent = Intent(context, FormEntryActivity::class.java)
        formEntryIntent.action = Intent.ACTION_EDIT
        formEntryIntent.data = contentUri
        formEntryIntent.putExtra(
            ApplicationConstants.BundleKeys.FORM_MODE,
            ApplicationConstants.FormModes.EDIT_SAVED
        )
        context.startActivity(formEntryIntent)
    }

    override fun updateForm(formPath: String, tag: String, tagValue: String, listener: FormsProcessListener?) {
        var fos: FileOutputStream? = null
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(File(formPath))
            document.documentElement.normalize()
            if (document != null) {
                updateDocumentBasedOnTag(document, tag, tagValue)
                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()
                val source = DOMSource(document)
                fos = FileOutputStream(File(formPath))
                val result = StreamResult(fos)
                transformer.transform(source, result)
            }
        } catch (e: Exception) {
            listener?.onProcessingError(e)
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                    listener?.onProcessed()
                } catch (e: IOException) {
                    listener?.onProcessingError(e)
                }
            }
        }
    }

    override fun updateForm(formPath: String, values: HashMap<String, String>, listener: FormsProcessListener?) {
        var progress = 0
        for (entry in values.entries) {
            updateForm(formPath, entry.key, entry.value, object : FormsProcessListener {
                override fun onProcessed() {
                    progress++
                    if (progress == values.size)
                        listener?.onProcessed()
                }
                override fun onProcessingError(e: Exception) {
                    listener?.onProcessingError(e)
                }
            })
        }
    }
    private fun updateDocumentBasedOnTag(document: Document, tag: String, tagValue: String): Document {
        try {
            if (document.getElementsByTagName(tag).item(0).childNodes.length > 0)
                document.getElementsByTagName(tag)
                    .item(0)
                    .childNodes
                    .item(0)
                    .nodeValue = tagValue
            else
                document.getElementsByTagName(tag).item(0).appendChild(document.createTextNode(tagValue))
        } catch (e: java.lang.Exception) {
            return document
        }
        return document
    }

    override fun openForm(formId: String, context: Context) {
        CoroutineScope(Job()).launch {
            // Delete any saved instances of this form
            val savedInstances = instancesRepository.getAllByFormId(formId)
            for (instance in savedInstances) {
                if (instance.status == Instance.STATUS_INCOMPLETE) {
                    instancesRepository.delete(instance.dbId)
                }
            }
            val requiredForm = formsDatabaseInteractor.getLatestFormById(formId)
            if (requiredForm == null) {
                downloadAndOpenForm(formId, context)
            }
            else {
                val xmlFile = File(requiredForm.formFilePath)
                if (xmlFile.exists() && (requiredForm.formMediaPath == null || mediaExists(requiredForm))) {
                    openFormWithFormId(formId, context)
                }
                else {
                    requiredForm.formMediaPath?.let { File(it).deleteRecursively() }
                    xmlFile.delete()
                    formsDatabaseInteractor.deleteByFormId(formId)
                    downloadAndOpenForm(formId, context)
                }
            }
        }
    }

    override fun openSavedForm(formId: String, context: Context) {
        CoroutineScope(Job()).launch {
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(
                FormEventBus.getState()
                    .subscribe { event ->
                        when (event) {
                            is FormStateEvent.OnFormOpenFailed -> {
                                if (event.formId == formId) {
                                    compositeDisposable.clear()
                                    openForm(formId, context)
                                }
                            }
                            is FormStateEvent.OnFormOpened -> {
                                if (event.formId == formId) {
                                    compositeDisposable.clear()
                                }
                            }
                            else -> {}
                        }
                    }
            )

            formInstanceInteractor.openLatestSavedInstanceWithFormId(formId, context)
        }
    }

    override fun prefillAndOpenForm(formId: String, tagValueMap: HashMap<String, String>, context: Context) {
        CoroutineScope(Job()).launch {
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(FormEventBus.getState().subscribe { event ->
                when (event) {
                    is FormStateEvent.OnFormSaved -> {
                        if (event.formId == formId) {
                            val prefilledInstance = formInstanceInteractor.getInstanceByPath(event.instancePath)
                            if (prefilledInstance != null) {
                                formInstanceInteractor.openInstance(prefilledInstance, context)
                            }
                            else {
                                FormEventBus.formOpenFailed(formId, "Form instance cannot be found!")
                            }
                            compositeDisposable.clear()
                        }
                    }
                    is FormStateEvent.OnFormOpenFailed -> if (event.formId == formId) compositeDisposable.clear()
                    is FormStateEvent.OnFormSaveError -> if (event.formId == formId) compositeDisposable.clear()
                    else -> {}
                }
            })
            prefillForm(formId, tagValueMap)
        }
    }

    private fun downloadAndOpenForm(formId: String, context: Context) {
        formsNetworkInteractor.downloadFormById(formId, object : FileDownloadListener {
            override fun onComplete(downloadedFile: File) {
                openFormWithFormId(formId, context)
            }
        })
    }

    private fun mediaExists(form: Form): Boolean {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(form.formFilePath))
        val values = document.getElementsByTagName("value")
        for (index in 0 until values.length) {
            val attributes = values.item(index).attributes
            if (attributes.length > 0) {
                val nodeValue = attributes.item(0).nodeValue
                if (nodeValue == "image" || nodeValue == "audio" || nodeValue == "video") {
                    var mediaFileName = values.item(index).firstChild.nodeValue
                    if (mediaFileName.isNotBlank()) {
                        mediaFileName = mediaFileName.substring(mediaFileName.lastIndexOf("/") + 1)
                        val mediaFile = File(form.formMediaPath + "/" + mediaFileName)
                        if (!mediaFile.exists())
                            return false
                    }
                }
            }
        }
        return true
    }
}
