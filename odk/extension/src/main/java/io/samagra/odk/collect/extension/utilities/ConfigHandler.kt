package io.samagra.odk.collect.extension.utilities

import android.app.Application
import android.util.Log
import io.samagra.odk.collect.extension.listeners.ODKProcessListener
import org.json.JSONException
import org.json.JSONObject
import org.odk.collect.android.injection.config.DaggerAppDependencyComponent
import org.odk.collect.android.projects.CurrentProjectProvider
import org.odk.collect.android.utilities.ProjectResetter
import org.odk.collect.android.utilities.ThemeUtils
import org.odk.collect.projects.Project
import org.odk.collect.settings.ODKAppSettingsImporter
import java.io.File
import java.util.*

/** Responsible for configuring the odk.
 *  Consumes a json string consisting of the desired configuration.
 *
 *  @author Chinmoy Chakraborty
 */
class ConfigHandler(private val application: Application) {

    private val settingsImporter: ODKAppSettingsImporter =
        DaggerAppDependencyComponent.builder().application(application).build().settingsImporter()
    private val currentProjectProvider: CurrentProjectProvider =
        DaggerAppDependencyComponent.builder().application(application).build()
            .currentProjectProvider()
    private val storagePathProvider =
        DaggerAppDependencyComponent.builder().application(application).build()
            .storagePathProvider()
    private val projectsRepository =
        DaggerAppDependencyComponent.builder().application(application).build().projectsRepository()

    private lateinit var settings: String

    fun configure(settings: String) {
        try {
            currentProjectProvider.getCurrentProject()
        } catch (e: IllegalStateException) {
            val newProject = Project.Saved(
                uuid = UUID.randomUUID().toString(),
                Project.DEMO_PROJECT.name,
                Project.DEMO_PROJECT_ICON,
                Project.DEMO_PROJECT_COLOR
            )
            projectsRepository.save(newProject)
            currentProjectProvider.setCurrentProject(newProject.uuid)
        }
        val currentSettings = getSettings() ?: settings
        setSettings(currentSettings)
        val importSuccess = settingsImporter.fromJSON(
            currentSettings,
            currentProjectProvider.getCurrentProject()
        )
        ThemeUtils(application.applicationContext).setDarkModeForCurrentProject()
        if (importSuccess) {
            File(storagePathProvider.getProjectRootDirPath() + File.separator + currentProjectProvider.getCurrentProject().name).createNewFile()
            Log.d("SETTINGS UPDATE", "Settings Imported!")
        } else {
            Log.d("SETTINGS UPDATE", "Settings could not be imported!")
            throw IllegalStateException("Settings could not be imported!")
        }
    }

    /** Resets the odk and deletes all data.
     *  Warning: It does not check for unsaved data or forms that are
     *  not uploaded before deleting. Using this method will erase all
     *  data forcefully.
     */
    fun reset(listener: ODKProcessListener) {
        try {
            val failedActions =
                DaggerAppDependencyComponent.builder().application(application).build()
                    .projectResetter().reset(
                        listOf(
                            ProjectResetter.ResetAction.RESET_CACHE,
                            ProjectResetter.ResetAction.RESET_FORMS,
                            ProjectResetter.ResetAction.RESET_LAYERS,
                            ProjectResetter.ResetAction.RESET_INSTANCES,
                            ProjectResetter.ResetAction.RESET_PREFERENCES
                        )
                    )
            if (failedActions.isEmpty())
                listener.onProcessComplete()
            else
                listener.onProcessingError(IllegalStateException("Reset action failed!"))
        } catch (e: Exception) {
            listener.onProcessingError(e)
        }
    }

    fun setServerUrl(settings: String, serverUrl: String, username: String?, password: String?) {
        if (serverUrl.isEmpty() || settings.isEmpty()) return
        val settingsJson = JSONObject(settings)
        try {
            settingsJson.put("server_url", serverUrl)
            if (username != null && username.isNotEmpty()) settingsJson.put("username", username)
            if (password != null && password.isNotEmpty()) settingsJson.put("password", password)
            setSettings(settingsJson.toString())
        } catch (e: JSONException) {
            throw IllegalStateException()
        }
        configure(settingsJson.toString())
    }

    private fun getSettings(): String? {
        return if (this::settings.isInitialized) this.settings
        else null
    }

    private fun setSettings(settings: String) {
        this.settings = settings
    }
}