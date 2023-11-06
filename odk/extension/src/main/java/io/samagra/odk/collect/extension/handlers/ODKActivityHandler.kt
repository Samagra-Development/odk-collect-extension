package io.samagra.odk.collect.extension.handlers

import android.content.Context
import android.content.Intent
import io.samagra.odk.collect.extension.interactors.ODKActivityInteractor
import org.odk.collect.android.activities.InstanceChooserList
import org.odk.collect.android.activities.InstanceUploaderListActivity
import org.odk.collect.android.utilities.ApplicationConstants

class ODKActivityHandler : ODKActivityInteractor {

    override fun openDraftFormsList(formsToFilter: List<String>?, ctx: Context) {
        val i = Intent(ctx, InstanceChooserList::class.java)
        i.putExtra(
            ApplicationConstants.BundleKeys.FORM_MODE,
            ApplicationConstants.FormModes.EDIT_SAVED
        )
        if (formsToFilter != null) {
            i.putExtra(ApplicationConstants.BundleKeys.FORM_ID, formsToFilter.toTypedArray())
        }
        ctx.startActivity(i)
    }

    override fun openFinalizedFormsList(ctx: Context) {
        val i = Intent(ctx, InstanceUploaderListActivity::class.java)
        ctx.startActivity(i)
    }
}
