package io.samagra.odk.collect.extension.modules

import dagger.Module
import dagger.Provides
import io.samagra.odk.collect.extension.annotations.ODKActivitiesHandler
import io.samagra.odk.collect.extension.annotations.ODKFormInstanceHandler
import io.samagra.odk.collect.extension.handlers.ODKActivityHandler
import io.samagra.odk.collect.extension.interactors.ODKActivityInteractor
import javax.inject.Singleton

@Module
class ODKActivityInteractorModule {

    @Provides
    @Singleton
    @ODKActivitiesHandler
    fun getODKActivitiesHandler(): ODKActivityInteractor {
        return ODKActivityHandler()
    }
}
