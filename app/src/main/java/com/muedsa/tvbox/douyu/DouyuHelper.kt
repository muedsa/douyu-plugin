package com.muedsa.tvbox.douyu

import com.muedsa.js.lexer.Lexer
import com.muedsa.js.parser.Parser
import com.muedsa.js.runtime.Interpreter
import com.muedsa.js.runtime.VariableKind
import com.muedsa.js.runtime.initBrowserEnv
import com.muedsa.js.runtime.value.JSNativeFunction
import com.muedsa.js.runtime.value.JSObject
import com.muedsa.js.runtime.value.JSString
import com.muedsa.tvbox.api.store.IPluginPerfStore
import com.muedsa.tvbox.douyu.model.EncryptionInfo
import com.muedsa.tvbox.douyu.model.WebEncryptAuth
import com.muedsa.tvbox.douyu.service.DouyuService
import com.muedsa.tvbox.tool.LenientJson
import com.muedsa.tvbox.tool.md5
import timber.log.Timber

object DouyuHelper {

    val eval = JSNativeFunction(name = "eval") { interpreter, _, args ->
        var code = interpreter.getPrimitiveString(args[0])
        Timber.d("code=\n${code}")
        code = code.trimEnd(';')
        val tokens = Lexer(code).tokenize()
        val parser = Parser(tokens)
        val statements = parser.parse()
        interpreter.interpret(statements)
    }

    val md5 = JSNativeFunction(name = "md5") { interpreter, _, args ->
        val text = interpreter.getPrimitiveString(args[0])
        JSString(text.md5().toHexString())
    }

    val cryptoJS = JSObject(
        mutableMapOf("MD5" to md5)
    )

    fun buildSignParams(
        js: String,
        rid: Long,
        did: String,
    ): Map<String, String> {
        val interpreter = Interpreter()
            .initBrowserEnv()
            .also {
                it.getGlobalEnv().define("eval", eval, VariableKind.CONST)
                it.getGlobalEnv().define("CryptoJS", cryptoJS, VariableKind.CONST)
            }
        val tokens = Lexer(js).tokenize()
        val parser = Parser(tokens)
        val statements = parser.parse()
        interpreter.interpret(statements)

        val ts: Long = System.currentTimeMillis() / 1000
        val code2 = "ub98484234('${rid}','${did}','${ts}')"
        val tokens2 = Lexer(code2).tokenize()
        val statements2 = Parser(tokens2).parse()
        val result = interpreter.interpret(statements2).toPrimitiveString()
        val signMap = result.split("&").associate {
            val (k, v) = it.split("=")
            k to v
        }
        return signMap
    }

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