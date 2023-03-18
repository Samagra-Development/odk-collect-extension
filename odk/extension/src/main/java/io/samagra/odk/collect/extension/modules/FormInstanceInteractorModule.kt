package io.samagra.odk.collect.extension.modules

import android.app.Application
import io.samagra.odk.collect.extension.annotations.ODKFormInstanceHandler
import io.samagra.odk.collect.extension.handlers.FormInstanceHandler
import io.samagra.odk.collect.extension.interactors.FormInstanceInteractor
import dagger.Module
import dagger.Provides
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import javax.inject.Singleton

@Module
class FormInstanceInteractorModule {

    @Provides
    @Singleton
    @ODKFormInstanceHandler
    fun getODKFormInstanceHandler(application: Application): FormInstanceInteractor {
        val appDependencyComponent = DaggerAppDependencyComponent.builder().application(application).build()
        val currentProjectProvider = appDependencyComponent.currentProjectProvider()
        val instancesRepository = appDependencyComponent.instancesRepositoryProvider().get()
        return FormInstanceHandler(instancesRepository, currentProjectProvider)
    }
}
