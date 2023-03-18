package io.samagra.odk.collect.extension.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import io.samagra.odk.collect.extension.annotations.SQLiteFormsDatabase
import io.samagra.odk.collect.extension.components.DaggerStorageInteractorComponent
import io.samagra.odk.collect.extension.handlers.FormsDatabaseHandler
import io.samagra.odk.collect.extension.interactors.FormsDatabaseInteractor
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import javax.inject.Singleton

@Module
class FormsDatabaseInteractorModule {

    @Provides
    @Singleton
    @SQLiteFormsDatabase
    fun getSQLiteFormsDatabaseHandler(application: Application): FormsDatabaseInteractor {
        val formsRepository = DaggerAppDependencyComponent.builder().application(application).build().formsRepositoryProvider().get()
        val storageInteractor = DaggerStorageInteractorComponent.factory().create(application).getStorageInteractor()
        return FormsDatabaseHandler(formsRepository, storageInteractor)
    }
}