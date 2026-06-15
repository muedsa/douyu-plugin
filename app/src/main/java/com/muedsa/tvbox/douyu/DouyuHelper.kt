package com.muedsa.tvbox.douyu

import com.muedsa.js.lexer.Lexer
import com.muedsa.js.parser.Parser
import com.muedsa.js.runtime.Interpreter
import com.muedsa.js.runtime.VariableKind
import com.muedsa.js.runtime.initBrowserEnv
import com.muedsa.js.runtime.value.JSNativeFunction
import com.muedsa.js.runtime.value.JSObject
import com.muedsa.js.runtime.value.JSString
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

    fun buildSignParms(js: String, rid: Long): Map<String, String> {
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
        val code2 = "ub98484234('${rid}','${DouyuConst.DID}','${ts}')"
        val tokens2 = Lexer(code2).tokenize()
        val statements2 = Parser(tokens2).parse()
        val result = interpreter.interpret(statements2).toPrimitiveString()
        val signMap = result.split("&").associate {
            val (k, v) = it.split("=")
            k to v
        }
        return signMap
    }
}