package io.samagra.odk.collect.extension.annotations

import javax.inject.Qualifier


/** This annotation is used to indicate the usage of Firebase Storage for network storage operations.
*   It is crucial to include the `google-services.json` file in your project if you intend to use this annotation.
*   If the google-services.json file is not present it may result in a `ClassNotFoundException` at runtime,and will automatically
*   fallback to using [GenericNetworkStorage] as an alternative mechanism.
 */
@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class FirebaseStorage()
