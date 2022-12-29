package com.freegang.androidutils.json

import org.json.JSONArray
import org.json.JSONObject

/**
 * GJSONUtils Extension
 */

fun String.parse(): JSONObject {
    return GJSONUtils.parse(this)
}

fun String.parseArray(): JSONArray {
    return GJSONUtils.parseArray(this)
}

fun JSONObject.isEmpty(): Boolean {
    return GJSONUtils.isEmpty(this)
}

fun JSONObject.isNotEmpty(): Boolean {
    return GJSONUtils.isNotEmpty(this)
}

fun JSONArray.isEmpty(): Boolean {
    return GJSONUtils.isEmpty(this)
}

fun JSONArray.isNotEmpty(): Boolean {
    return GJSONUtils.isNotEmpty(this)
}