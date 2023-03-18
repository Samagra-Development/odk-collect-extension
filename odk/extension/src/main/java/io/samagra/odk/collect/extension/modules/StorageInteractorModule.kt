package io.samagra.odk.collect.extension.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import io.samagra.odk.collect.extension.annotations.LocalStorage
import io.samagra.odk.collect.extension.handlers.LocalStorageHandler
import io.samagra.odk.collect.extension.interactors.StorageInteractor
import javax.inject.Singleton

@Module
class StorageInteractorModule {

    @Provides
    @Singleton
    @LocalStorage
    fun getLocalStorageHandler(context: Context): StorageInteractor {
        return LocalStorageHandler(context)
    }
}