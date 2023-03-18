package io.samagra.odk.collect.extension.handlers

import android.content.Context
import android.content.Intent
import io.samagra.odk.collect.extension.interactors.FormInstanceInteractor
import org.odk.collect.android.activities.FormEntryActivity
import org.odk.collect.android.activities.FormHierarchyActivity
import org.odk.collect.android.events.FormEventBus
import org.odk.collect.android.external.InstancesContract
import org.odk.collect.android.projects.CurrentProjectProvider
import org.odk.collect.android.utilities.ApplicationConstants
import org.odk.collect.forms.instances.Instance
import org.odk.collect.forms.instances.InstancesRepository

class FormInstanceHandler(
    private val instancesRepository: InstancesRepository,
    private val currentProjectProvider: CurrentProjectProvider
): FormInstanceInteractor {
    override fun getInstanceWithId(instanceId: Long): Instance? {
        return instancesRepository.get(instanceId)
    }

    override fun deleteInstanceWithId(instanceId: Long) {
        instancesRepository.delete(instanceId)
    }

    override fun openInstance(instance: Instance, context: Context) {
        val instanceUri = InstancesContract.getUri(currentProjectProvider.getCurrentProject().uuid, instance.dbId)
        val intent = Intent(context, FormEntryActivity::class.java)
        intent.action = Intent.ACTION_EDIT
        intent.data = instanceUri
        intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED)
        intent.putExtra(FormHierarchyActivity.EXTRA_JUMP_TO_BEGINNING, true)
        context.startActivity(intent)
    }

    override fun openLatestSavedInstanceWithFormId(formId: String, context: Context) {
        val instances = instancesRepository.getAllByFormId(formId).filter { instance -> instance.status == Instance.STATUS_INCOMPLETE }
        if (instances.isNotEmpty()) {
            val latestInstance = instances.sortedByDescending { it.lastStatusChangeDate }[0]
            val instanceUri = InstancesContract.getUri(currentProjectProvider.getCurrentProject().uuid, latestInstance.dbId)
            val intent = Intent(context, FormEntryActivity::class.java)
            intent.action = Intent.ACTION_EDIT
            intent.data = instanceUri
            intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED)
            intent.putExtra(FormHierarchyActivity.EXTRA_JUMP_TO_BEGINNING, true)
            context.startActivity(intent)
        }
        else {
            FormEventBus.formOpenFailed(formId, "No saved form with this id exists")
        }
    }

    override fun getInstancesWithFormId(formId: String): List<Instance> {
        return instancesRepository.getAllByFormId(formId)
    }

    override fun getInstancesWithStatus(status: String): List<Instance> {
        return instancesRepository.getAllByStatus(status)
    }

    override fun getInstanceByPath(instancePath: String): Instance? {
        return instancesRepository.getOneByPath(instancePath)
    }

}
