package io.samagra.odk.collect.extension.components

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import io.samagra.odk.collect.extension.annotations.ODKFormsInteractor
import io.samagra.odk.collect.extension.interactors.FormsInteractor
import io.samagra.odk.collect.extension.modules.FormsInteractorModule
import javax.inject.Singleton

@Singleton
@Component(modules = [FormsInteractorModule::class])
interface FormsInteractorComponent {

    @ODKFormsInteractor
    fun getFormsInteractor(): FormsInteractor

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance application: Application): FormsInteractorComponent
    }
}
