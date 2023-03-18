package com.samagra.odkinteractor

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.odkinteractor.interactors.StorageInteractor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*

@RunWith(AndroidJUnit4::class)
class StorageInteractorTest {

    lateinit var storageInteractor: StorageInteractor

    @Before
    fun setup() {
        storageInteractor = DaggerStorageInteractorComponent.factory().create(ApplicationProvider.getApplicationContext()).getStorageInteractor()
    }

    @Test
    fun createsPreference() {
        val key = "TEST_KEY"
        val value = "TEST_VALUE"
        storageInteractor.setPreference(key, value)
        val actualValue = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).getString(key, "")
        // Check if correct value is set
        Assert.assertEquals(actualValue, value)
    }

    @Test
    fun getsPreference() {
        val key = "TEST_KEY"
        val value = "TEST_VALUE"
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit().putString(key, value).commit()
        // Check if correct value is fetched
        Assert.assertEquals(value, storageInteractor.getPreference(key))
    }

    @Test
    fun clearsPreference() {
        val key = "TEST_KEY"
        val value = "TEST_VALUE"
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit().putString(key, value).commit()
        storageInteractor.clearPreference(key)
        // Clears a preference properly
        Assert.assertEquals(storageInteractor.getPreference(key), null)
    }

    @Test
    fun clearsAllPreferences() {
        val key1 = "TEST_KEY1"
        val value1 = "TEST_VALUE1"
        val key2 = "TEST_KEY2"
        val value2 = "TEST_VALUE2"
        val key3 = "TEST_KEY3"
        val value3 = "TEST_VALUE3"
        val preferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        val editor = preferences.edit()
        editor.putString(key1, value1)
        editor.putString(key2, value2)
        editor.putString(key3, value3)
        editor.commit()
        storageInteractor.clearPreferences()
        // Clears all preferences
        Assert.assertFalse(preferences.contains(key1))
        Assert.assertFalse(preferences.contains(key2))
        Assert.assertFalse(preferences.contains(key3))
    }

    @Test
    fun createsTemporaryFile() {
        val tempFile = storageInteractor.createTempFile()
        Assert.assertTrue(tempFile.exists())
    }

    @Test
    fun createsFile() {
        val fileName = UUID.randomUUID().toString()
        val createdFile = storageInteractor.createFile(fileName)
        Assert.assertTrue(createdFile.exists())
        createdFile.deleteOnExit()
    }

    @Test
    fun deletesFile() {
        val fileName = UUID.randomUUID().toString()
        val context: Context = ApplicationProvider.getApplicationContext()
        val createdFile = File(context.filesDir, fileName)
        createdFile.createNewFile()
        // Check if file is deleted
        storageInteractor.deleteFile(fileName)
        Assert.assertFalse(createdFile.exists())
    }

    @Test
    fun createsFolder() {
        val folderName = UUID.randomUUID().toString()
        val createdFolder = storageInteractor.createFolder(folderName)
        Assert.assertTrue(createdFolder.exists())
        createdFolder.deleteOnExit()
    }

    @Test
    fun deletesFolder() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val parentDirPath = "UUID.randomUUID().toString()"
        val parentFolder = File(context.filesDir, parentDirPath)
        val childFolder1 = File(parentFolder, UUID.randomUUID().toString())
        val childFolder2 = File(parentFolder, UUID.randomUUID().toString())
        val childFolder3 = File(parentFolder, UUID.randomUUID().toString())
        val childFolder4 = File(childFolder1, UUID.randomUUID().toString())
        childFolder1.mkdirs()
        childFolder2.mkdirs()
        childFolder3.mkdirs()
        childFolder4.mkdirs()
        // Check if all folders are deleted
        storageInteractor.deleteFolder(parentDirPath)
        Assert.assertFalse(parentFolder.exists())

        // Cleanup code in case folders are not cleared
        childFolder4.deleteOnExit()
        childFolder1.deleteOnExit()
        childFolder2.deleteOnExit()
        childFolder3.deleteOnExit()
        parentFolder.deleteOnExit()
    }

    @Test
    fun checkEnoughSpaceWorks() {
        val veryLargeSpace = Long.MAX_VALUE
        val verySmallSpace = 1L

        Assert.assertFalse(storageInteractor.checkIfEnoughSpace(veryLargeSpace))
        Assert.assertTrue(storageInteractor.checkIfEnoughSpace(verySmallSpace))
    }
}