package io.samagra.odk.collect.extension.components

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import io.samagra.odk.collect.extension.annotations.ODKWrapper
import io.samagra.odk.collect.extension.interactors.ODKInteractor
import io.samagra.odk.collect.extension.modules.ODKInteractorModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ODKInteractorModule::class])
interface ODKInteractorComponent {

    @ODKWrapper
    fun getODKInteractor(): ODKInteractor

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ODKInteractorComponent
    }
}