package com.freegang.androidutils.log;

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

/// 改由Kotlin实现
object GLogCat {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    private var tag: String = "GLogCat"
    private var application: Application? = null
    private var showTitle = false
    private var showMiddleBorder = false
    private var saveToStorage = false

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

    fun init(block: GLogCat.() -> Unit) {
        block.invoke(this)
    }

    fun setTag(tag: String) {
        this.tag = tag
    }

    /**
     * open logcat title
     * will print 'tag' and 'level`
     */
    fun openTitle() {
        this.showTitle = true
    }

    /**
     * open logcat middle border
     * it may be called `divider line`
     */
    fun openMiddleBorder() {
        this.showMiddleBorder = true
    }

    /**
     * open logcat sava to storage
     *
     * will be saved in an application private directory
     *
     * @param
     */
    fun openStorage(application: Application) {
        this.application = application
        this.saveToStorage = true
    }

    /**
     * VERBOSE = 2
     */
    fun v(msg: String) {
        println(Log.VERBOSE, tag, msg)
    }

    fun v(vararg msg: String) {
        println(Log.VERBOSE, tag, *msg)
    }

    /**
     * DEBUG = 3
     */
    fun d(msg: String) {
        println(Log.DEBUG, tag, msg)
    }

    fun d(vararg msg: String) {
        println(Log.DEBUG, tag, *msg)
    }

    /**
     * INFO = 4
     */
    fun i(msg: String) {
        println(Log.INFO, tag, msg)
    }

    fun i(vararg msg: String) {
        println(Log.INFO, tag, *msg)
    }

    /**
     * WARN = 5
     */
    fun w(msg: String) {
        println(Log.WARN, tag, msg)
    }

    fun w(vararg msg: String) {
        println(Log.WARN, tag, *msg)
    }

    /**
     * ERROR = 6
     */
    fun e(msg: String) {
        println(Log.ERROR, tag, msg)
    }

    fun e(vararg msg: String) {
        println(Log.ERROR, tag, *msg)
    }

    /**
     * print logcat
     */
    private fun println(priority: Int, tag: String, vararg msg: String) {
        //max length string
        val maxReduce = msg.reduce { acc, s -> if (acc.length > s.length) acc else s }
        //border
        val border = maxReduce.map { borderSolid }.joinToString("")
        val middleBorder = maxReduce.map { borderDotted }.joinToString("")
        val topBorder = "$topBorderStart$border"
        val contentLeftBorder = "$borderStart$borderSolid"
        val bottomBorder = "$bottomBorderStart$border"

        ///打印日志
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
            if (msg[msg.lastIndex] != it && showMiddleBorder) {
                Log.println(priority, tag, "$contentLeftBorder$middleBorder")
            }
        }
        //bottom border
        Log.println(priority, tag, bottomBorder)
    }

    /**
     * print logcat to file (write)
     */
    private fun writeToStorage(priority: Int, tag: String, msg: String) {
        if (!saveToStorage) return
        val application = application ?: return

        val appName = application.resources.getString(application.applicationInfo.labelRes)
        val logFile =
            File(getLogcatStoragePath(), "${appName}_".plus(dateFormat.format(Calendar.getInstance().time)).plus(".log"))
        if (!logFile.exists()) logFile.createNewFile()

        try {
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
            e.printStackTrace()
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
     * logcat dir
     */
    private fun getLogcatStoragePath(): File? {
        return application?.getExternalFilesDir("logs")
    }

    /**
     * read the logcat in the file
     * if the log file exists
     *
     * @param date need date
     */
    fun getStorageLogContent(date: Date): String {
        val application = application ?: return "read fail, application is null."

        val appName = application.resources.getString(application.applicationInfo.labelRes)
        val logFile = File(getLogcatStoragePath(), "${appName}_".plus(dateFormat.format(date)).plus(".log"))
        if (!logFile.exists()) return "read fail, file `${logFile.name}` non-existent."

        val reader = FileReader(logFile)
        return reader.readText()
    }
}

