package io.samagra.odk.collect.extension.components

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import io.samagra.odk.collect.extension.annotations.GenericNetworkStorage
import io.samagra.odk.collect.extension.annotations.ODKNetworkHandler
import io.samagra.odk.collect.extension.interactors.FormsNetworkInteractor
import io.samagra.odk.collect.extension.interactors.NetworkStorageInteractor
import io.samagra.odk.collect.extension.modules.FormsNetworkInteractorModule
import javax.inject.Singleton

@Singleton
@Component(modules = [FormsNetworkInteractorModule::class])
interface FormsNetworkInteractorComponent {

    @GenericNetworkStorage
    fun getNetworkStorageInteractor(): NetworkStorageInteractor

    @ODKNetworkHandler
    fun getFormsNetworkInteractor(): FormsNetworkInteractor

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): FormsNetworkInteractorComponent
    }
}
