package com.muedsa.tvbox.douyu

import com.muedsa.js.lexer.Lexer
import com.muedsa.js.parser.Parser
import com.muedsa.js.runtime.Interpreter
import com.muedsa.js.runtime.VariableKind
import com.muedsa.js.runtime.initBrowserEnv
import com.muedsa.js.runtime.value.JSNativeFunction
import com.muedsa.js.runtime.value.JSObject
import com.muedsa.js.runtime.value.JSString
import com.muedsa.tvbox.douyu.service.DouyuService
import com.muedsa.tvbox.tool.LenientJson
import com.muedsa.tvbox.tool.createJsonRetrofit
import com.muedsa.tvbox.tool.createOkHttpClient
import com.muedsa.tvbox.tool.md5
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DemoTest {

    val douyuService = createJsonRetrofit(
        baseUrl = "https://www.douyu.com/",
        service = DouyuService::class.java,
        okHttpClient = createOkHttpClient(debug = true)
    )

    val eval = JSNativeFunction(name = "eval") { interpreter, _, args ->
        var code = interpreter.getPrimitiveString(args[0])
        println("eval: $code")
        code = code.removeSuffix(";;;;")
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

    val interpreter = Interpreter()
        .initBrowserEnv()
        .also {
            it.getGlobalEnv().define("eval", eval, VariableKind.CONST)
            it.getGlobalEnv().define("CryptoJS", cryptoJS, VariableKind.CONST)
        }

    @Test
    fun demo() = runTest {
        val rid: Long = 601514
        val resp = douyuService.homeH5Enc(rids = rid)
        check(resp.error == 0)
        checkNotNull(resp.data)
        val code = resp.data["room${rid}"]
        checkNotNull(code)
        // println(code)
        val tokens = Lexer(code).tokenize()
        val parser = Parser(tokens)
        val statements = parser.parse()
        interpreter.interpret(statements)

        val ts: Long = System.currentTimeMillis() / 1000
        val code2 = "ub98484234('${rid}','${DouyuConst.DID}','${ts}')"
        val tokens2 = Lexer(code2).tokenize()
        val statements2 = Parser(tokens2).parse()
        val result = interpreter.interpret(statements2).toPrimitiveString()
        // println(result)
        // println(interpreter.getCurrentEnv().dumpEnv())
        val signMap = result.split("&").associate {
            val (k, v) = it.split("=")
            k to v
        }
        val resp2 = douyuService.getH5Play(rid, signMap, rate = 1)
        println(LenientJson.encodeToString(resp2))
    }

    @Test
    fun part1() = runTest {
        val code = """
        function ub98484234(xx0, xx1, xx2) {
          var cb = xx0 + xx1 + xx2 + "220120260615";
          var rb = CryptoJS.MD5(cb).toString();
          var re = [];
          for (var i = 0; i < rb.length / 8; i++)
            re[i] =
              (parseInt(rb.substr(i * 8, 2), 16) & 0xff) |
              ((parseInt(rb.substr(i * 8 + 2, 2), 16) << 8) & 0xff00) |
              ((parseInt(rb.substr(i * 8 + 4, 2), 16) << 24) >>> 8) |
              (parseInt(rb.substr(i * 8 + 6, 2), 16) << 24);
          var k2 = [0x7067f1c4, 0x358598a4, 0x15b520, 0x7e17ed4c];
          for (var I = 0; I < 2; I++) {
            var v0 = re[I * 2],
              v1 = re[I * 2 + 1],
              sum = 0,
              i = 0;
            var delta = 0x9e3779b9;
            for (i = 0; i < 32; i++) {
              sum += delta;
              v0 += ((v1 << 4) + k2[0]) ^ (v1 + sum) ^ ((v1 >>> 5) + k2[1]);
              v1 += ((v0 << 4) + k2[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + k2[3]);
            }
            re[I * 2] = v0;
            re[I * 2 + 1] = v1;
          }
          re[0] -= k2[0];
          re[0] += k2[2];
          re[0] -= k2[2];
          re[1] += k2[1];
          re[1] = (re[1] >>> (k2[3] % 16)) | (re[1] << (32 - (k2[3] % 16)));
          re[1] = (re[1] >>> (k2[1] % 16)) | (re[1] << (32 - (k2[1] % 16)));
          re[1] += k2[3];
          re[2] -= k2[0];
          re[2] = (re[2] << (k2[2] % 16)) | (re[2] >>> (32 - (k2[2] % 16)));
          re[2] = (re[2] << (k2[2] % 16)) | (re[2] >>> (32 - (k2[2] % 16)));
          re[3] = (re[3] << (k2[1] % 16)) | (re[3] >>> (32 - (k2[1] % 16)));
          re[3] = (re[3] << (k2[3] % 16)) | (re[3] >>> (32 - (k2[3] % 16)));
          re[3] = (re[3] << (k2[1] % 16)) | (re[3] >>> (32 - (k2[1] % 16)));
          re[3] -= k2[3];
          re[0] -= k2[0];
          re[0] += k2[2];
          re[0] -= k2[2];
          re[0] -= k2[2];
          re[1] ^= k2[1];
          re[1] ^= k2[3];
          re[1] = (re[1] << (k2[3] % 16)) | (re[1] >>> (32 - (k2[3] % 16)));
          re[2] += k2[0];
          re[2] = (re[2] << (k2[2] % 16)) | (re[2] >>> (32 - (k2[2] % 16)));
          re[2] += k2[2];
          re[2] = (re[2] << (k2[2] % 16)) | (re[2] >>> (32 - (k2[2] % 16)));
          re[3] ^= k2[1];
          re[3] = (re[3] << (k2[3] % 16)) | (re[3] >>> (32 - (k2[3] % 16)));
          re[3] = (re[3] >>> (k2[3] % 16)) | (re[3] << (32 - (k2[3] % 16)));
          {
            var hc = "0123456789abcdef".split("");
            console.log(hc.length);
            for (var i = 0; i < re.length; i++) {
              var j = 0,
                s = "";
              for (; j < 4; j++)
                s += hc[(re[i] >> (j * 8 + 4)) & 15] + hc[(re[i] >> (j * 8)) & 15];
              re[i] = s;
            }
            re = re.join("");
          }
          var rt = "v=220120260615" + "&did=" + xx1 + "&tt=" + xx2 + "&sign=" + re;
          return rt;
        }
        ub98484234('601514','${DouyuConst.DID}','1781510403');
        """.trimIndent()
        val tokens = Lexer(code).tokenize()
        val parser = Parser(tokens)
        val statements = parser.parse()
        val result = interpreter.interpret(statements).toPrimitiveString()
        println(result)
        println(interpreter.getCurrentEnv().dumpEnv())
    }
}