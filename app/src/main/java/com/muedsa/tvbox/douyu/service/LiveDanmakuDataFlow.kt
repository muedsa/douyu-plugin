package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.DanmakuData
import com.muedsa.tvbox.api.data.DanmakuDataFlow
import com.muedsa.tvbox.douyu.DanmakuProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.Buffer
import okio.ByteString
import timber.log.Timber
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class LiveDanmakuDataFlow(
    val roomId: Long,
    request: Request,
    okHttpClient: OkHttpClient,
) : DanmakuDataFlow {

    override val flow = MutableSharedFlow<DanmakuData>()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var closed: Boolean = false

    private val listener = object : WebSocketListener() {
        private val receiveBuffer = Buffer()

        override fun onOpen(webSocket: WebSocket, response: Response) {
            receiveBuffer.clear()
            webSocket.send(DanmakuProtocol.pack("type@=unsub/mt@=dyh_legend_subscribe/"))
            webSocket.send(DanmakuProtocol.pack("type@=loginreq/roomid@=${roomId}/"))
            heartbeat()
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // 把新收到的数据写入缓冲区末尾
            receiveBuffer.write(bytes)
            // 循环解析缓冲区中的所有完整包
            while (receiveBuffer.size >= 13) {
                // 预读前4字节获取包长度，不消耗缓冲区数据
                val packetLen = receiveBuffer.peek().readIntLe()
                val totalFullPacketLen = packetLen + 4 // 完整包总长度（包含前4字节）

                // 缓冲区数据不足一个完整包 → 等待下一次数据
                if (receiveBuffer.size < totalFullPacketLen) {
                    break
                }

                // 读取完整的整个数据包（包含前4字节）
                val fullPacket = receiveBuffer.readByteString(totalFullPacketLen.toLong())

                val msg = DanmakuProtocol.unpack(fullPacket)
                if (msg.isNotEmpty()) {
                    handleMsg(msg)
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            closed = true
            receiveBuffer.clear()
            flow.tryEmit(
                DanmakuData(
                    danmakuId = Random.nextLong(),
                    position = -1,
                    content = "弹幕流断开",
                    textColor = 0xFF_00_00,
                    mode = 4,
                )
            )
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            closed = true
            receiveBuffer.clear()
            flow.tryEmit(
                DanmakuData(
                    danmakuId = Random.nextLong(),
                    position = -1,
                    content = "弹幕流意外断开, ${t.message}",
                    textColor = 0xFF_00_00,
                    mode = 4,
                )
            )
        }
    }

    private val webSocket = okHttpClient.newWebSocket(request = request, listener = listener)

    override fun close() {
        if (!closed) {
            webSocket.send(LOGOUT_PACK)
            Timber.d("live room $roomId WebSocket request close")
            // webSocket.close(code = 1001, reason = null)
            closed = true
        }
    }

    private fun heartbeat() {
        coroutineScope.launch {
            while (!closed) {
                webSocket.send(HEARTBEAT_PACK)
                delay(HEARTBEAT_DELAY)
            }
        }
    }

    private fun handleMsg(msg: Map<String, String>) {
        val type = msg["type"]
        when (type) {
            "loginres" -> {
                webSocket.send(DanmakuProtocol.pack("type@=joingroup/rid@=${roomId}/gid@=1/"))
            }

            "chatmsg" -> {
//                val nickname = msg["nn"] ?: "unknown"
                val content = msg["txt"]
                val col = msg["col"] ?: "0"
                val hc = msg["hc"]
                val dms = msg["dms"] ?: "4"
                content?.let {
                    coroutineScope.launch {
                        flow.emit(
                            DanmakuData(
                                danmakuId = Random.nextLong(),
                                position = -1,
                                content = it,
                                mode = MODE_MAP[dms] ?: 1,
                                textColor = getColor(col = col, hc = hc),
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        val HEARTBEAT_DELAY = 40.seconds
        val HEARTBEAT_PACK = DanmakuProtocol.pack("type@=mrkl/")
        val LOGOUT_PACK = DanmakuProtocol.pack("type@=logout/")

        val COLOR_MAP: Map<String, Int> = mapOf(
            "0" to 0xFFFFFF,
            "1" to 0xE53935,
            "2" to 0x4CAF50,
            "3" to 0x2196F3,
            "4" to 0x9C27B0,
            "5" to 0x00BCD4,
            "6" to 0x00BCD4,
            "7" to 0xFF9800,
        )

        val MODE_MAP: Map<String, Int> = mapOf(
            "4" to 1,
            "5" to 4,
            "6" to 5,
        )

        fun getColor(col: String, hc: String? = null): Int {
            return if (!hc.isNullOrEmpty()) {
                parseNobleColor(hc)
            } else {
                COLOR_MAP[col] ?: 0xFFFFFF
            }

        }

        private fun parseNobleColor(hc: String): Int {
            if (hc.length != 8) return 0xFFFFFF
            return try {
                Integer.parseUnsignedInt(hc, 16)
            } catch (_: NumberFormatException) {
                0xFFFFFF
            }
        }
    }

}