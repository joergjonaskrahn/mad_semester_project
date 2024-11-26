package org.dailydone.mobile.android.util;

import java.util.Locale;
import java.util.TimeZone;

public class Constants {
    // The alpha attribute in XML seems to only accepts @integer. Since alpha values are no
    // integer they cannot be defined as XML constants.
    public static float BUTTON_ENABLED_ALPHA = 1;
    public static float BUTTON_DISABLED_ALPHA = 0.4f;

    public static Locale LOCALE = Locale.getDefault();
    public static TimeZone TIMEZONE = TimeZone.getDefault();
}
