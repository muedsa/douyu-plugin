package com.muedsa.tvbox.douyu

import okio.Buffer
import okio.ByteString

object DanmakuProtocol {

    fun pack(message: String): ByteString {
        val msgBytes = message.toByteArray(Charsets.UTF_8)
        val packetLen = msgBytes.size + 9
        val buffer = Buffer()
        // 小端序写入包头
        buffer.writeIntLe(packetLen)       // 0~3: 长度1
        buffer.writeIntLe(packetLen)       // 4~7: 长度2（副本）
        buffer.writeShortLe(689)           // 8~9: 客户端消息类型 689
        buffer.writeByte(0)                // 10: 加密标记 0
        buffer.writeByte(0)                // 11: 保留位 0
        // 写入消息体
        buffer.write(msgBytes)
        // 必须的末尾 0x00 终止符
        buffer.writeByte(0)
        return buffer.readByteString()
    }

    fun unpack(bytes: ByteString): Map<String, String> {
        if (bytes.size < 13) return emptyMap()
        val contentBytes = bytes.substring(12, bytes.size - 1)
        val content = contentBytes.utf8()

        val result = mutableMapOf<String, String>()
        for (item in content.split('/')) {
            if (item.isEmpty()) continue
            val parts = item.split("@=", limit = 2)
            if (parts.size != 2) continue
            // 还原斗鱼转义字符
            val value = parts[1]
                .replace("@S", "/")
                .replace("@A", "@")
                .replace("@G", "=")
            result[parts[0]] = value
        }
        return result
    }
}