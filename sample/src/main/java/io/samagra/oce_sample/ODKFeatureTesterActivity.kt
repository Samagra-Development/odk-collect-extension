package io.samagra.oce_sample

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import io.reactivex.rxjava3.disposables.CompositeDisposable

import io.samagra.odk.collect.extension.interactors.FormsDatabaseInteractor
import io.samagra.odk.collect.extension.interactors.FormsInteractor
import io.samagra.odk.collect.extension.interactors.FormsNetworkInteractor
import io.samagra.odk.collect.extension.interactors.ODKInteractor
import io.samagra.odk.collect.extension.listeners.FileDownloadListener
import io.samagra.odk.collect.extension.listeners.ODKProcessListener
import io.samagra.odk.collect.extension.utilities.ODKProvider
import org.apache.commons.io.IOUtils
import org.odk.collect.android.events.FormEventBus
import org.odk.collect.android.events.FormStateEvent
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import timber.log.Timber
import java.io.File

class ODKFeatureTesterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var openFormsInput: EditText
    private lateinit var downloadFormsInput: EditText
    private lateinit var deleteFormsInput: EditText
    private lateinit var openSavedInput: EditText
    private lateinit var prefillFormInput: EditText
    private lateinit var prefillTagInput: EditText
    private lateinit var prefillValueInput: EditText
    private lateinit var openSavedButton: Button
    private lateinit var openFormsButton: Button
    private lateinit var downloadFormsButton: Button
    private lateinit var deleteFormsButton: Button
    private lateinit var downloadAllFormsButton: Button
    private lateinit var clearAllFormsButton: Button
    private lateinit var showAllForms: Button
    private lateinit var prefillButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var odkInteractor: ODKInteractor
    private lateinit var formsInteractor: FormsInteractor
    private lateinit var networkInteractor: FormsNetworkInteractor
    private lateinit var formsDatabaseInteractor: FormsDatabaseInteractor

    private lateinit var context: Context

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_odkfeature_tester)

        context = this
        openFormsInput = findViewById(R.id.open_form_input)
        downloadFormsInput = findViewById(R.id.download_form_input)
        deleteFormsInput = findViewById(R.id.delete_form_input)
        openFormsButton = findViewById(R.id.open_form_button)
        deleteFormsButton = findViewById(R.id.delete_form_button)
        downloadFormsButton = findViewById(R.id.download_form_button)
        downloadAllFormsButton = findViewById(R.id.download_all_forms)
        clearAllFormsButton = findViewById(R.id.clear_all_forms)
        showAllForms = findViewById(R.id.show_all_forms)
        progressBar = findViewById(R.id.form_progress)
        openSavedInput = findViewById(R.id.open_saved_form_input)
        openSavedButton = findViewById(R.id.open_saved_form_button)
        prefillButton = findViewById(R.id.prefill_and_open_form_button)
        prefillFormInput = findViewById(R.id.prefill_and_open_form_input)
        prefillTagInput = findViewById(R.id.prefill_and_open_tag_input)
        prefillValueInput = findViewById(R.id.prefill_and_open_value_input)

        ODKProvider.init(application)
        odkInteractor = ODKProvider.getOdkInteractor()
        progressBar.visibility = View.VISIBLE
        downloadFormsButton.isEnabled=false
        downloadAllFormsButton.isEnabled=false
        clearAllFormsButton.isEnabled=false
        odkInteractor.setupODK(IOUtils.toString(resources.openRawResource(R.raw.settings)), false, object :
            ODKProcessListener {
            override fun onProcessComplete() {
                val currentProjectProvider = DaggerAppDependencyComponent.builder().application(application).build().currentProjectProvider()
                currentProjectProvider.getCurrentProject().name
                formsDatabaseInteractor = ODKProvider.getFormsDatabaseInteractor()
                networkInteractor = ODKProvider.getFormsNetworkInteractor()
                formsInteractor = ODKProvider.getFormsInteractor()
                downloadFormsButton.isEnabled=true
                downloadAllFormsButton.isEnabled=true
                clearAllFormsButton.isEnabled=true
                progressBar.visibility = View.INVISIBLE
            }
            override fun onProcessingError(exception: Exception) {
                exception.printStackTrace()
                progressBar.visibility = View.INVISIBLE
            }
        })

        openFormsButton.setOnClickListener(this)
        downloadFormsButton.setOnClickListener(this)
        deleteFormsButton.setOnClickListener(this)
        downloadAllFormsButton.setOnClickListener(this)
        clearAllFormsButton.setOnClickListener(this)
        showAllForms.setOnClickListener(this)
        openSavedButton.setOnClickListener(this)
        prefillButton.setOnClickListener(this)

        setListeners()
    }

    private fun setListeners() {
        compositeDisposable.add(
            FormEventBus.getState().subscribe { event ->
                when (event) {
                    is FormStateEvent.OnFormDownloadFailed -> Timber.tag("FORM EVENT").d("Download for form %s failed. Reason: %s", event.formId, event.errorMessage)
                    is FormStateEvent.OnFormDownloaded -> Timber.tag("FORM EVENT").d("Form downloaded with id: %s", event.formId)
                    is FormStateEvent.OnFormOpenFailed -> Timber.tag("FORM EVENT").d("Form open failed for form %s. Reason: %s", event.formId, event.errorMessage)
                    is FormStateEvent.OnFormOpened -> Timber.tag("FORM EVENT").d("Form with id: %s was opened", event.formId)
                    is FormStateEvent.OnFormSaveError -> Timber.tag("FORM EVENT").d("Form with id: %s could not be saved. Reason: %s", event.formId, event.errorMessage)
                    is FormStateEvent.OnFormSaved -> Timber.tag("FORM EVENT").d("Form with id: %s was saved. Saved instance path: %s", event.formId, event.instancePath)
                    is FormStateEvent.OnFormUploadFailed -> Timber.tag("FORM EVENT").d("Form upload failed for form id: %s. Reason: %s", event.formId, event.errorMessage)
                    is FormStateEvent.OnFormUploaded -> Timber.tag("FORM EVENT").d("Form with id: %s was uploaded", event.formId)
                }
                progressBar.visibility = View.INVISIBLE
            }
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.open_form_button -> {
                val formId: String = openFormsInput.text.toString().trim()
                if (formId.isNotBlank()) {
                    progressBar.visibility = View.VISIBLE
                    formsInteractor.openForm(formId, context)
                }
            }
            R.id.download_form_button -> {
                val formId: String = downloadFormsInput.text.toString().trim()
                if (formId.isNotBlank()) {
                    progressBar.visibility = View.VISIBLE
                    networkInteractor.downloadFormById(formId, object : FileDownloadListener {
                        override fun onProgress(progress: Int) {
                        }

                        override fun onComplete(downloadedFile: File) {
                            progressBar.visibility = View.INVISIBLE
                            showToast("Download Complete")
                        }

                        override fun onCancelled(exception: Exception) {
                            progressBar.visibility = View.INVISIBLE
                            showToast(exception.message)
                        }

                    })
                }
            }
            R.id.delete_form_button -> {
                val formId: String = deleteFormsInput.text.toString().trim()
                if (formId.isNotBlank()) {
                    progressBar.visibility = View.VISIBLE
                    formsDatabaseInteractor.deleteByFormId(formId)
                    showToast("$formId Form Deleted")
                    progressBar.visibility = View.INVISIBLE
                }
            }
            R.id.download_all_forms -> {
                progressBar.visibility = View.VISIBLE
                networkInteractor.downloadRequiredForms(object: FileDownloadListener {
                    override fun onProgress(progress: Int) {
                    }
                    override fun onComplete(downloadedFile: File) {
                        progressBar.visibility = View.INVISIBLE
                        runOnUiThread{
                            showToast("Downloaded all forms!")
                        }
                    }

                    override fun onCancelled(exception: Exception) {
                        progressBar.visibility = View.INVISIBLE
                        Timber.e(exception)
                        runOnUiThread{ showToast(exception.message) }
                    }

                })
            }
            R.id.prefill_and_open_form_button -> {
                progressBar.visibility = View.VISIBLE
                val formId = prefillFormInput.text.toString().trim()
                val formTag = prefillTagInput.text.toString().trim()
                val formValue = prefillValueInput.text.toString().trim()
                if (formId.isNotBlank() && formTag.isNotBlank() && formValue.isNotBlank()) {
                    formsInteractor.prefillAndOpenForm(
                            formId,
                            hashMapOf(
                                    formTag to formValue,
                            ),
                            context
                    )
                }
            }
            R.id.clear_all_forms -> {
                progressBar.visibility = View.VISIBLE
                formsDatabaseInteractor.clearDatabase()
                showToast("All Forms Cleared!")
                progressBar.visibility = View.INVISIBLE
            }
            R.id.show_all_forms -> {
                startActivity(Intent(this, FormsListActivity::class.java))
            }
            R.id.open_saved_form_button -> {
                val formId: String = openSavedInput.text.toString().trim()
                if (formId.isNotBlank()) {
                    progressBar.visibility = View.VISIBLE
                    formsInteractor.openSavedForm(formId, context)
                }
            }
        }
    }

    fun showToast(text: String?) {
        if (text != null)
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}