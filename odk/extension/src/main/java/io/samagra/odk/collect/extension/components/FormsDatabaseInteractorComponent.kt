package io.samagra.odk.collect.extension.components

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import io.samagra.odk.collect.extension.annotations.SQLiteFormsDatabase
import io.samagra.odk.collect.extension.interactors.FormsDatabaseInteractor
import io.samagra.odk.collect.extension.modules.FormsDatabaseInteractorModule
import javax.inject.Singleton

@Singleton
@Component(modules = [FormsDatabaseInteractorModule::class])
interface FormsDatabaseInteractorComponent {

    @SQLiteFormsDatabase
    fun getFormsDatabaseInteractor(): FormsDatabaseInteractor

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): FormsDatabaseInteractorComponent
    }
}