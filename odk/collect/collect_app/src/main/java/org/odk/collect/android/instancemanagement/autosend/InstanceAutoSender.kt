package org.odk.collect.android.instancemanagement.autosend

import android.content.Context
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
import org.odk.collect.metadata.PropertyManager
import org.odk.collect.permissions.PermissionsProvider
import org.odk.collect.settings.keys.ProjectKeys

class InstanceAutoSender(
    private val instanceAutoSendFetcher: InstanceAutoSendFetcher,
    private val context: Context,
    private val notifier: Notifier,
    private val googleAccountsManager: GoogleAccountsManager,
    private val googleApiProvider: GoogleApiProvider,
    private val permissionsProvider: PermissionsProvider,
    private val instancesAppState: InstancesAppState,
    private val propertyManager: PropertyManager
) {
    fun autoSendInstances(projectDependencyProvider: ProjectDependencyProvider): Boolean {
        val instanceSubmitter = InstanceSubmitter(
            projectDependencyProvider.formsRepository,
            googleAccountsManager,
            googleApiProvider,
            permissionsProvider,
            projectDependencyProvider.generalSettings,
            propertyManager
        )
        return projectDependencyProvider.changeLockProvider.getInstanceLock(projectDependencyProvider.projectId).withLock { acquiredLock: Boolean ->
            if (acquiredLock) {
                val toUpload = instanceAutoSendFetcher.getInstancesToAutoSend(
                    projectDependencyProvider.projectId,
                    projectDependencyProvider.instancesRepository,
                    projectDependencyProvider.formsRepository
                )

                try {
                    if (projectDependencyProvider.generalSettings.getBoolean(ProjectKeys.KEY_SERVER_SUBMISSION_IS_ENABLED)) {
                        val result: Map<Instance, FormUploadException?> = instanceSubmitter.submitInstances(toUpload)
                        result.entries.stream().forEach { entry ->
                            if (entry.value == null) {
                                FormEventBus.formUploaded(entry.key.formId, entry.key.instanceFilePath)
                            }
                            else {
                                FormEventBus.formUploadError(entry.key.formId, entry.value!!.message)
                            }
                        }
                        notifier.onSubmission(result, projectDependencyProvider.projectId)
                    }
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
