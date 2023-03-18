package org.odk.collect.android.events

/**
 * Events exporter class for form events. This class contains all the
 * events that occur during the lifecycle of a form.
 */
object FormEventBus: ODKEventBus<FormStateEvent>() {
    //TODO: make these functions internal

     fun formOpened(formId: String) {
        state.onNext(FormStateEvent.OnFormOpened(formId))
    }

    fun formOpenFailed(formId: String, errorMessage: String) {
        state.onNext(FormStateEvent.OnFormOpenFailed(formId, errorMessage))
    }

     fun formSaved(formId: String, instancePath: String) {
        state.onNext(FormStateEvent.OnFormSaved(formId, instancePath))
    }

     fun formSaveError(formId: String, errorMessage: String) {
        state.onNext(FormStateEvent.OnFormSaveError(formId, errorMessage))
    }

     fun formUploaded(formId: String, instancePath: String) {
        state.onNext(FormStateEvent.OnFormUploaded(formId, instancePath))
    }

     fun formUploadError(formId: String, errorMessage: String) {
        state.onNext(FormStateEvent.OnFormUploadFailed(formId, errorMessage))
    }

     fun formDownloaded(formId: String) {
        state.onNext(FormStateEvent.OnFormDownloaded(formId))
    }

     fun formDownloadFailed(formId: String, errorMessage: String) {
        state.onNext(FormStateEvent.OnFormDownloadFailed(formId, errorMessage))
    }
}

sealed class FormStateEvent {
    /** Called when a form is opened. */
    data class OnFormOpened(val formId: String): FormStateEvent()

    /** Called when an error occurs while opening a form. */
    data class OnFormOpenFailed(val formId: String, val errorMessage: String): FormStateEvent()

    /** Called when a form is saved. */
    data class OnFormSaved(val formId: String, val instancePath: String): FormStateEvent()

    /** Called when a form save process errors out. */
    data class OnFormSaveError(val formId: String, val errorMessage: String): FormStateEvent()

    /** Called when a form upload is successful. */
    data class OnFormUploaded(val formId: String, val instancePath: String): FormStateEvent()

    /** Called when a form upload fails. */
    data class OnFormUploadFailed(val formId: String, val errorMessage: String): FormStateEvent()

    /** Called when a form is successfully downloaded. */
    data class OnFormDownloaded(val formId: String): FormStateEvent()

    /** Called when a form download fails. */
    data class OnFormDownloadFailed(val formId: String, val errorMessage: String): FormStateEvent()
}