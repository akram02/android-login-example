package com.company.app

import android.app.Application
import android.content.Context
import com.greengrowapps.ggarest.GgaRest
import com.greengrowapps.jhiusers.JhiUsers
import com.greengrowapps.jhiusers.JhiUsersImpl
import com.company.app.core.CustomSerializer
import com.company.app.core.Core
import com.company.app.core.config.CoreConfiguration

import com.company.app.core.messaging.Messager
import com.company.app.core.messaging.MessagerGetListener
import com.company.app.core.messaging.MessagerFactory
import com.company.app.core.messaging.StrompMessagerFactory

class MyApplication : Application() {

    private lateinit var jhiUsers: JhiUsers

    private lateinit var core: Core

    private lateinit var config: CoreConfiguration


    private var messagerFactory: MessagerFactory? = null


    override fun onCreate() {
        super.onCreate()

        GgaRest.init(this)
        GgaRest.setSerializer(CustomSerializer())

        if(BuildConfig.DEBUG){
            config = CoreConfiguration("http://10.10.100.62:8080")
        }
        else{
            config = CoreConfiguration("https://jhipster.tech")
        }

        jhiUsers = JhiUsersImpl.with(this,config.serverUrl,true,getSharedPreferences("JhiUsers", Context.MODE_PRIVATE))
        core = Core(jhiUsers,config,getSharedPreferences("Core", Context.MODE_PRIVATE),CustomSerializer())

        messagerFactory = StrompMessagerFactory(config.serverUrl) { val token = jhiUsers.authToken; if (token.isNullOrEmpty()) { null } else {token}  }

    }

    fun getJhiUsers() : JhiUsers{
        return jhiUsers
    }

    fun getCore() : Core{
        return core
    }

    fun getMessager(listener: MessagerGetListener){
      messagerFactory?.instance(listener)
    }

}
