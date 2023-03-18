package io.samagra.odk.collect.extension.handlers

import android.content.Context
import androidx.preference.PreferenceManager
import io.samagra.odk.collect.extension.interactors.StorageInteractor
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

class LocalStorageHandler @Inject constructor(private val context: Context) : StorageInteractor {

    override fun setPreference(key: String, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply()
    }

    override fun getPreference(key: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null)
    }

    override fun clearPreference(key: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply()
    }

    override fun clearPreferences() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
    }

    override fun createTempFile(): File {
        return File.createTempFile(UUID.randomUUID().toString(), ".tempfile")
    }

    override fun createFile(path: String): File {
        val fileToCreate = File(context.filesDir, path)
        fileToCreate.createNewFile()
        return fileToCreate
    }

    override fun deleteFile(path: String): Boolean {
        val fileToDelete = File(context.filesDir, path)
        if (fileToDelete.exists()) return fileToDelete.delete()
        return true
    }

    override fun createFolder(path: String): File {
        val folderToCreate = File(context.filesDir, path)
        if (!folderToCreate.exists())
            folderToCreate.mkdirs()
        return folderToCreate
    }

    override fun deleteFolder(path: String): Boolean {
        val folderToDelete = File(context.filesDir, path)
        if (folderToDelete.exists()) {
            return try {
                FileUtils.deleteDirectory(folderToDelete)
                true
            } catch (exception: IOException) {
                false
            }
        }
        return true
    }

    override fun checkIfEnoughSpace(requiredSpace: Long): Boolean {
        val availableFreeSpace = File(context.filesDir.absolutePath).freeSpace
        return availableFreeSpace >= requiredSpace
    }
}