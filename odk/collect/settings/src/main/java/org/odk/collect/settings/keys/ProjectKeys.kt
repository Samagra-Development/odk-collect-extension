package org.odk.collect.settings.keys

object ProjectKeys {
    // server_preferences.xml
    const val KEY_PROTOCOL = "protocol"

    // odk_server_preferences.xml
    const val KEY_SERVER_URL = "server_url"
    const val KEY_USERNAME = "username"
    const val KEY_PASSWORD = "password"
    const val KEY_SERVER_SUBMISSION_IS_ENABLED = "server_submission_enabled"

    const val KEY_CUSTOM_SERVER_URL = "custom_server_url"
    const val KEY_CUSTOM_SERVER_HEADERS = "custom_server_headers"
    const val KEY_CUSTOM_SERVER_IS_ENABLED = "custom_server_enabled"

    // google_preferences.xml
    const val KEY_SELECTED_GOOGLE_ACCOUNT = "selected_google_account"
    const val KEY_GOOGLE_SHEETS_URL = "google_sheets_url"

    // user_interface_preferences.xml
    const val KEY_APP_THEME = "appTheme"
    const val KEY_APP_LANGUAGE = "app_language"
    const val KEY_FONT_SIZE = "font_size"
    const val KEY_NAVIGATION = "navigation"

    // map_preferences.xml
    const val KEY_BASEMAP_SOURCE = "basemap_source"

    // basemap styles
    const val KEY_GOOGLE_MAP_STYLE = "google_map_style"
    const val KEY_MAPBOX_MAP_STYLE = "mapbox_map_style"
    const val KEY_USGS_MAP_STYLE = "usgs_map_style"
    const val KEY_CARTO_MAP_STYLE = "carto_map_style"
    const val KEY_REFERENCE_LAYER = "reference_layer"

    // form_management_preferences.xml
    const val KEY_FORM_UPDATE_MODE = "form_update_mode"
    const val KEY_PERIODIC_FORM_UPDATES_CHECK = "periodic_form_updates_check"
    const val KEY_AUTOMATIC_UPDATE = "automatic_update"
    const val KEY_HIDE_OLD_FORM_VERSIONS = "hide_old_form_versions"
    const val KEY_AUTOSEND = "autosend"
    const val KEY_DELETE_AFTER_SEND = "delete_send"
    const val KEY_CONSTRAINT_BEHAVIOR = "constraint_behavior"
    const val KEY_HIGH_RESOLUTION = "high_resolution"
    const val KEY_IMAGE_SIZE = "image_size"
    const val KEY_GUIDANCE_HINT = "guidance_hint"
    const val KEY_EXTERNAL_APP_RECORDING = "external_app_recording"
    const val KEY_INSTANCE_SYNC = "instance_sync"

    // identity_preferences.xml
    const val KEY_ANALYTICS = "analytics"

    // form_metadata_preferences.xml
    const val KEY_METADATA_USERNAME = "metadata_username"
    const val KEY_METADATA_PHONENUMBER = "metadata_phonenumber"
    const val KEY_METADATA_EMAIL = "metadata_email"
    const val KEY_FORM_METADATA = "form_metadata"
    const val KEY_BACKGROUND_LOCATION = "background_location"
    const val KEY_BACKGROUND_RECORDING = "background_recording"

    // experimental_preferences.xml
    const val KEY_PREDICATE_CACHING = "predicate_caching"

    // values
    const val PROTOCOL_SERVER = "odk_default"
    const val PROTOCOL_GOOGLE_SHEETS = "google_sheets"
    const val NAVIGATION_SWIPE = "swipe"
    const val NAVIGATION_BUTTONS = "buttons"
    const val NAVIGATION_BOTH = "swipe_buttons"
    const val CONSTRAINT_BEHAVIOR_ON_SWIPE = "on_swipe"

    // basemap section
    const val CATEGORY_BASEMAP = "category_basemap"

    // basemap source values
    const val BASEMAP_SOURCE_GOOGLE = "google"
    const val BASEMAP_SOURCE_MAPBOX = "mapbox"
    const val BASEMAP_SOURCE_OSM = "osm"
    const val BASEMAP_SOURCE_USGS = "usgs"
    const val BASEMAP_SOURCE_STAMEN = "stamen"
    const val BASEMAP_SOURCE_CARTO = "carto"

    const val GOOGLE_DRIVE_DEPRECATION_LEARN_MORE_CLICKED = "gd_lear_more_clicked"
    const val GOOGLE_DRIVE_DEPRECATION_BANNER_DISMISSED = "gd_banner_dismissed"

    // Custom UI settings
    const val FORM_ACTIVITY_TOOLBAR_BACKGROUND_COLOR = "form_activity_toolbar_background_color"
    const val FORM_ACTIVITY_TOOLBAR_FOREGROUND_COLOR = "form_activity_toolbar_foreground_color"
    const val FORM_ACTIVITY_PRIMARY_COLOR = "form_activity_primary_color"
    const val FORM_ACTIVITY_FOOTER_COLOR = "form_activity_footer_color"
    const val FORM_ACTIVITY_NAVIGATION_BACKGROUND_COLOR = "form_activity_navigation_button_background_color"
    const val FORM_ACTIVITY_NAVIGATION_FOREGROUND_COLOR = "form_activity_navigation_button_foreground_color"
    const val QUIT_DIALOG_DISCARD_TEXT = "quit_dialog_discard_text"
    const val QUIT_DIALOG_KEEP_EDITING_TEXT = "quit_dialog_keep_editing_text"
    const val QUIT_DIALOG_SAVE_AS_DRAFT_TEXT = "quit_dialog_save_as_draft_text"
    const val QUIT_DIALOG_TITLE = "quit_dialog_title"
    const val QUIT_DIALOG_EXPLANATION = "quit_dialog_explanation"
    const val FINALIZE_BUTTON_TEXT = "finalize_button_text"

    // Set this variable the same as applicationId
    const val APP_PROVIDER = "com.example.odkshellapp"
}
