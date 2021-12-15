package com.example.servi2.coneccion

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class ConexionBD(context: Context) : SQLiteOpenHelper(context, "utez.db", null, 1){


    val CHAT = "create TABLE IF NOT EXISTS chat(" +
            "id integer primary key autoincrement, " +
            "user text," +
            "message text," +
            "leido integer)"

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(CHAT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun registerData(nombreTabla: String, datos: ContentValues): Boolean {
        val db = this.writableDatabase
        try {
            db.insert(nombreTabla, null, datos)
            return true
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    fun getData(nombreTabla: String, parametros:Array<String>):Cursor{
        val db = this.writableDatabase

        return db.query(nombreTabla, parametros, null,null ,null,null,null)

    }

    fun deleteData(nombreTabla:String):Boolean{
        val db = this.writableDatabase
        return try {
            db.execSQL("delete  from "+nombreTabla)
            true
        }catch (e:SQLException){
            false
        }
    }
}