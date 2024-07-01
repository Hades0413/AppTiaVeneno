package com.example.apptiaveneno.utils

import android.app.Application
import android.content.Context
import com.example.apptiaveneno.Data.InitBD

class appConfig:Application() {


    companion object{
        lateinit var CONTEXT:Context
        lateinit var BD:InitBD
        var BD_NAME="AppTiaVeneno.bd"
        var VERSION=1
    }
    override fun onCreate() {
        super.onCreate()
        CONTEXT =applicationContext
        BD=InitBD()
    }
}