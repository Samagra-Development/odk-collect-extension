package io.samagra.odk.collect.extension.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import io.samagra.odk.collect.extension.annotations.ODKFormsInteractor
import io.samagra.odk.collect.extension.components.DaggerFormInstanceInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerFormsDatabaseInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerFormsNetworkInteractorComponent
import io.samagra.odk.collect.extension.handlers.ODKFormsHandler
import io.samagra.odk.collect.extension.interactors.FormsInteractor
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import javax.inject.Singleton

@Module
class FormsInteractorModule {

    @Provides
    @Singleton
    @ODKFormsInteractor
    fun getODKFormsHandler(application: Application): FormsInteractor {
        val appDependencyComponent = DaggerAppDependencyComponent.builder().application(application).build()
        val currentProjectProvider = appDependencyComponent.currentProjectProvider()
        val mediaUtils = appDependencyComponent.providesMediaUtils()
        val storagePathProvider = appDependencyComponent.storagePathProvider()
        val entitiesRepository = appDependencyComponent.entitiesRepositoryProvider().get(currentProjectProvider.getCurrentProject().uuid)
        val formsDatabaseInteractor = DaggerFormsDatabaseInteractorComponent.factory().create(application).getFormsDatabaseInteractor()
        val formsNetworkInteractor = DaggerFormsNetworkInteractorComponent.factory().create(application).getFormsNetworkInteractor()
        val instancesRepository = DaggerAppDependencyComponent.builder().application(application).build().instancesRepositoryProvider().get()
        val formInstanceInteractor = DaggerFormInstanceInteractorComponent.factory().create(application).getFormInstanceInteractor()

        return ODKFormsHandler(currentProjectProvider, formsDatabaseInteractor, storagePathProvider, mediaUtils, entitiesRepository, formsNetworkInteractor,instancesRepository,formInstanceInteractor)
    }
}
