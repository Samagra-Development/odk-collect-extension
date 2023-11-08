package org.odk.collect.android.formentry

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.odk.collect.android.R
import org.odk.collect.android.databinding.QuitFormDialogLayoutBinding
import org.odk.collect.android.events.FormEventBus
import org.odk.collect.android.formentry.saving.FormSaveViewModel
import org.odk.collect.settings.SettingsProvider
import org.odk.collect.settings.keys.ProjectKeys
import org.odk.collect.settings.keys.ProtectedProjectKeys

object QuitFormDialog {

    @JvmStatic
    fun show(
        activity: Activity,
        formSaveViewModel: FormSaveViewModel,
        formEntryViewModel: FormEntryViewModel,
        settingsProvider: SettingsProvider,
        formId: String,
        onSaveChangesClicked: Runnable?
    ): AlertDialog {
        return create(
            activity,
            formSaveViewModel,
            formEntryViewModel,
            settingsProvider,
            formId,
            onSaveChangesClicked
        ).also {
            it.show()
        }
    }

    private fun create(
        activity: Activity,
        formSaveViewModel: FormSaveViewModel,
        formEntryViewModel: FormEntryViewModel,
        settingsProvider: SettingsProvider,
        formId: String,
        onSaveChangesClicked: Runnable?
    ): AlertDialog {
        val saveAsDraft = settingsProvider.getProtectedSettings()
            .getBoolean(ProtectedProjectKeys.KEY_SAVE_MID)
        val saveByDefault = if (saveAsDraft) settingsProvider.getProtectedSettings()
            .getBoolean(ProtectedProjectKeys.KEY_SAVE_BY_DEFAULT) else false

        val binding = QuitFormDialogLayoutBinding.inflate(activity.layoutInflater)
        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(
                settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_TITLE) ?:
                formSaveViewModel.formName ?: "unnamed form"
            )
            .setView(binding.root)
            .create()

        if (!settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_EXPLANATION).isNullOrBlank()) {
            binding.saveExplanation.text = settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_EXPLANATION)
        }
        else {
            binding.saveExplanation.text = if (!saveAsDraft) {
                activity.getString(R.string.confirm_exit_without_save)
            } else {
                if (saveByDefault) {
                    activity.getString(R.string.confirm_exit_with_save)
                }
                else {
                    activity.getString(R.string.confirm_exit)
                }
            }
        }

        binding.discardChanges.isVisible = !saveByDefault

        binding.discardChanges.setOnClickListener {
            formSaveViewModel.ignoreChanges()
            formEntryViewModel.exit()
            activity.finish()
            dialog.dismiss()
            FormEventBus.formAbandoned(formId)
        }

        if (!settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_DISCARD_TEXT).isNullOrEmpty()) {
            binding.discardChanges.text = settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_DISCARD_TEXT)
        }

        binding.keepEditingOutlined.isVisible = false
        binding.keepEditingFilled.isVisible = true

        binding.keepEditingOutlined.setOnClickListener {
            dialog.dismiss()
        }

        binding.keepEditingFilled.setOnClickListener {
            dialog.dismiss()
        }

        if (!settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_KEEP_EDITING_TEXT).isNullOrBlank()) {
            binding.keepEditingFilled.text = settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_KEEP_EDITING_TEXT)
        }

        binding.saveChanges.isVisible = saveAsDraft
        binding.saveChanges.setOnClickListener {
            onSaveChangesClicked?.run()
        }

        if (!settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_SAVE_AS_DRAFT_TEXT).isNullOrBlank()) {
            binding.saveChanges.text = settingsProvider.getUnprotectedSettings().getString(ProjectKeys.QUIT_DIALOG_SAVE_AS_DRAFT_TEXT)
        }

        binding.saveAndExit.isVisible = saveByDefault

        binding.saveAndExit.setOnClickListener {
            onSaveChangesClicked?.run()
        }

        return dialog
    }
}
