package io.samagra.odk.collect.extension.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.samagra.odk.collect.extension.annotations.LocalStorage
import io.samagra.odk.collect.extension.interactors.StorageInteractor
import io.samagra.odk.collect.extension.modules.StorageInteractorModule
import javax.inject.Singleton

@Singleton
@Component(modules = [StorageInteractorModule::class])
interface StorageInteractorComponent {

    @LocalStorage
    fun getStorageInteractor(): StorageInteractor

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance content: Context): StorageInteractorComponent
    }
}