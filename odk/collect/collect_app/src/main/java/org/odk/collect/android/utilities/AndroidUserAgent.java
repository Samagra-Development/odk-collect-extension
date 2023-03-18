package org.odk.collect.android.utilities;

import org.odk.collect.android.BuildConfig;
import org.odk.collect.utilities.UserAgentProvider;

public final class AndroidUserAgent implements UserAgentProvider {

    @Override
    public String getUserAgent() {
        //TODO pass from Apps
        return String.format("%s/%s %s",
                "org.odk.collect.collect_app",
                1,
                System.getProperty("http.agent"));
    }

}
