package com.freegang.androidutils.text

/**
 * GTextUtils Extension
 */

/// 是否为 null or empty
fun CharSequence.isNullStringOrEmpty(): Boolean {
    return GTextUtils.isEmpty(this)
}