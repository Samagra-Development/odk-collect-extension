package io.samagra.odk.collect.extension.handlers

import android.content.Context
import android.content.Intent
import android.util.Log
import io.samagra.odk.collect.extension.interactors.FormsDatabaseInteractor
import io.samagra.odk.collect.extension.interactors.FormsInteractor
import io.samagra.odk.collect.extension.listeners.FormsProcessListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.javarosa.core.model.FormDef
import org.odk.collect.android.activities.FormEntryActivity
import org.odk.collect.android.events.FormEventBus
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
    private val entitiesRepository: EntitiesRepository
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
}
