package org.odk.collect.android.instancemanagement.autosend

import android.content.Context
import org.odk.collect.analytics.Analytics
import org.odk.collect.android.R
import org.odk.collect.android.events.FormEventBus
import org.odk.collect.android.formmanagement.InstancesAppState
import org.odk.collect.android.gdrive.GoogleAccountsManager
import org.odk.collect.android.gdrive.GoogleApiProvider
import org.odk.collect.android.instancemanagement.InstanceSubmitter
import org.odk.collect.android.instancemanagement.SubmitException
import org.odk.collect.android.notifications.Notifier
import org.odk.collect.android.projects.ProjectDependencyProvider
import org.odk.collect.android.upload.FormUploadException
import org.odk.collect.forms.instances.Instance
import org.odk.collect.permissions.PermissionsProvider
import org.odk.collect.utilities.XmlJsonUtility
import java.io.File

class InstanceAutoSender(
    private val instanceAutoSendFetcher: InstanceAutoSendFetcher,
    private val context: Context,
    private val notifier: Notifier,
    private val analytics: Analytics,
    private val googleAccountsManager: GoogleAccountsManager,
    private val googleApiProvider: GoogleApiProvider,
    private val permissionsProvider: PermissionsProvider,
    private val instancesAppState: InstancesAppState
) {
    fun autoSendInstances(projectDependencyProvider: ProjectDependencyProvider): Boolean {
        val instanceSubmitter = InstanceSubmitter(
            analytics,
            projectDependencyProvider.formsRepository,
            googleAccountsManager,
            googleApiProvider,
            permissionsProvider,
            projectDependencyProvider.generalSettings
        )
        return projectDependencyProvider.changeLockProvider.getInstanceLock(projectDependencyProvider.projectId).withLock { acquiredLock: Boolean ->
            if (acquiredLock) {
                val toUpload = instanceAutoSendFetcher.getInstancesToAutoSend(
                    projectDependencyProvider.projectId,
                    projectDependencyProvider.instancesRepository,
                    projectDependencyProvider.formsRepository
                )

                try {
                    val result: Map<Instance, FormUploadException?> = instanceSubmitter.submitInstances(toUpload)
                    result.entries.stream().forEach { entry ->
                        if (entry.value == null) {
                            try {
                                val submittedData = XmlJsonUtility.convertToJson(File(entry.key.instanceFilePath))
                                FormEventBus.formUploaded(entry.key.formId, submittedData)
                            } catch (e: Exception) {
                                FormEventBus.formUploadError(entry.key.formId, e.message ?: "Failed to read submitted data!")
                            }
                        }
                        else {
                            FormEventBus.formUploadError(entry.key.formId, entry.value!!.message)
                        }
                    }
                    notifier.onSubmission(result, projectDependencyProvider.projectId)
                } catch (e: SubmitException) {
                    when (e.type) {
                        SubmitException.Type.GOOGLE_ACCOUNT_NOT_SET -> {
                            val result: Map<Instance, FormUploadException?> = toUpload.associateWith {
                                FormUploadException(context.getString(R.string.google_set_account))
                            }
                            notifier.onSubmission(result, projectDependencyProvider.projectId)
                        }
                        SubmitException.Type.GOOGLE_ACCOUNT_NOT_PERMITTED -> {
                            val result: Map<Instance, FormUploadException?> = toUpload.associateWith {
                                FormUploadException(context.getString(R.string.odk_permissions_fail))
                            }
                            notifier.onSubmission(result, projectDependencyProvider.projectId)
                        }
                        SubmitException.Type.NOTHING_TO_SUBMIT -> {
                            // do nothing
                        }
                    }
                }
                instancesAppState.update()
                true
            } else {
                false
            }
        }
    }
}
