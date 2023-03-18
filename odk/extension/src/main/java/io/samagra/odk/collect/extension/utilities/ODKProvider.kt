package io.samagra.odk.collect.extension.utilities

import android.app.Application
import android.content.Context
import io.samagra.odk.collect.extension.components.DaggerFormsDatabaseInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerFormsInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerFormsNetworkInteractorComponent
import io.samagra.odk.collect.extension.components.DaggerODKInteractorComponent
import io.samagra.odk.collect.extension.interactors.*
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import org.odk.collect.android.storage.StoragePathProvider
import org.odk.collect.permissions.PermissionsProvider

/**
 * ODKProvider is a helper utility class that provides access to various interfaces inside the
 * Collect app module and extension module. This class provides a simplified way to retrieve objects
 * of these interfaces without having to manually create instances of the corresponding classes.
 *
 * ODKProvider contains several methods that can be used to retrieve objects of different interfaces,
 * such as FormsInteractor, FormInstanceInteractor, and FormsDatabaseInteractor, among others.
 * These interfaces allow you to interact with different parts of the ODK system, such as forms,
 * instances, and database.
 *
 *  The ODKProvider is implemented using the Singleton design pattern, meaning that there is only
 *  one instance of it throughout the app's lifecycle. Make sure to call the init() method once
 *  and pass an application context before trying to retrieve any objects.
 *
 *  @author Chinmoy Chakraborty
 */

object ODKProvider {

    private lateinit var application: Application
    private lateinit var odkInteractor: ODKInteractor
    private lateinit var formsDatabaseInteractor: FormsDatabaseInteractor
    private lateinit var formsNetworkInteractor: FormsNetworkInteractor
    private lateinit var networkStorageInteractor: NetworkStorageInteractor
    private lateinit var formsInteractor: FormsInteractor
    private lateinit var permissionProvider: PermissionsProvider
    private lateinit var storagePathProvider: StoragePathProvider
    private lateinit var configHandler: ConfigHandler

    fun init(application: Application) {
        ODKProvider.application = application
    }

    fun getApplication(): Application {
        return application
    }

    fun getApplicationContext(): Context {
        return application
    }

    fun getOdkInteractor(): ODKInteractor {
        if (this::odkInteractor.isInitialized) return odkInteractor
        odkInteractor = DaggerODKInteractorComponent.factory().create(
            getApplication()
        ).getODKInteractor()
        return odkInteractor
    }

    fun getFormsDatabaseInteractor(): FormsDatabaseInteractor {
        if (this::formsDatabaseInteractor.isInitialized) return formsDatabaseInteractor
        formsDatabaseInteractor = DaggerFormsDatabaseInteractorComponent.factory()
            .create(getApplication()).getFormsDatabaseInteractor()
        return formsDatabaseInteractor
    }

    fun getFormsNetworkInteractor(): FormsNetworkInteractor {
        if (this::formsNetworkInteractor.isInitialized) return formsNetworkInteractor
        formsNetworkInteractor = DaggerFormsNetworkInteractorComponent.factory().create(
            getApplication()
        ).getFormsNetworkInteractor()
        return formsNetworkInteractor
    }

    fun getNetworkStorageInteractor(): NetworkStorageInteractor {
        if (this::networkStorageInteractor.isInitialized) return networkStorageInteractor
        networkStorageInteractor = DaggerFormsNetworkInteractorComponent.factory().create(
            getApplication()
        ).getNetworkStorageInteractor()
        return networkStorageInteractor
    }

    fun getFormsInteractor(): FormsInteractor {
        if (this::formsInteractor.isInitialized) return formsInteractor
        formsInteractor = DaggerFormsInteractorComponent.factory().create(
            getApplication()
        ).getFormsInteractor()
        return formsInteractor
    }

    fun getPermissionProvider(): PermissionsProvider {
        if (this::permissionProvider.isInitialized) return permissionProvider
        permissionProvider = DaggerAppDependencyComponent.builder().application(getApplication()).build().permissionsProvider()
        return permissionProvider
    }

    fun getStoragePathProvider(): StoragePathProvider {
        if (this::storagePathProvider.isInitialized) return storagePathProvider
        storagePathProvider = DaggerAppDependencyComponent.builder().application(getApplication()).build().storagePathProvider()
        return storagePathProvider
    }

    fun getConfigHandler(): ConfigHandler {
        if (this::configHandler.isInitialized) return configHandler
        configHandler = ConfigHandler(getApplication())
        return configHandler
    }
}