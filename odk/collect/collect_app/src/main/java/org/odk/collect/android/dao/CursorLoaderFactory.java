package org.odk.collect.android.dao;

import android.net.Uri;
import android.text.TextUtils;

import androidx.loader.content.CursorLoader;

import org.jetbrains.annotations.NotNull;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.forms.DatabaseFormColumns;
import org.odk.collect.android.database.instances.DatabaseInstanceColumns;
import org.odk.collect.android.external.FormsContract;
import org.odk.collect.android.external.InstancesContract;
import org.odk.collect.android.projects.CurrentProjectProvider;
import org.odk.collect.forms.instances.Instance;

public class CursorLoaderFactory {

    public static final String INTERNAL_QUERY_PARAM = "internal";
    private final CurrentProjectProvider currentProjectProvider;

    public CursorLoaderFactory(CurrentProjectProvider currentProjectProvider) {
        this.currentProjectProvider = currentProjectProvider;
    }

    public CursorLoader createSentInstancesCursorLoader(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            String selection = DatabaseInstanceColumns.STATUS + "=? or " + DatabaseInstanceColumns.STATUS + "=?";
            String[] selectionArgs = {Instance.STATUS_SUBMITTED, Instance.STATUS_SUBMISSION_FAILED};

            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        } else {
            String selection =
                    "(" + DatabaseInstanceColumns.STATUS + "=? or "
                            + DatabaseInstanceColumns.STATUS + "=?) and "
                            + DatabaseInstanceColumns.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = {
                    Instance.STATUS_SUBMITTED,
                    Instance.STATUS_SUBMISSION_FAILED,
                    "%" + charSequence + "%"};

            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader createEditableInstancesCursorLoader(String sortOrder) {
        String selection = DatabaseInstanceColumns.STATUS + " !=? " +
                "and " + DatabaseInstanceColumns.STATUS + " !=? ";
        String[] selectionArgs = {
                Instance.STATUS_SUBMITTED,
                Instance.STATUS_SUBMISSION_FAILED
        };

        return getInstancesCursorLoader(selection, selectionArgs, sortOrder);
    }

    // WARNING: Custom ODK changes
    public CursorLoader createEditableInstancesCursorLoader(String sortOrder, @NotNull String[] formIds) {
        String[] selectionArgs = new String[formIds.length + 2];
        selectionArgs[0] = Instance.STATUS_SUBMITTED;
        selectionArgs[1] = Instance.STATUS_SUBMISSION_FAILED;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < formIds.length; i++) {
            placeholders.append("?,");
            selectionArgs[i + 2] = formIds[i];
        }
        placeholders.deleteCharAt(placeholders.length() - 1);
        String selection = DatabaseInstanceColumns.STATUS + " !=? " +
                "and " + DatabaseInstanceColumns.STATUS + " !=? " +
                "and " + DatabaseInstanceColumns.JR_FORM_ID + " IN (" + placeholders + ") ";

        return getInstancesCursorLoader(selection, selectionArgs, sortOrder);
    }

    public CursorLoader createEditableInstancesCursorLoader(CharSequence charSequence, String sortOrder, String[] formIds) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            cursorLoader = formIds != null ? createEditableInstancesCursorLoader(sortOrder, formIds) : createEditableInstancesCursorLoader(sortOrder);
        } else {
            String selection = DatabaseInstanceColumns.STATUS + " !=? " +
                    "and " + DatabaseInstanceColumns.STATUS + " !=? " +
                    "and " + DatabaseInstanceColumns.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = {
                    Instance.STATUS_SUBMITTED,
                    Instance.STATUS_SUBMISSION_FAILED,
                    "%" + charSequence + "%"};
            if (formIds != null) {
                StringBuilder placeholders = new StringBuilder();
                selectionArgs = new String[formIds.length + 3];
                selectionArgs[0] = Instance.STATUS_SUBMITTED;
                selectionArgs[1] = Instance.STATUS_SUBMISSION_FAILED;
                selectionArgs[2] = "%" + charSequence + "%";
                for (int i = 0; i < formIds.length; i++) {
                    placeholders.append("?,");
                    selectionArgs[i + 3] = formIds[i];
                }
                placeholders.deleteCharAt(placeholders.length() - 1);
                selection = selection + " and " + DatabaseInstanceColumns.JR_FORM_ID + " IN (" + placeholders + ")";
            }
            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader createSavedInstancesCursorLoader(String sortOrder) {
        String selection = DatabaseInstanceColumns.DELETED_DATE + " IS NULL ";

        return getInstancesCursorLoader(selection, null, sortOrder);
    }

    public CursorLoader createSavedInstancesCursorLoader(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            cursorLoader = createSavedInstancesCursorLoader(sortOrder);
        } else {
            String selection =
                    DatabaseInstanceColumns.DELETED_DATE + " IS NULL and "
                            + DatabaseInstanceColumns.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + charSequence + "%"};
            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader createFinalizedInstancesCursorLoader(String sortOrder) {
        String selection = DatabaseInstanceColumns.STATUS + "=? or " + DatabaseInstanceColumns.STATUS + "=?";
        String[] selectionArgs = {Instance.STATUS_COMPLETE, Instance.STATUS_SUBMISSION_FAILED};

        return getInstancesCursorLoader(selection, selectionArgs, sortOrder);
    }

    public CursorLoader createFinalizedInstancesCursorLoader(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            cursorLoader = createFinalizedInstancesCursorLoader(sortOrder);
        } else {
            String selection =
                    "(" + DatabaseInstanceColumns.STATUS + "=? or "
                            + DatabaseInstanceColumns.STATUS + "=?) and "
                            + DatabaseInstanceColumns.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = {
                    Instance.STATUS_COMPLETE,
                    Instance.STATUS_SUBMISSION_FAILED,
                    "%" + charSequence + "%"};

            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader createCompletedUndeletedInstancesCursorLoader(String sortOrder) {
        String selection = DatabaseInstanceColumns.DELETED_DATE + " IS NULL and ("
                + DatabaseInstanceColumns.STATUS + "=? or "
                + DatabaseInstanceColumns.STATUS + "=? or "
                + DatabaseInstanceColumns.STATUS + "=?)";

        String[] selectionArgs = {Instance.STATUS_COMPLETE,
                Instance.STATUS_SUBMISSION_FAILED,
                Instance.STATUS_SUBMITTED};

        return getInstancesCursorLoader(selection, selectionArgs, sortOrder);
    }

    public CursorLoader createCompletedUndeletedInstancesCursorLoader(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            cursorLoader = createCompletedUndeletedInstancesCursorLoader(sortOrder);
        } else {
            String selection = DatabaseInstanceColumns.DELETED_DATE + " IS NULL and ("
                    + DatabaseInstanceColumns.STATUS + "=? or "
                    + DatabaseInstanceColumns.STATUS + "=? or "
                    + DatabaseInstanceColumns.STATUS + "=?) and "
                    + DatabaseInstanceColumns.DISPLAY_NAME + " LIKE ?";

            String[] selectionArgs = {
                    Instance.STATUS_COMPLETE,
                    Instance.STATUS_SUBMISSION_FAILED,
                    Instance.STATUS_SUBMITTED,
                    "%" + charSequence + "%"};

            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        }
        return cursorLoader;
    }

    /**
     * Returns a loader filtered by the specified charSequence in the specified sortOrder. If
     * newestByFormId is true, only the most recently-downloaded version of each form is included.
     */
    public CursorLoader getFormsCursorLoader(CharSequence charSequence, String sortOrder, boolean newestByFormId) {
        CursorLoader cursorLoader;

        if (charSequence.length() == 0) {
            Uri formUri = newestByFormId ?
                    FormsContract.getContentNewestFormsByFormIdUri(currentProjectProvider.getCurrentProject().getUuid()) :
                    FormsContract.getUri(currentProjectProvider.getCurrentProject().getUuid());
            cursorLoader = new CursorLoader(Collect.getInstance(), getUriWithAnalyticsParam(formUri), null, DatabaseFormColumns.DELETED_DATE + " IS NULL", new String[]{}, sortOrder);
        } else {
            String selection = DatabaseFormColumns.DISPLAY_NAME + " LIKE ? AND " + DatabaseFormColumns.DELETED_DATE + " IS NULL";
            String[] selectionArgs = {"%" + charSequence + "%"};

            Uri formUri = newestByFormId ?
                    FormsContract.getContentNewestFormsByFormIdUri(currentProjectProvider.getCurrentProject().getUuid()) :
                    FormsContract.getUri(currentProjectProvider.getCurrentProject().getUuid());
            cursorLoader = new CursorLoader(Collect.getInstance(), getUriWithAnalyticsParam(formUri), null, selection, selectionArgs, sortOrder);
        }
        return cursorLoader;
    }

    private CursorLoader getInstancesCursorLoader(String selection, String[] selectionArgs, String sortOrder) {
        Uri uri = InstancesContract.getUri(currentProjectProvider.getCurrentProject().getUuid());

        return new CursorLoader(
                Collect.getInstance(),
                getUriWithAnalyticsParam(uri),
                null,
                selection,
                selectionArgs,
                sortOrder);
    }

    private Uri getUriWithAnalyticsParam(Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter(INTERNAL_QUERY_PARAM, "true")
                .build();
    }
}
