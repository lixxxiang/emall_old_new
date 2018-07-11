package com.example.emall_core.app

import com.orhanobut.logger.Logger
import java.util.*


/**
 * Created by lixiang on 2018/1/22.
 */
class Configurator {
    val EMALL_CONFIGS: HashMap<String, Any> = HashMap()

    init {
        EMALL_CONFIGS.put(ConfigKeys.CONFIG_READY.toString(), false)
        EMALL_CONFIGS.put(ConfigKeys.HANDLER.toString(), ConfigKeys.HANDLER)

    }
//
//    private object Holder {
//        val INSTANCE = Configurator()
//    }
//
//    fun configure() {
//        Logger.init()
//        EMALL_CONFIGS.put(ConfigKeys.CONFIG_READY.toString(), true)
//    }
//
//    fun getInstance(): Configurator {
//        return Holder.INSTANCE
//    }
//
//    fun getEmallConfigs(): HashMap<String, Any> {
//        return EMALL_CONFIGS
//    }
//
//    fun checkConfiguration() {
//        val isReady = EMALL_CONFIGS[ConfigKeys.CONFIG_READY.toString()] as Boolean
//        if (!isReady) {
//            throw RuntimeException("Configuration is not ready,call configure")
//        }
//    }
//
//    fun <T> getConfiguration(key: Any): T? {
//        checkConfiguration()
//        return EMALL_CONFIGS[key] as T?
//    }
}