package com.example.emall_core.app

/**
 * Created by lixiang on 2018/1/29.
 */
import android.content.Context
import android.os.Handler
import java.util.*



/**
 * Created by lixiang on 2018/1/22.
 */
final class Emall{
//    fun init(context: Context): Configurator {
//        getConfigurations().put(ConfigKeys.APPLICATION_CONTEXT.toString(), context.applicationContext)
//        return Configurator().getInstance()
//    }

    fun init(context: Context): Context {
        return context.applicationContext
    }
////
//    fun getConfigurations(): HashMap<String, Any>{
//        return Configurator().getInstance().getEmallConfigs()
//    }
////
//    fun <T> getConfiguration(key: Any): T? {
//        return Configurator().getInstance().getConfiguration(key)
//    }
////
//    fun getApplicationContext(): Context? {
//        return Configurator().getInstance().getConfiguration(ConfigKeys.APPLICATION_CONTEXT)
//    }
////
////    /**
////     * name???
////     */
//    fun getApplicationContextName(): Context? {
//        return Configurator().getInstance().getConfiguration(ConfigKeys.APPLICATION_CONTEXT.name)
//    }
////
//    fun getApplication():Context{
//        return getConfigurations()[ConfigKeys.APPLICATION_CONTEXT.name] as Context
//    }
////
//    fun getHandler(): Handler? {
//        return Configurator().getInstance().getConfiguration(ConfigKeys.HANDLER.name)
//    }
}