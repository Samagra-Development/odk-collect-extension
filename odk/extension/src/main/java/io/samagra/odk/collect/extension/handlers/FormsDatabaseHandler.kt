package io.samagra.odk.collect.extension.handlers

import io.samagra.odk.collect.extension.AppConstants
import io.samagra.odk.collect.extension.interactors.FormsDatabaseInteractor
import io.samagra.odk.collect.extension.interactors.StorageInteractor
import org.odk.collect.forms.Form
import org.odk.collect.forms.FormsRepository
import javax.inject.Inject

class FormsDatabaseHandler @Inject constructor(
        private val formsRepository: FormsRepository,
        private val storageInteractor: StorageInteractor
    ): FormsDatabaseInteractor {

    override fun getLocalForms(): List<Form> {
        return formsRepository.all
    }

    override fun getFormsByFormId(formId: String): List<Form> {
        return formsRepository.getAllByFormId(formId)
    }

    override fun getLatestFormById(formId: String): Form? {
        val formsList = formsRepository.getAllByFormId(formId)
        formsList.sortWith { form1, form2 ->
            if (form1.version == null) 1
            else if (form2.version == null) -1
            else form1.version!!.compareTo(form2.version!!)
        }
        return if (formsList.isEmpty()) null else formsList[0]
    }

    override fun getFormByFormIdAndVersion(formId: String, formVersion: String): Form? {
        return formsRepository.getLatestByFormIdAndVersion(formId, formVersion)
    }

    override fun getFormByMd5Hash(md5Hash: String): Form? {
        return formsRepository.getOneByMd5Hash(md5Hash)
    }

    override fun deleteForm(id: Long) {
        formsRepository.delete(id)
    }

    override fun deleteByFormIdAndVersion(formId: String, formVersion: String) {
        val form = formsRepository.getLatestByFormIdAndVersion(formId, formVersion)
        formsRepository.delete(form?.dbId)
    }

    override fun deleteByFormId(formId: String) {
        val formsList = formsRepository.getAllByFormId(formId)
        formsList.forEach{formsRepository.delete(it.dbId)}
    }

    override fun clearDatabase() {
        storageInteractor.clearPreference(AppConstants.ZIP_HASH_KEY)
        formsRepository.deleteAll()
    }

    override fun addForm(form: Form) {
        formsRepository.save(form)
    }
}