package com.pavellukyanov.themartian.utils.helpers

import com.pavellukyanov.themartian.utils.C.DB_NAME

class DatabaseHelper : Helper() {
    private val db = context.getDatabasePath(DB_NAME)

    fun canRead(): Boolean = db.canRead()

    fun getFolderPath(): String = context.filesDir.absolutePath.replace("files", "databases")
}