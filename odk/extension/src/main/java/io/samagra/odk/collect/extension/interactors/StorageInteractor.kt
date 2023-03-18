package io.samagra.odk.collect.extension.interactors

import java.io.File

/** An interface for basic storage utility tasks. */
interface StorageInteractor {

    /** Sets a key->value preference in the default shared preference file.
     *  Replaces a given key if it already exists. */
    fun setPreference(key: String, value: String)

    /** Gets a preference from default shared preference file given a key.
     *  Returns null, if key is not present. */
    fun getPreference(key: String): String?

    /** Clears a preference from default shared preferences, given a key. */
    fun clearPreference(key: String)

    /** Clears all preferences from default shared preferences. */
    fun clearPreferences()

    /** Creates a temporary file. Use deleteOnExit() method to delete the file
     * once the file is used and no longer needed. */
    fun createTempFile(): File

    /** Create a file in internal storage given a path. Does nothing if the file
     * already exists. */
    fun createFile(path: String): File

    /** Delete a file from internal storage given a path.
     *  Returns true if the file is successfully deleted or it does not exist,
     *  otherwise returns false. */
    fun deleteFile(path: String): Boolean

    /** Creates a folder in the app internal storage. Does not do anything if the
     *  folder already exists. */
    fun createFolder(path: String): File

    /** Delete a file from internal storage given a path.
     *  Returns true if the folder is successfully deleted or it does not exist,
     *  otherwise returns false. */
    fun deleteFolder(path: String): Boolean

    /** Checks if there is 'requiredSpace' amount of free space
     * available on the device. */
    fun checkIfEnoughSpace(requiredSpace: Long): Boolean
}