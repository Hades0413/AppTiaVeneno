package com.example.apptiaveneno.Data

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.apptiaveneno.utils.appConfig

class InitBD : SQLiteOpenHelper(appConfig.CONTEXT, appConfig.BD_NAME, null, appConfig.VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla
        db.execSQL("create table tb_testimonios(" +
                "cod integer primary key autoincrement," +
                "nomusu varchar(30)," +
                "testimonio varchar(255)," +
                "fotousu varchar(30))")

        // Insertar registros con testimonios
        db.execSQL("insert into tb_testimonios values(null, 'Señora Carmella', 'Estoy muy contento con la app Tia Veneno. Es muy fácil de usar y la comida es excelente.', 'carmella')")
        db.execSQL("insert into tb_testimonios values(null, 'Ricardo', 'La aplicación Tia Veneno es increíble. Todo es sencillo de encontrar y la comida es deliciosa.', 'ricardo')")
        db.execSQL("insert into tb_testimonios values(null, 'Seño Meche', 'Me encanta la app Tia Veneno. Es muy intuitiva y la calidad de la comida es insuperable.', 'meche')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }


}