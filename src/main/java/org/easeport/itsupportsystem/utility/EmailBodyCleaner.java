package org.easeport.itsupportsystem.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailBodyCleaner {
    private static final Pattern REPLY_CUT_PATTERN = Pattern.compile(
            "(?s)(?<=\\r?\\n\\r?\\n)(" +
                    "On .*wrote:|" +                // English
                    "Den .*skrev|" +                // Danish/Norwegian
                    "Le .*a écrit|" +               // French
                    "Am .*schrieb|" +               // German
                    "El .*escribió|" +              // Spanish
                    "Il .*ha scritto|" +            // Italian
                    "Op .*schreef|" +               // Dutch
                    "於 .*寫道|" +                    // Chinese
                    "В .*писал|" +                  // Russian
                    ".*<.*@.*>|" +                  // any email header line
                    ">\\s*" +                       // quoted text marker
                    ").*"
    );

    public static String clean(String body) {
        if (body == null || body.isBlank()) return body;

        Matcher matcher = REPLY_CUT_PATTERN.matcher(body);
        if (matcher.find()) {
            return body.substring(0, matcher.start()).trim();
        }

        // fallback cleanup – remove trailing signatures like "--"
        int sigIndex = body.indexOf("\n--");
        if (sigIndex > 0) {
            body = body.substring(0, sigIndex).trim();
        }

        return body.trim();
    }
}
