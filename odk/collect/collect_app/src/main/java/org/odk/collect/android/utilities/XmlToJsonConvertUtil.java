package org.odk.collect.android.utilities;

import org.odk.collect.android.helpers.xmltojsonlib.src.main.java.fr.arnaudguyon.xmltojsonlib.XmlToJson;

import java.io.File;
import java.util.List;

/**
 * A Utility class used to convert an xml string to json formatted string.
 *
 *  @author Prabhav Chopra
 */
public class XmlToJsonConvertUtil {

    /** Takes a XML String and converts it into a formatted JSON String.
     Returns the formatted JSON String.
     */
    public static String convertToJson(String xmlString, File instanceFile) {

        List<File> files = ReadFileUtil.getFilesInParentDirectory(instanceFile);

        XmlToJson xmlToJson = new XmlToJson.Builder(xmlString)
                .setAttributeName("/data/id", "form_id")
                .setAttributeName("/data/version", "form_version")
                .skipAttribute("/xmlns:")
                .build();

        String formatted = xmlToJson.toFormattedString();

        if (files != null) {
            for (File file : files) {
                formatted = formatted.replace(file.getName(), file.getAbsolutePath());
            }
        }

        return formatted;
    }


}
