package com.muedsa.tvbox.douyu

import com.muedsa.tvbox.api.store.IPluginPerfStore
import com.muedsa.tvbox.douyu.model.EncryptionInfo
import com.muedsa.tvbox.douyu.model.WebEncryptAuth
import com.muedsa.tvbox.douyu.service.DouyuService
import com.muedsa.tvbox.tool.LenientJson
import com.muedsa.tvbox.tool.md5

object DouyuHelper {

    suspend fun getEncryptionInfo(
        did: String,
        douyuService: DouyuService,
        store: IPluginPerfStore
    ): EncryptionInfo {
        var encryptionInfo: EncryptionInfo? = null
        val encryptionInfoJson = store.get(PERF_KEY_ENCRYPTION_INFO)
        if (!encryptionInfoJson.isNullOrEmpty()) {
            encryptionInfo = LenientJson.decodeFromString<EncryptionInfo>(encryptionInfoJson)
            val now: Long = System.currentTimeMillis() / 1000
            if (encryptionInfo.expireAt < now) {
                encryptionInfo = null
            }
        }
        if (encryptionInfo == null) {
            val resp = douyuService.getEncryption(did)
            encryptionInfo = resp.data ?: throw RuntimeException("获取签名加密key失败${resp.msg ?: ""}")
            store.update(PERF_KEY_ENCRYPTION_INFO, LenientJson.encodeToString(encryptionInfo))
        }
        return encryptionInfo
    }

    fun encryptAuth(
        encryptionInfo: EncryptionInfo,
        type: String,
        rid: Long,
        did: String,
        ts: Long? = null,
        randStr: String? = null,
        encTime: Int? = null,
    ): WebEncryptAuth {
        var o = ""
        var key = ""
        var keyVer = ""
        val tt: Long = ts ?: (System.currentTimeMillis() / 1000)
        when(type) {
            "stream" -> {
                o = "${rid}${tt}"
                key = encryptionInfo.key
            }
            "login" -> {
                o = "${rid}${did}${tt}"
                key = encryptionInfo.cpp.danmu.key
                keyVer = encryptionInfo.cpp.danmu.keyVer
            }
            "heartbeat" -> {
                o = "${rid}${did}${tt}"
                key = encryptionInfo.cpp.heartbeat.key
                keyVer = encryptionInfo.cpp.heartbeat.keyVer
            }
        }
        var u = randStr ?: encryptionInfo.randStr
        var i = 0
        val time = encTime ?: encryptionInfo.encTime
        while (i < time) {
            u = "${u}${key}".md5().toHexString()
            i++
        }
        u = "${u}${key}${o}".md5().toHexString()
        return WebEncryptAuth(
            key = encryptionInfo,
            algVer = "1.0",
            keyVer = keyVer,
            auth = u,
            ts = tt,
        )
    }

    fun buildWebStreamSignParams(
        encryptionInfo: EncryptionInfo,
        rid: Long,
        did: String,
        ts: Long,
    ): Map<String, String> {
        val encryptAuth = encryptAuth(
            encryptionInfo = encryptionInfo,
            type = "stream",
            rid = rid,
            did = did,
            ts = ts,
        )
        return mapOf(
            "enc_data" to encryptAuth.key.encData,
            "tt" to "${encryptAuth.ts}",
            "did" to did,
            "auth" to encryptAuth.auth
        )
    }

}