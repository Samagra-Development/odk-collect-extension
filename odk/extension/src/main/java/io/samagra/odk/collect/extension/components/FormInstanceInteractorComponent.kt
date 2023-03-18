package io.samagra.odk.collect.extension.components

import android.app.Application
import io.samagra.odk.collect.extension.annotations.ODKFormInstanceHandler
import io.samagra.odk.collect.extension.interactors.FormInstanceInteractor
import io.samagra.odk.collect.extension.modules.FormInstanceInteractorModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FormInstanceInteractorModule::class])
interface FormInstanceInteractorComponent {

    @ODKFormInstanceHandler
    fun getFormInstanceInteractor(): FormInstanceInteractor

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): FormInstanceInteractorComponent
    }
}
