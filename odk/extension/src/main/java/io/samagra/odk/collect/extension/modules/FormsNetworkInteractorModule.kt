package io.samagra.odk.collect.extension.modules

import android.app.Application
import io.samagra.odk.collect.extension.handlers.FirebaseStorageHandler
import io.samagra.odk.collect.extension.handlers.FormsNetworkHandler
import io.samagra.odk.collect.extension.handlers.GenericNetworkStorageHandler
import io.samagra.odk.collect.extension.interactors.FormsNetworkInteractor
import io.samagra.odk.collect.extension.interactors.NetworkStorageInteractor
import io.samagra.odk.collect.extension.utilities.FormsDownloadUtil
import dagger.Module
import dagger.Provides
import io.samagra.odk.collect.extension.annotations.FirebaseStorage
import io.samagra.odk.collect.extension.annotations.GenericNetworkStorage
import io.samagra.odk.collect.extension.annotations.ODKNetworkHandler
import io.samagra.odk.collect.extension.components.DaggerFormsNetworkInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerStorageInteractorComponent
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import javax.inject.Singleton

@Module
class FormsNetworkInteractorModule {

    @Provides
    @Singleton
    @FirebaseStorage
    fun getFirebaseStorageHandler(application: Application): NetworkStorageInteractor {
        val storageInteractor = DaggerStorageInteractorComponent.factory().create(application.applicationContext).getStorageInteractor()
        val storagePathProvider = DaggerAppDependencyComponent.builder().application(application).build().storagePathProvider()
        application.applicationContext.resources.getIdentifier("google_app_id", "string", application.packageName).apply {
           return if (this != 0) {
               FirebaseStorageHandler(storageInteractor, storagePathProvider)
           } else {
               GenericNetworkStorageHandler(storageInteractor, storagePathProvider)
           }
        }
    }

    @Provides
    @Singleton
    @GenericNetworkStorage
    fun getGenericNetworkStorageHandler(application: Application): NetworkStorageInteractor {
        val storageInteractor = DaggerStorageInteractorComponent.factory().create(application.applicationContext).getStorageInteractor()
        val storagePathProvider = DaggerAppDependencyComponent.builder().application(application).build().storagePathProvider()
        return GenericNetworkStorageHandler(storageInteractor, storagePathProvider)
    }

    @Provides
    @Singleton
    @ODKNetworkHandler
    fun getFormsNetworkInteractor(application: Application): FormsNetworkInteractor {
        val appDependencyComponent = DaggerAppDependencyComponent.builder().application(application).build()
        val formsDownloadUtil = FormsDownloadUtil(appDependencyComponent.providesServerFormsDetailsFetcher(), appDependencyComponent.providesFormDownloader())
        val networkComponent = DaggerFormsNetworkInteractorComponent.factory().create(application)
        val networkStorageInteractor = networkComponent.getNetworkStorageInteractor()
        val storageInteractor = DaggerStorageInteractorComponent.factory().create(application.applicationContext).getStorageInteractor()
        val storagePathProvider = appDependencyComponent.storagePathProvider()
        return FormsNetworkHandler(formsDownloadUtil, networkStorageInteractor, storageInteractor, storagePathProvider)
    }
}
