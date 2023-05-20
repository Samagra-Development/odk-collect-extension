package io.samagra.odk.collect.extension.interactors

import android.content.Context
import io.samagra.odk.collect.extension.listeners.FormsProcessListener
import org.odk.collect.forms.Form

/** FormsInteractor Interface provides methods to interact with ODK forms. Developers can use this
 * interface to open a form with a specific form ID or MD5 hash and also pre-fill form values.
 *
 * @author Chinmoy Chakraborty
 */
interface FormsInteractor {

    /** Opens the latest version related to the formId. */
    fun openFormWithFormId(formId: String, context: Context)

    /** Opens a form with the given md5 hash. */
    fun openFormWithMd5Hash(md5Hash: String, context: Context)

    /**
     * This function is used to prefill a single form field.
     * This function takes three parameters: the unique identifier of the ODK form,
     * the tag or name of the form field, and the value to be pre-filled in the form field.
     * Note: This creates a separate form instance of the original form and does not alter
     * the original form in any way.
     */
    fun prefillForm(formId: String, tag: String, value: String)

    /**
     * This function is used to prefill multiple form fields.
     * This function takes two parameters: the unique identifier of the ODK form
     * and a map of tag-value pairs.
     * Note: This creates a separate form instance of the original form and does not alter
     * the original form in any way.
     */
    fun prefillForm(formId: String, tagValueMap: HashMap<String, String>)

    /**
     * Prefills the values of a form given a tag and value. The formPath may be the
     * path of a form itself or an instance of a form.
     * Note: This modifies the original form described by the form path.
     */
    fun updateForm(formPath: String, tag: String, tagValue: String, listener: FormsProcessListener?)

    /**
     * Prefills the values of a form given a list of tags and values. The formPath may be the
     * path of a form itself or an instance of a form.
     * Note: This modifies the original form described by the form path.
     */
    fun updateForm(formPath: String, values: HashMap<String, String>,listener: FormsProcessListener?)

    /** Opens the latest version related to the formId. Deletes any
     *  saved instance of a form with this particular formId. */
    fun openForm(formId: String, context: Context)

    /** Opens a saved form. If no saved instance is found, opens a new form. */
    fun openSavedForm(formId: String, context: Context)

    /** This method pre-fills a form with data from a given map of key-value pairs,
     * and then opens the form in a given Android context. */
    fun prefillAndOpenForm(formId: String, tagValueMap: HashMap<String, String>, context: Context)
}
