# ODK Collect Extension

## Introduction

### ODK Collect

ODK Collect is an open source Android app that replaces paper forms used in survey-based data
gathering. It supports a wide range of question and answer types, and is designed to work well
without network connectivity.

You can read more about ODK Collect [here](https://docs.getodk.org/collect-intro/).

### ODK Collect Extension

ODK Collect Extension is a suite of tools built on top of ODK Collect that enable developers to
seamlessly integrate ODK Collect into their own Android applications. With this module, developers
can take advantage of all the powerful features of ODK Collect, while also customizing the app's
functionality to meet their specific needs.

We have also built a bunch of additional features on top of ODK collect:

* **Integration of form zip to speed up the download process**: This feature allows the module to
  download multiple forms as a single zip file from the server, which speeds up the download process
  compared to downloading each form individually. We use this feature in all of our apps & believe
  this will expedite the reach of ODK in developing countries.
* **Parallel form download**: This feature allows the module to download multiple forms in parallel,
  which speeds up the download process even more.
* **Custom theming on form screens**: This feature allows the module to customize the look and feel
  of the forms by providing a settings file that can be used to modify the form screens according to
  the user's needs.
* **Callbacks on form submission**: This feature allows the module to receive callbacks when a user
  submits a form, which can be used to perform custom actions based on the form submission.
* **Configuration to submit results to a custom server**: This feature allows the module to send
  form submission results to a custom server, which is useful when integrating with other systems.
* **Form data prefilling**: This feature allows the module to prefill form data based on
  user-provided values, which saves time and improves data accuracy.
* **Automatic form download if a form is not available on device**: This feature automatically
  downloads a form from the server if it is not already available on the local device before opening
  the form.
* **Notification integration**: This feature allows the module to modify ODK notifications based on
  the user's needs, such as changing the notification sound or adding custom text to the
  notification.

ODK Collect is a standalone Android application which can be communicated via intents and providers.
ODK Collect Extension is a library which can be integrated into any existing Android application to
embed ODK directly into them.

## Installation Set-Up Guide
To setup ODK extensions library and ODK Central Server locally follow our step-by-step guide [here](./INITIAL-SETUP.md).

## Versions

* Current version: 0.0.1
  * Based on ODK Release: [v2022.4.4](https://github.com/getodk/collect/releases/tag/v2022.4.4)

## Integration

### via Module

1. Download
   the [ODK module](https://github.com/Samagra-Development/odk-collect-extension/tree/main/odk).
2. Copy the directory in your project folder.
3. Update your app's `settings.gradle` to include the ODK modules.

  ```gradle
  include ':odk:collect:projects'
  include ':odk:collect:formstest'
  include ':odk:collect:shared'
  include ':odk:collect:forms'
  include ':odk:collect:analytics'
  include ':odk:collect:androidshared'
  include ':odk:collect:audiorecorder'
  include ':odk:collect:testshared'
  include ':odk:collect:audioclips'
  include ':odk:collect:strings'
  include ':odk:collect:async'
  include ':odk:collect:collect_app'
  include ':odk:collect:nbistubs'
  include ':odk:collect:material'
  include ':odk:collect:fragmentstest'
  include ':odk:collect:location'
  include ':odk:collect:geo'
  include ':odk:collect:errors'
  include ':odk:collect:externalapp'
  include ':odk:collect:upgrade'
  include ':odk:collect:permissions'
  include ':odk:collect:imageloader'
  include ':odk:collect:glide'
  include ':odk:collect:androidtest'
  include ':odk:collect:settings'
  include ':odk:collect:servicetest'
  include ':odk:collect:maps'
  include ':odk:collect:osmdroid'
  include ':odk:collect:icons'
  include ':odk:collect:crash-handler'
  include ':odk:collect:entities'
  include ':odk:collect:selfie-camera'
  include ':odk:extension'
  ```

4. Add `odk:extension` module dependency to your app's module

```gradle
implementation project(':odk:extension')
```

5. Build your project

### via Gradle dependency

**Coming soon**

## Usage

Wiki coming soon

## Need

* **Integration** - ODK Collect is a standalone Android app which can connect to a self hosted ODK
  Central instance. For developers with existing Android applications ODK Collect exposes content
  providers and intents to open forms and collect data. This requires user's of the apps to download
  2 different apps to use ODK. ODK Collect Extension makes it extremely easy to integrate ODK
  Collect's application inside existing Android apps via a single gradle dependency.

* **Customization** - Embedding ODK Collect in their own application allows organizations to
  customize the app's user interface and functionality to better suit their specific needs. They can
  also incorporate their own branding and design elements to maintain a consistent user experience
  across their entire suite of applications.

* **Integration with other systems** - Organizations may need to integrate data collection with
  other systems or workflows, and embedding ODK Collect in their own application allows them to do
  this seamlessly. By embedding the app, data can be automatically transferred to other systems or
  databases, reducing the need for manual data entry and minimizing errors.

* **User experience** - Embedding ODK Collect in an existing application can improve the user
  experience, as users don't have to switch between different apps to collect and manage data. This
  can result in increased productivity and efficiency.

* **Access control and security** - Embedding ODK Collect in their own application allows
  organizations to enforce strict access controls and security measures to protect sensitive data.
  They can also store data on their own servers or in their own cloud storage, giving them more
  control over how data is managed and secured.

* **Workflow management** - By embedding ODK Collect in their own application, organizations can
  more easily manage and track data collection workflows. They can assign tasks to specific users or
  teams, monitor progress, and generate reports and analytics.

## Git

We closely follow the stable releases of ODK Collect and build extensions on top of them.

![Git Versioning](./GitFlowOdkCollectExtension.png)

* **collect-stable-release**  - This branch tracks the stable releases of the original
  getodk/collect repository. This branch is updated only when a new stable release is made available
  in the original repository. No active development is done in this branch.

* **develop** - This branch is used for active development. All feature branches are merged into
  this branch, and it is updated regularly. This branch is intended to be used for experimental and
  ongoing development work.

* **feature/$FEATURE_NAME** - These branches are used for active development work, with each branch
  focusing on a specific feature or functionality. They are created from the develop branch, and
  when work is completed, they are merged back into develop.

* **main** - This branch is used to build releases derived from the develop branch. All completed
  work in the develop branch is merged 33into this branch for release. This branch is intended to be
  used for stable and production-ready releases.

## Versioning

We follow the [Semantic Versioning 2.0.0](https://semver.org/#semantic-versioning-200) guidelines
for versioning the ODK Collect Extension library.

## Interfaces

### ODKInteractor

The OdkInteractor interface is a key component of the ODK Interactor Module, which provides
developers with methods for setting up, configuring, and resetting ODK, as well as opening a form.
Using a JSON string, developers can configure ODK by pulling configuration information from a JSON
file. The open form functionality not only ensures that the form exists on the device, but also
checks for the required XML file and media files. If these files are not present on the device, the
module will automatically download them from the server.
Refer to the technical documentation [here](./odk/extension/README.md#odkinteractor-interface).

### FormsDatabaseInteractor

The FormsDatabaseInteractor interface provides a set of methods to interact with the local forms
database. The interface includes methods for fetching a list of all locally available forms, getting
a list of forms by formId, retrieving the latest version of a form by formId, and deleting forms
from the database based on various criteria. Additionally, the interface provides methods for adding
new forms to the database. By providing these functionalities, the FormsDatabaseInteractor interface
helps to simplify the management of local forms on the device and make it easier for developers to
incorporate forms functionality into their applications. Refer to the technical
documentation [here](./odk/extension/README.md#formsdatabaseinteractor-interface).

### FormsNetworkInteractor

The FormsNetworkInteractor interface provides a set of methods to interact with the server to
download and manage forms. It is responsible for carrying out all network-related tasks, including
checking for new forms available for download, downloading individual forms or a list of forms,
checking if there is a new forms zip available for download, and downloading the latest version of a
form based on its form ID. Developers can use this interface to manage forms on the server and
ensure that their users have the latest version of the forms available. Refer to the technical
documentation [here](./odk/extension/README.md#formsnetworkinteractor-interface).

### FormsInteractor

The FormsInteractor interface provides methods for interacting with ODK forms. It allows users to
open the latest version of a form based on its ID or MD5 hash, as well as prefill form values given
specific tags and their corresponding values. The FormsInteractor interface is designed to provide
an easy and convenient way to manage ODK forms and their data. Refer to the technical
documentation [here](./odk/extension/README.md#formsinteractor-interface).

### FormInstanceInteractor

FormInstanceInteractor is an interface that provides methods to interact with ODK Collect form
instances. The interface provides methods to retrieve, delete, and open form instances, as well as
retrieve instances by their path, status, and form ID. Refer to the technical
documentation [here](./odk/extension/README.md#forminstanceinteractor-interface).

### StorageInteractor

StorageInteractor interface provides a set of methods for performing basic storage utility tasks in
an application. It includes methods for managing preferences using the default shared preference
file, creating and deleting files and folders in the app's internal storage, and checking the
availability of free space on the device. These methods allow developers to easily handle
storage-related operations in their applications. Refer to the technical
documentation [here](./odk/extension/README.md#storageinteractor-interface).

## Contribution Guidelines

- Clone the repository and create a new branch from the develop branch for the `feature/bugfix`
  being worked on. Name the branch according to the feature being worked on.

- Make changes to the code in the branch and commit the changes regularly.

- Once the work is completed and tested, push the branch to the remote repository and create a Pull
  Request against the `develop` branch.

- The Pull Request will be reviewed by other contributors and merged into the `develop` branch if it
  meets the necessary requirements and passes any relevant testing.

- After a release candidate has been thoroughly tested, and deemed ready for release, the changes
  will be merged from `develop` to `main`, and a new release will be built and made available.
