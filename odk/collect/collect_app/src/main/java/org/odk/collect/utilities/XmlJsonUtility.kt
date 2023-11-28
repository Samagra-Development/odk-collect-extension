package org.odk.collect.utilities

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.IOException
import java.io.StringReader

object XmlJsonUtility {

    @Throws(XmlPullParserException::class, IOException::class, JSONException::class)
    fun convertToJson(xml: String?): JSONObject {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))
        val jsonObject = JSONObject()
        var jsonArray = JSONArray()
        var tagName = ""
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> {}
                XmlPullParser.START_TAG -> {
                    tagName = parser.name
                    if (jsonObject.has(tagName)) {
                        // If the tag already exists in the object, convert it to an array
                        val existingValue = jsonObject[tagName]
                        if (existingValue is JSONArray) {
                            jsonArray = existingValue
                        } else {
                            jsonArray = JSONArray()
                            jsonArray.put(existingValue)
                            jsonObject.put(tagName, jsonArray)
                        }
                    }
                }
                XmlPullParser.TEXT -> if (tagName.isNotEmpty()) {
                    jsonArray.put(parser.text)
                }
                XmlPullParser.END_TAG -> {
                    if (tagName.isNotEmpty()) {
                        if (jsonArray.length() > 0) {
                            jsonObject.put(tagName, jsonArray)
                            jsonArray = JSONArray()
                        } else {
                            jsonObject.put(tagName, "")
                        }
                    }
                    tagName = ""
                }
            }
            eventType = parser.next()
        }
        return jsonObject
    }

    @Throws(IOException::class)
    fun convertToJson(xmlFile: File): JSONObject {
        val xmlString = xmlFile.readText()
        return convertToJson(xmlString)
    }
}
