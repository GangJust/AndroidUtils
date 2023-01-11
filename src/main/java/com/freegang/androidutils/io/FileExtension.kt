package com.freegang.androidutils.io

import java.io.File


/**
 * 删除所有内容, 如果文件夹不为空, 则递归遍历删除其子项, 直到将它自身删除结束
 */

fun File.forceDelete() {
    //文件直接删除
    if (this.isFile) {
        this.delete()
        return
    }
    //遍历删除文件夹
    this.listFiles()?.forEach {
        if (it.isFile) it.delete()
        if (it.isDirectory) it.forceDelete()
    }
    //删除文件夹本身
    this.delete()
}
