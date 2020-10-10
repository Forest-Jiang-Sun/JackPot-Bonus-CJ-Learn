package com.aspectgaming.common.loader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aspectgaming.common.configuration.DisplayConfiguration;
import com.aspectgaming.common.configuration.GameConfiguration;

public class LoaderUtil {

    private static final Pattern patternResolution = Pattern.compile("@{resolution}", Pattern.LITERAL);
    private static final String resolution = getResolution();

    private static String getResolution() {
        DisplayConfiguration display = GameConfiguration.getInstance().display;
        return Matcher.quoteReplacement(display.width + "x" + display.height);
    }

    public static String filterPath(String name) {
        if (name.indexOf('@') >= 0) {
            return patternResolution.matcher(name).replaceFirst(resolution);
        }
        return name;
    }
}
