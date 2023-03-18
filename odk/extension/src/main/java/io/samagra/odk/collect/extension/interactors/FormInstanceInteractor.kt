package io.samagra.odk.collect.extension.interactors

import android.content.Context
import org.odk.collect.forms.instances.Instance

/**
 * An interface for interacting with ODK Collect form instances.
 *
 * @author Chinmoy Chakraborty
 */
interface FormInstanceInteractor {

    /**
     * Retrieve an ODK Collect form instance by its ID.
     * Parameters:
     * instanceId (Long): The ID of the form instance to retrieve.
     * Returns:
     * An Instance object representing the form instance if it exists, null otherwise.
     */
    fun getInstanceWithId(instanceId: Long): Instance?

    /**
     * Delete an ODK Collect form instance by its ID.
     * Parameters:
     * instanceId (Long): The ID of the form instance to delete.
     */
    fun deleteInstanceWithId(instanceId: Long)

    /**
     * Open an ODK Collect form instance in ODK Collect.
     * Parameters:
     * instance (Instance): The form instance to open.
     * context (Context): The Context object of the current Activity or Application.
     */
    fun openInstance(instance: Instance, context: Context)

    /**
     * Open the latest ODK Collect form instance with a given form ID in ODK Collect.
     * Parameters:
     * formId (String): The ID of the form.
     * context (Context): The Context object of the current Activity or Application.
     */
    fun openLatestSavedInstanceWithFormId(formId: String, context: Context)

    /**
     * Retrieve all ODK Collect form instances with a given form ID.
     * Parameters:
     * formId (String): The ID of the form.
     * Returns:
     * A list of Instance objects representing all form instances with the given form ID.
     */
    fun getInstancesWithFormId(formId: String): List<Instance>

    /**
     * Retrieve all ODK Collect form instances with a given status.
     * Parameters:
     * status (String): The status of the form instance, e.g. "incomplete", "complete", etc.
     * Returns:
     * A list of Instance objects representing all form instances with the given status.
     */
    fun getInstancesWithStatus(status: String): List<Instance>

    /**
     * Retrieve an ODK Collect form instance by its path.
     * Parameters:
     * instancePath (String): The path of the form instance to retrieve.
     * Returns:
     * An Instance object representing the form instance if it exists, null otherwise.
     */
    fun getInstanceByPath(instancePath: String): Instance?
}
