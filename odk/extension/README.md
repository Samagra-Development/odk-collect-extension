# Technical Documentation

## ODKInteractor Interface

The OdkInteractor interface is a key component of the ODK Interactor Module, which provides developers with methods for setting up, configuring, and resetting ODK, as well as opening a form. Using a JSON string, developers can configure ODK by pulling configuration information from a JSON file. The open form functionality not only ensures that the form exists on the device, but also checks for the required XML file and media files. If these files are not present on the device, the module will automatically download them from the server.

***Methods:***

`fun setupODK(settingsJson: String, lazyDownload: Boolean, listener: ODKProcessListener)`

This method sets up the ODK environment according to a given configuration, and optionally downloads all available forms. This method must be called before any other method. \
***Parameters:*** \
`settingsJson` - A string representing the configuration for the ODK environment. \
`lazyDownload` - A Boolean indicating whether to download all available forms immediately. \
`listener` - An ODKProcessListener object to receive callbacks during the setup process.

<br>

`fun resetODK(listener: ODKProcessListener)`

This method resets the ODK environment and deletes all data. \
***Parameters:*** \
`listener` - An ODKProcessListener object to receive callbacks during the reset process.

<br>

`fun openForm(formId: String, context: Context)`

This method opens the latest version of a form with a given form ID, deleting any saved instance of a form with that ID. \
***Parameters:*** \
`formId` - A string representing the ID of the form to be opened. \
`context` - A Context object representing the Android application context. \
`listener` - A FormsProcessListener object to receive callbacks during the form opening process.

<br>

`fun openSavedForm(formId: String, context: Context, listener: FormsProcessListener)`

This method opens a saved instance of a form with a given form ID, or creates a new instance if no saved instance is found. \
***Parameters:*** \
`formId` - A string representing the ID of the form to be opened. \
`context` - A Context object representing the Android application context. \
`listener` - A FormsProcessListener object to receive callbacks during the form opening process.

<br><br>

`fun prefillAndOpenForm(formId: String, tagValueMap: HashMap<String, String>, context: Context)`

This method pre-fills a form with data from a given map of key-value pairs, and then opens the form in a given Android context. \
***Parameters:*** \
`formId` - A string representing the ID of the form to be opened. \
`tagValueMap` - A HashMap containing the key-value pairs representing the data to pre-fill the form with. \
`context` - A Context object representing the Android application context. 

<br><br>

## FormsDatabaseInteractor Interface

The FormsDatabaseInteractor interface provides a set of methods to interact with the local forms database. This interface enables developers to manage the forms that are stored on the device, including retrieving, adding, and deleting forms.

***Methods:***
The FormsDatabaseInteractor interface provides the following methods:

`fun getLocalForms(): List<Form>`

This method returns a list of all the forms that are locally available in the database. \
***Returns:*** \
A list of Form objects representing the forms that are stored locally.

<br>

`fun getFormsByFormId(formId: String): List<Form>`

This method returns a list of forms, given a form ID. \
***Parameters:*** \
`formId` - A string representing the form ID. \
***Returns:*** \
A list of Form objects representing the forms with the given form ID.

<br>

`fun getLatestFormById(formId: String): Form?`

This method returns the latest version of a form, given a form ID. \
***Parameters:*** \
`formId` - A string representing the form ID. \
***Returns:*** \
A Form object representing the latest version of the form, or null if no matching form is found.

<br>

`fun getFormByFormIdAndVersion(formId: String, formVersion: String): Form?`

This method returns a form, given a form ID and form version. \
***Parameters:*** \
`formId` - A string representing the form ID. \
`formVersion` - A string representing the form version. \
***Returns:*** \
A Form object representing the form with the given form ID and form version, or null if no matching form is found.

<br>

`fun getFormByMd5Hash(md5Hash: String): Form?`

This method returns a form, given an MD5 hash. \
***Parameters:*** \
`md5Hash` - A string representing the MD5 hash of the form. \
***Returns:*** \
A Form object representing the form with the given MD5 hash, or null if no matching form is found.

<br>

`fun deleteForm(id: Long)`

This method deletes a form from the database, given an ID. \
***Parameters:*** \
`id` - A long integer representing the ID of the form in the database.

<br>

`fun deleteByFormIdAndVersion(formId: String, formVersion: String)`

This method deletes a form from the database, given a form ID and form version. \
***Parameters:*** \
`formId` - A string representing the form ID. \
`formVersion` - A string representing the form version.

<br>

`fun deleteByFormId(formId: String)`

This method deletes all forms with a given form ID. \
***Parameters:*** \
`formId` - A string representing the form ID.

<br>

`fun clearDatabase()`

This method deletes all form data from the database.

<br>

`fun addForm(form: Form)`

This method adds a form to the database, given a form definition. \
***Parameters:*** \
`form` - A Form object representing the form to be added to the database.

<br><br>

## FormsNetworkInteractor Interface

FormsNetworkInteractor interface provides a set of methods to interact with the server to download and manage ODK forms. This interface enables developers to manage the forms that are available on the server, including retrieving forms.

***Methods:***

`getNewForms(listener: FormListDownloaderListener)`

This method checks if there are new forms available for download from the server. \
***Parameters:*** \
`listener`- A FormListDownloaderListener object that receives the list of forms available on the server.

<br>

`hasZipChanged(zipPath: String?): Boolean`

This method checks if there is a new forms zip available for download. Note that this method should be called inside a coroutine to avoid UI freezes. \
***Parameters:*** \
`zipPath`- A string representing the path to the forms zip file. \
***Returns:*** \
A boolean indicating whether the zip file has changed or not.

<br>

`downloadRequiredForms(listener: FileDownloadListener)`

This method automatically checks which forms are needed and downloads them. First, it checks if there is a zip update and then checks individual forms. \
***Parameters:*** \
`listener`- A FileDownloadListener object that receives the download progress.

<br>

`downloadFormsFromList(formsList: ArrayList<ServerFormDetails>, listener: DownloadFormsTaskListener)`

This method takes a list of forms as input and downloads them only if necessary. \
***Parameters:*** \
formsList: An array list of ServerFormDetails objects representing the forms to be downloaded. \
`listener`- A DownloadFormsTaskListener object that receives the download progress.

<br>

`downloadFormsFromIdList(formIds: ArrayList<String>, listener: DownloadFormsTaskListener)`

This method takes a list of form IDs and downloads them only if necessary. \
***Parameters:*** \
`formIds`- An array list of strings representing the IDs of the forms to be downloaded. \
`listener`- A DownloadFormsTaskListener object that receives the download progress.

<br>

`downloadFormsList(listener: FormListDownloaderListener)`

This method downloads forms from the server, given a list of forms details. \
***Parameters:*** \
`listener`- A FormListDownloaderListener object that receives the list of forms available on the server.

<br>

`downloadFormById(formId: String, listener: FileDownloadListener)`

This method downloads the latest version of a form present on the server based on form ID. \
***Parameters:*** \
`formId`- A string representing the ID of the form to be downloaded. \
`listener`- A FileDownloadListener object that receives the download progress.

<br>

`downloadFormWithServerDetail(form: ServerFormDetails, listener: DownloadFormsTaskListener)`

This method takes a ServerFormDetails object as input and downloads the corresponding form only if necessary. \
***Parameters:*** \
`form`- A ServerFormDetails object representing the form to be downloaded. \
`listener`- A DownloadFormsTaskListener object that receives the download progress.

<br><br>

## FormsInteractor Interface

FormsInteractor Interface provides methods to interact with ODK forms. Developers can use this interface to open a form with a specific form ID or MD5 hash and also pre-fill form values.

***Methods:***

`openFormWithFormId(formId: String, context: Context)`

Opens the latest version related to the given formId. \
***Parameters:*** \
`formId`- The ID of the form to open. This is a string value. \
`context`- The context in which to open the form. This is an Android Context object.

<br>

`openFormWithMd5Hash(md5Hash: String, context: Context)`

Opens the form with the given MD5 hash. \
***Parameters:*** \
`md5Hash`- The MD5 hash of the form to open. This is a string value. \
`context`- The context in which to open the form. This is an Android Context object.

<br>

`fun prefillForm(formId: String, tag: String, value: String)`

This function is used to prefill a single form field.
Note: This creates a separate form instance of the original form and does not alter the original form in any way. \
***Parameters:*** \
`formId`- The ID of the form to open. This is a string value. \
`tag`- The tag of the form element to update. This is a string value. \
`value`- The new value to set for the form element. This is a string value.

<br>

`fun prefillForm(formId: String, tagValueMap: HashMap<String, String>)`

This function is used to prefill multiple form fields.
Note: This creates a separate form instance of the original form and does not alter the original form in any way. \
***Parameters:*** \
`formId`- The ID of the form to open. This is a string value. \
`tagValueMap`- A HashMap containing tag-value pairs to update the form with. This is a map from string keys to string values.

<br>

`updateForm(formPath: String, tag: String, tagValue: String, listener: FormsProcessListener?)`

Prefills the values of a form given a tag and value or a list of tags and values. \
***Parameters:*** \
`formPath`- A string that represents the path of the form. \
`tag`- The tag of the form element to update. This is a string value. \
`tagValue`- The new value to set for the form element. This is a string value. \
`listener`- An optional listener to handle the update process. This is a FormsProcessListener object.

<br>

`updateForm(formPath: String, values: HashMap<String, String>, listener: FormsProcessListener?)`

Prefills the values of a form based on a list of a tags and values. \
***Parameters:*** \
`formPath`- A string that represents the path of the form. \
`values`- A HashMap containing tag-value pairs to update the form with. This is a map from string keys to string values. \
`listener`- An optional listener to handle the update process. This is a FormsProcessListener object.

<br><br>

## FormInstanceInteractor Interface

FormInstanceInteractor is an interface that provides methods to interact with ODK Collect form instances. The interface provides methods to retrieve, delete, and open form instances, as well as retrieve instances by their path, status, and form ID.

***Methods:***

`fun getInstanceWithId(instanceId: Long): Instance?`

This method retrieves an ODK Collect form instance by its ID. \
***Parameters:*** \
`instanceId`- The ID of the form instance to retrieve. \
***Returns:*** \
`Instance`- An Instance object representing the form instance if it exists, null otherwise.

<br>

`fun deleteInstanceWithId(instanceId: Long)`

This method deletes an ODK Collect form instance by its ID. \
***Parameters:*** \
`instanceId`- The ID of the form instance to delete.

<br>

`fun openInstance(instance: Instance, context: Context)`

This method opens an ODK Collect form instance in ODK Collect. \
***Parameters:*** \
`instance`- The form instance to open. \
`context`- The Context object of the current Activity or Application.

<br>

`fun openLatestInstanceWithFormId(formId: String, context: Context)`

This method opens the latest ODK Collect form instance with a given form ID in ODK Collect. \
***Parameters:*** \
`formId`- The ID of the form. \
`context`- The Context object of the current Activity or Application.

<br>

`fun getInstancesWithFormId(formId: String): List<Instance>`

This method retrieves all ODK Collect form instances with a given form ID. \
***Parameters:*** \
`formId`- The ID of the form. \
***Returns:*** \
`List<Instance>`- A list of Instance objects representing all form instances with the given form ID.

<br>

`fun getInstancesWithStatus(status: String): List<Instance>`

This method retrieves all ODK Collect form instances with a given status. \
***Parameters:*** \
`status (String)`- The status of the form instance. Accepted values include: \
Instance.STATUS_INCOMPLETE = "incomplete" \
Instance.STATUS_COMPLETE = "complete" \
Instance.STATUS_SUBMITTED = "submitted" \
Instance.STATUS_SUBMISSION_FAILED = "submissionFailed" \
***Returns:*** \
`List<Instance>`- A list of Instance objects representing all form instances with the given status.

<br>

`fun getInstanceByPath(instancePath: String): Instance?`

This method retrieves an ODK Collect form instance by its path. \
***Parameters:*** \
`instancePath`- The path of the form instance to retrieve. \
***Returns:*** \
`Instance`- An Instance object representing the form instance if it exists, null otherwise.
