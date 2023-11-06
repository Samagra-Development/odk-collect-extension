package org.odk.collect.android.dao;

import android.net.Uri;

import androidx.loader.content.CursorLoader;

import org.jetbrains.annotations.NotNull;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.database.instances.DatabaseInstanceColumns;
import org.odk.collect.android.external.InstancesContract;
import org.odk.collect.android.projects.CurrentProjectProvider;
import org.odk.collect.forms.instances.Instance;

@Deprecated
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

    public CursorLoader createEditableInstancesCursorLoader(CharSequence charSequence, String sortOrder, @NotNull String[] formIds) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            String selection = DatabaseInstanceColumns.STATUS + " =? ";
            String[] selectionArgs = new String[formIds.length + 1];
            selectionArgs[0] = Instance.STATUS_INCOMPLETE;
            if (formIds.length > 0) {
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < formIds.length; i++) {
                    placeholders.append("?,");
                    selectionArgs[i + 1] = formIds[i];
                }
                placeholders.deleteCharAt(placeholders.length() - 1);
                selection = selection + "and " + DatabaseInstanceColumns.JR_FORM_ID + " IN (" + placeholders + ") ";
            }
            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        } else {

            String selection = DatabaseInstanceColumns.STATUS + " =? " +
                    "and " + DatabaseInstanceColumns.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = new String[formIds.length + 2];
            selectionArgs[0] =
                    Instance.STATUS_INCOMPLETE;
            selectionArgs[1] = "%" + charSequence + "%";
            if (formIds.length > 0) {
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < formIds.length; i++) {
                    placeholders.append("?,");
                    selectionArgs[i + 2] = formIds[i];
                }
                placeholders.deleteCharAt(placeholders.length() - 1);
                selection = selection + " and " + DatabaseInstanceColumns.JR_FORM_ID + " IN (" + placeholders + ") ";
            }
            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader createSavedInstancesCursorLoader(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            String selection = DatabaseInstanceColumns.DELETED_DATE + " IS NULL ";
            cursorLoader = getInstancesCursorLoader(selection, null, sortOrder);
        } else {
            String selection =
                    DatabaseInstanceColumns.DELETED_DATE + " IS NULL and "
                            + DatabaseInstanceColumns.DISPLAY_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + charSequence + "%"};
            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader createFinalizedInstancesCursorLoader(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            String selection = DatabaseInstanceColumns.STATUS + "=? or " + DatabaseInstanceColumns.STATUS + "=?";
            String[] selectionArgs = {Instance.STATUS_COMPLETE, Instance.STATUS_SUBMISSION_FAILED};

            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
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

    public CursorLoader createCompletedUndeletedInstancesCursorLoader(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;
        if (charSequence.length() == 0) {
            String selection = DatabaseInstanceColumns.DELETED_DATE + " IS NULL and ("
                    + DatabaseInstanceColumns.STATUS + "=? or "
                    + DatabaseInstanceColumns.STATUS + "=? or "
                    + DatabaseInstanceColumns.STATUS + "=?)";

            String[] selectionArgs = {Instance.STATUS_COMPLETE,
                    Instance.STATUS_SUBMISSION_FAILED,
                    Instance.STATUS_SUBMITTED};

            cursorLoader = getInstancesCursorLoader(selection, selectionArgs, sortOrder);
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
