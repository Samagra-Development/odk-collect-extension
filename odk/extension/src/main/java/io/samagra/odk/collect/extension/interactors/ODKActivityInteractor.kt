package io.samagra.odk.collect.extension.interactors

import android.content.Context

/**
 * An interface for interacting with pre-built screens of ODK Collect.
 * Provides methods to open various screens in ODK Collect.
 * @author Hema Sharma
 */
interface ODKActivityInteractor {

    /**
     * Opens the draft form list screen in ODK Collect.
     * @param formsToFilter A list of form names to filter the draft forms. Can be null to show all draft forms.
     * @param ctx The context of the calling activity.
     **/
    fun openDraftFormsList(formsToFilter: List<String>?, ctx: Context)

    /**
     * Opens the list of un-submitted forms screen in ODK Collect.
     * @param ctx The context of the calling activity.
     */
    fun openFinalizedFormsList(ctx: Context)
}
