package io.samagra.odk.collect.extension.modules

import android.app.Application
import io.samagra.odk.collect.extension.handlers.ODKHandler
import io.samagra.odk.collect.extension.interactors.ODKInteractor
import dagger.Module
import dagger.Provides
import io.samagra.odk.collect.extension.annotations.ODKWrapper
import javax.inject.Singleton

@Module
class ODKInteractorModule {

    @Provides
    @Singleton
    @ODKWrapper
    fun getODKHandler(application: Application): ODKInteractor {
        return ODKHandler(application)
    }
}