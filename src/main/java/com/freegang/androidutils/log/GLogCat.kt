package com.freegang.androidutils.log

import android.app.Application
import android.util.Log
import com.freegang.freely.utils.io.forceDelete
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/// 改由Kotlin实现
class GLogCat {
    private var tag: String = "GLogCat"
    private var maxBorderSize = 64
    private var showTitle = false
    private var showDivider = false
    private var saveToLocal = false
    private var silence = false

    // border
    private var topBorderStart: Char = '╭'
    private var topBorderEnd: Char = '╮'
    private var bottomBorderStart: Char = '╰'
    private var bottomBorderEnd: Char = '╯'

    private var borderBar: Char = '│'
    private var borderStart: Char = '├'
    private var borderEnd: Char = '┤'
    private var borderSolid: Char = '─'
    private var borderDotted: Char = '┄'

    // symbol char
    private var aggravateChar: Char = '•'
    private var filledCircularChar: Char = '●'
    private var outlineCircularChar: Char = '○'

    /**
     * logcat tag
     */
    fun setTag(tag: String) {
        this.tag = tag
    }

    /**
     * max border size
     */
    fun setMaxBorderSize(size: Int) {
        this.maxBorderSize = size
    }

    /**
     * open logcat title
     * will print 'tag' and 'level`
     */
    fun showTitle() {
        this.showTitle = true
    }

    /**
     * open logcat middle border
     * it may be called `divider line`
     */
    fun showDivider() {
        this.showDivider = true
    }

    /**
     * open logcat sava to storage
     *
     * will be saved in an application private directory
     *
     * @param
     */
    fun saveToLocal() {
        this.saveToLocal = true
    }

    /**
     * print logcat
     */
    private fun println(priority: Int, tag: String, vararg msg: String) {
        /// silence mode
        if (silence) return

        //max length string
        val maxReduce = msg.reduce { acc, s -> if (acc.length > s.length) acc else s }

        // border builder
        val border = if (maxReduce.length >= maxBorderSize) {
            maxBorderSize.forCalc(0, "") { "$it$borderDotted" }
        } else {
            maxReduce.map { borderSolid }.joinToString("")
        }
        val divider = if (maxReduce.length >= 64) {
            maxBorderSize.forCalc(0, "") { "$it$borderDotted" }
        } else {
            maxReduce.map { borderDotted }.joinToString("")
        }


        //final border
        val topBorder = "$topBorderStart$border"
        val contentLeftBorder = "$borderStart$borderSolid"
        val bottomBorder = "$bottomBorderStart$border"

        /// print Log
        //top border
        Log.println(priority, tag, topBorder)
        //title
        if (showTitle) {
            Log.println(priority, tag, "$borderBar $tag $borderSolid Level[${getLevelString(priority)}]")
            Log.println(priority, tag, "$borderStart$border")
        }
        //content
        msg.forEach {
            writeToStorage(priority, tag, it)
            Log.println(priority, tag, "$contentLeftBorder$it")
            //middle border
            if (msg[msg.lastIndex] != it && showDivider) {
                Log.println(priority, tag, "$contentLeftBorder$divider")
            }
        }
        //bottom border
        Log.println(priority, tag, bottomBorder)
    }

    /**
     * print logcat to file (write)
     */
    private fun writeToStorage(priority: Int, tag: String, msg: String) {
        if (!saveToLocal) return
        val application = application ?: return

        try {
            val appName = application.resources.getString(application.applicationInfo.labelRes)
            val logFile = File(getLocalLogPath(), "${appName}_".plus(dateFormat.format(Calendar.getInstance().time)).plus(".log"))
            if (!logFile.exists()) logFile.createNewFile()

            FileWriter(logFile, true).use {
                it.append("[")
                it.append("tag=${tag}, ")
                it.append("level=${getLevelString(priority)}, ")
                it.append("time=")
                it.append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.getDefault()).format(Calendar.getInstance().time))
                it.append("]: ")
                it.append(msg)
                it.append("\n")
            }
        } catch (e: Exception) {
            Log.e(tag, "GLogCat Error: ${e.message}")
        }
    }

    /**
     * get logcat Level.
     * for example: d == Debug, i == Info
     */
    private fun getLevelString(priority: Int): String {
        return when (priority) {
            Log.VERBOSE -> "Verbose"
            Log.DEBUG -> "Debug"
            Log.INFO -> "Info"
            Log.WARN -> "Warn"
            Log.ERROR -> "Error"
            Log.ASSERT -> "Assert"
            else -> "Unknown"
        }
    }

    /**
     * static
     */
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        private val instance = GLogCat()
        private var application: Application? = null

        /**
         * init
         */
        fun init(application: Application, block: GLogCat.() -> Unit) {
            this.application = application
            block.invoke(instance)
        }

        /**
         * VERBOSE = 2
         */
        fun v(msg: String) {
            instance.println(Log.VERBOSE, instance.tag, msg)
        }

        fun v(vararg msg: String) {
            instance.println(Log.VERBOSE, instance.tag, *msg)
        }

        /**
         * DEBUG = 3
         */
        fun d(msg: String) {
            instance.println(Log.DEBUG, instance.tag, msg)
        }

        fun d(vararg msg: String) {
            instance.println(Log.DEBUG, instance.tag, *msg)
        }

        /**
         * INFO = 4
         */
        fun i(msg: String) {
            instance.println(Log.INFO, instance.tag, msg)
        }

        fun i(vararg msg: String) {
            instance.println(Log.INFO, instance.tag, *msg)
        }

        /**
         * WARN = 5
         */
        fun w(msg: String) {
            instance.println(Log.WARN, instance.tag, msg)
        }

        fun w(vararg msg: String) {
            instance.println(Log.WARN, instance.tag, *msg)
        }

        /**
         * ERROR = 6
         */
        fun e(msg: String) {
            instance.println(Log.ERROR, instance.tag, msg)
        }

        fun e(vararg msg: String) {
            instance.println(Log.ERROR, instance.tag, *msg)
        }

        /**
         * clear local logcat files
         */
        fun clearLocalLog() {
            getLocalLogPath()?.forceDelete()
        }

        /**
         * read the logcat in the file
         * if the log file exists
         *
         * @param date need date
         */
        fun readLocalLog(date: Date): String {
            val application = application ?: return "read fail, application is null."

            val appName = application.resources.getString(application.applicationInfo.labelRes)
            val logFile = File(getLocalLogPath(), "${appName}_".plus(dateFormat.format(date)).plus(".log"))
            if (!logFile.exists()) return "read fail, file `${logFile.name}` non-existent."

            val reader = FileReader(logFile)
            return reader.readText()
        }

        /**
         * logcat local dir
         */
        fun getLocalLogPath(): File? {
            return application?.getExternalFilesDir("logs")
        }

        /**
         * silent mode, no logcat output
         */
        fun silence() {
            instance.silence = true
        }
    }
}
