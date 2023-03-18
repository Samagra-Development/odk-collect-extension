package io.samagra.odk.collect.extension.handlers

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.samagra.odk.collect.extension.AppConstants
import io.samagra.odk.collect.extension.interactors.NetworkStorageInteractor
import io.samagra.odk.collect.extension.interactors.StorageInteractor
import io.samagra.odk.collect.extension.listeners.FileDownloadListener
import io.samagra.odk.collect.extension.listeners.FileExtractorListener
import io.samagra.odk.collect.extension.utilities.ZipExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.odk.collect.android.storage.StoragePathProvider
import org.odk.collect.android.storage.StorageSubdirectory
import java.io.File
import javax.inject.Inject

class FirebaseStorageHandler @Inject constructor(
    private val storageInteractor: StorageInteractor,
    private val storagePathProvider: StoragePathProvider
    ) : NetworkStorageInteractor {

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(360).build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.fetchAndActivate()
    }

    override fun getUpdatedZipHash(downloadPath: String?): String {
        return firebaseRemoteConfig.getString(AppConstants.ZIP_HASH_KEY)
    }

    override fun downloadFormsZip(downloadPath: String, fileDownloadListener: FileDownloadListener) {
        val tempZipFile = storageInteractor.createTempFile()
        storageReference.child(downloadPath).getFile(tempZipFile)
            .addOnProgressListener { snapshot -> fileDownloadListener.onProgress((snapshot.bytesTransferred / snapshot.totalByteCount * 100).toInt()) }
            .addOnSuccessListener {
                val filesDirectory = storagePathProvider.getProjectRootDirPath()
                CoroutineScope(Job()).launch {
                    val exception = withContext(CoroutineScope(Job()).coroutineContext) {
                        (ZipExtractor().extract(
                            tempZipFile.absolutePath,
                            filesDirectory,
                            object : FileExtractorListener {
                                override fun onExtractionComplete() {
                                    tempZipFile.deleteOnExit()
                                    storageInteractor.setPreference(
                                        AppConstants.ZIP_HASH_KEY, firebaseRemoteConfig.getString(
                                            AppConstants.ZIP_HASH_KEY))
                                    fileDownloadListener.onComplete(File(storagePathProvider.getOdkDirPath(StorageSubdirectory.FORMS)))
                                }
                                override fun onProgress(progress: Int) {
                                    fileDownloadListener.onProgress(progress)
                                }
                                override fun onFailed(e: Exception) {
                                    tempZipFile.deleteOnExit()
                                    fileDownloadListener.onCancelled(e)
                                }
                            }
                        ))
                    }
                    if (exception == null) fileDownloadListener.onComplete(File(filesDirectory, storagePathProvider.getOdkDirPath(StorageSubdirectory.FORMS)))
                    else fileDownloadListener.onCancelled(exception)
                    tempZipFile.deleteOnExit()
                }
            }
            .addOnFailureListener { exception -> fileDownloadListener.onCancelled(exception) }
    }
}
