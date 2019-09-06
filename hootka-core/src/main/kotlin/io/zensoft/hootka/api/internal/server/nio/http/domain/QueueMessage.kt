package io.zensoft.hootka.api.internal.server.nio.http.domain

class QueueMessage(
        private val bufferCapacity: Int,
        private val messageList: MutableList<ByteArray> = mutableListOf(),
        private var messageBuffer: ByteArray = ByteArray(bufferCapacity),
        private var caret: Int = 0,
        var size: Int = 0
) {

    fun put(byte: Byte) {
        if (bufferCapacity - 1 == caret) {
            messageBuffer[caret] = byte
            messageList.add(messageBuffer)
            messageBuffer = ByteArray(bufferCapacity)
            size++
            caret = 0
        } else {
            messageBuffer[caret] = byte
            caret++
            size++
        }
    }

    fun readMessage(): ByteArray {
        if (bufferCapacity - 1 != caret) {
            messageList.add(messageBuffer.copyOf(caret))
            messageBuffer = ByteArray(bufferCapacity)
            caret = 0
        }
        val message = ByteArray(size)
        size = 0
        while (messageList.isNotEmpty()) {
            val mes = messageList.first()
            mes.forEach { element ->
                message[size] = element
                size++
            }
            messageList.remove(mes)
        }
        size = 0
        return message
    }

}
