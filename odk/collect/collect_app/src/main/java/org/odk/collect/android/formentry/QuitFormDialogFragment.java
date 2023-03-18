package org.odk.collect.android.formentry;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.odk.collect.analytics.Analytics;
import org.odk.collect.android.R;
import org.odk.collect.android.external.InstancesContract;
import org.odk.collect.android.formentry.saving.FormSaveViewModel;
import org.odk.collect.android.injection.DaggerUtils;
import org.odk.collect.android.projects.CurrentProjectProvider;
import org.odk.collect.android.utilities.InstancesRepositoryProvider;
import org.odk.collect.async.Scheduler;
import org.odk.collect.forms.instances.Instance;
import org.odk.collect.settings.SettingsProvider;
import org.odk.collect.settings.keys.ProtectedProjectKeys;

import javax.inject.Inject;

public class QuitFormDialogFragment extends DialogFragment {

    @Inject
    Analytics analytics;

    @Inject
    Scheduler scheduler;

    @Inject
    SettingsProvider settingsProvider;

    @Inject
    CurrentProjectProvider currentProjectProvider;

    @Inject
    FormEntryViewModel.Factory formEntryViewModelFactory;

    private FormSaveViewModel formSaveViewModel;
    private FormEntryViewModel formEntryViewModel;
    private Listener listener;

    private final FormSaveViewModel.FactoryFactory formSaveViewModelFactoryFactory;

    public QuitFormDialogFragment(FormSaveViewModel.FactoryFactory formSaveViewModelFactoryFactory) {
        this.formSaveViewModelFactoryFactory = formSaveViewModelFactoryFactory;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerUtils.getComponent(context).inject(this);

        ViewModelProvider.Factory factory = formSaveViewModelFactoryFactory.create(requireActivity(), null);
        formSaveViewModel = new ViewModelProvider(requireActivity(), factory).get(FormSaveViewModel.class);
        formEntryViewModel = new ViewModelProvider(requireActivity(), formEntryViewModelFactory).get(FormEntryViewModel.class);

        if (context instanceof Listener) {
            listener = (Listener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        String title = formSaveViewModel.getFormName() == null ? getActivity().getString(R.string.no_form_loaded) : formSaveViewModel.getFormName();

        // WARNING: Custom ODK Changes
        MaterialAlertDialogBuilder formExitDialogBuilder = new MaterialAlertDialogBuilder(getActivity())
                .setTitle(getString(R.string.quit_application, title))
                .setNegativeButton(getActivity().getString(R.string.do_not_exit), (dialog, id) -> {
                    dialog.cancel();
                    dismiss();
                });

        if (settingsProvider.getProtectedSettings().getBoolean(ProtectedProjectKeys.KEY_SAVE_MID)) {
            if (settingsProvider.getProtectedSettings().getBoolean(ProtectedProjectKeys.KEY_SAVE_BY_DEFAULT)) {
                formExitDialogBuilder.setMessage(R.string.confirm_exit_with_save);
            }
            else {
                formExitDialogBuilder.setMessage(R.string.confirm_exit);
                formExitDialogBuilder.setNeutralButton(getActivity().getString(R.string.exit), (dialog, id) -> {
                    formSaveViewModel.ignoreChanges();
                    formEntryViewModel.exit();

                    String action = getActivity().getIntent().getAction();
                    if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_EDIT.equals(action)) {
                        // caller is waiting on a picked form
                        Uri uri = null;
                        String path = formSaveViewModel.getAbsoluteInstancePath();
                        if (path != null) {
                            Instance instance = new InstancesRepositoryProvider(requireContext()).get().getOneByPath(path);
                            if (instance != null) {
                                uri = InstancesContract.getUri(currentProjectProvider.getCurrentProject().getUuid(), instance.getDbId());
                            }
                        }
                        if (uri != null) {
                            getActivity().setResult(RESULT_OK, new Intent().setData(uri));
                        }
                    }
                    getActivity().finish();
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                });
            }
            formExitDialogBuilder.setPositiveButton(getActivity().getString(R.string.keep_changes), (dialog, id) -> {
                if (listener != null) {
                    listener.onSaveChangesClicked();
                }
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            });
        }
        else {
            formExitDialogBuilder.setMessage(R.string.confirm_exit_without_save);
            formExitDialogBuilder.setPositiveButton(getActivity().getString(R.string.exit), (dialog, id) -> {
                formSaveViewModel.ignoreChanges();
                formEntryViewModel.exit();

                String action = getActivity().getIntent().getAction();
                if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_EDIT.equals(action)) {
                    // caller is waiting on a picked form
                    Uri uri = null;
                    String path = formSaveViewModel.getAbsoluteInstancePath();
                    if (path != null) {
                        Instance instance = new InstancesRepositoryProvider(requireContext()).get().getOneByPath(path);
                        if (instance != null) {
                            uri = InstancesContract.getUri(currentProjectProvider.getCurrentProject().getUuid(), instance.getDbId());
                        }
                    }
                    if (uri != null) {
                        getActivity().setResult(RESULT_OK, new Intent().setData(uri));
                    }
                }
                getActivity().finish();
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            });
        }
        return formExitDialogBuilder.create();
    }

    public interface Listener {
        void onSaveChangesClicked();
    }
}
