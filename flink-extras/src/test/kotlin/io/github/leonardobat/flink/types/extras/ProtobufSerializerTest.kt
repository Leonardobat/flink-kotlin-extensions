/**
 * Copyright 2024 Leonardobat
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.github.leonardobat.flink.types.extras

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.google.protobuf.MessageLite
import io.github.leonardobat.samples.SampleInputMessage
import io.github.leonardobat.samples.SampleOutputMessage
import io.github.leonardobat.samples.sampleInputMessage
import io.github.leonardobat.samples.sampleOutputMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.Test

class ProtobufSerializerTest {

    private lateinit var kryo: Kryo

    @BeforeEach
    fun setup() {
        kryo = Kryo()
        kryo.addDefaultSerializer(MessageLite::class.java, ProtobufSerializer::class.java)
    }

    @Test
    fun `should serialize and deserialize a protobuf message properly`() {
        val message = sampleInputMessage { }
        val serialized = serialize(message)

        val deserialized = deserialize<SampleInputMessage>(serialized)
        assertEquals(message, deserialized)
    }

    @Test
    fun `should serialize and deserialize another protobuf message properly`() {
        val message = sampleOutputMessage { id = 1; value = "foo" }
        val serialized = serialize(message)
        val deserialized = deserialize<SampleOutputMessage>(serialized)
        assertEquals(message, deserialized)
    }

    @Test
    fun `should fail on serialize a unknown type`() {
        assertThrows<IllegalArgumentException> {
            serialize(AtomicBoolean(true))
        }
    }


    private inline fun <reified T : Any> serialize(obj: T): ByteArray {
        Output().use { output ->
            output.setBuffer(byteArrayOf(), BUFFER_SIZE)
            kryo.writeObject(output, obj)
            return output.toBytes()
        }
    }

    private inline fun <reified T : Any> deserialize(bytes: ByteArray): T {
        Input(bytes).use { input ->
            return kryo.readObject(input, T::class.java)
        }
    }

    companion object {
        const val BUFFER_SIZE = 1024
    }
}