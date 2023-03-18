package io.samagra.odk.collect.extension.interactors

import org.odk.collect.forms.Form

/** The FormsDatabaseInteractor interface provides a set of methods to interact with the local forms
 *  database. This interface enables developers to manage the forms that are stored on the device,
 *  including retrieving, adding, and deleting forms.
 *
 *  @author Chinmoy Chakraborty
 */
interface FormsDatabaseInteractor {

    /** Returns a list of all forms that are locally available. */
    fun getLocalForms(): List<Form>

    /** Returns a list of forms, given a formId.
     *  Note, formId here refers to the ref id of the form. */
    fun getFormsByFormId(formId: String): List<Form>

    /** Returns the latest version of a form based on formId. */
    fun getLatestFormById(formId: String): Form?

    /** Returns the latest form, given a formId and version.
     *  Note, formId here refers to the ref id of the form. */
    fun getFormByFormIdAndVersion(formId: String, formVersion: String): Form?

    /** Returns a form, given a md5 hash. */
    fun getFormByMd5Hash(md5Hash: String): Form?

    /** Deletes a form from the database, given an id.
     *  Note, id is not equivalent to ref id of form, it is merely refers to
     *  the primary key of the form in the database. */
    fun deleteForm(id: Long)

    /** Deletes a form from the database, given a formId and formVersion.
     *  Note, formId here refers to the ref id of the form. */
    fun deleteByFormIdAndVersion(formId: String, formVersion: String)

    /** Delete all forms with a given formId.
     *  Note, formId here refers to the ref id of the form. */
    fun deleteByFormId(formId: String)

    /** Deletes all form data from the database. */
    fun clearDatabase()

    /** Add a form to the database, given a form definition. */
    fun addForm(form: Form)
}
