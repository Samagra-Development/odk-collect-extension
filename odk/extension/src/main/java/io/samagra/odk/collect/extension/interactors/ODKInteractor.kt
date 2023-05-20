package io.samagra.odk.collect.extension.interactors

import android.content.Context
import io.samagra.odk.collect.extension.listeners.FormsProcessListener
import io.samagra.odk.collect.extension.listeners.ODKProcessListener

/** The OdkInteractor interface is a key component of the ODK Interactor Module, which provides
 * developers with methods for setting up, configuring, and resetting ODK, as well as opening a form.
 * Using a JSON string, developers can configure ODK by pulling configuration information from a JSON
 * file. The open form functionality not only ensures that the form exists on the device, but also
 * checks for the required XML file and media files. If these files are not present on the device,
 * the module will automatically download them from the server.
 *
 * @author Chinmoy Chakraborty
 */
interface ODKInteractor {

    /** Sets up the odk according to a given configHandler.
     *  If [lazyDownload] is set to true, it will **not** download
     *  all the available forms right away else, it will download
     *  all the forms along with setup. This method **MUST** be called
     *  before calling any other method. */
    fun setupODK(settingsJson: String, lazyDownload: Boolean, listener: ODKProcessListener)

    /** Resets everything in odk and deletes all data. */
    fun resetODK(listener: ODKProcessListener)

}
