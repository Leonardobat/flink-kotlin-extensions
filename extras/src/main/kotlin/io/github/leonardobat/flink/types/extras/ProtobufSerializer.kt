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
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.google.protobuf.MessageLite
import java.lang.reflect.Method

/**
 * A custom Kryo serializer for Google Protocol Buffers (Protobuf) messages.
 *
 * It implements the [Serializer] for the [MessageLite] since all protobuf messages implement that interface.
 * However, it uses methods from the actual generated code like `getDefaultInstance()` and `parseFrom`.
 *
 * For flink usage do a `env.addDefaultKryoSerializer(MessageLite::class.java, KryoProtobufSerializer::class.java)`
 *
 * @constructor Creates a new instance of KryoProtobufSerializer.
 */
class ProtobufSerializer: Serializer<MessageLite>() {

    // Cache for the `parseFrom(byte[] bytes)` method
    private val methodCache: MutableMap<Class<*>, Method> = HashMap()

    // Cache for the `getDefaultInstance()` method
    private val defaultInstanceMethodCache: MutableMap<Class<*>, Method> = HashMap()

    /**
     * Retrieves a cached method reference, this is necessary since using reflection is very slow. If the method is not
     * found in the cache, it is retrieved using reflection and then stored in the cache for future use.
     *
     * @param cls The class containing the method.
     * @param cache The cache to store the method reference.
     * @param methodName The name of the method.
     * @param parameterTypes The types of the method parameters.
     * @return The cached method reference.
     * @throws Exception If an error occurs during method retrieval.
     * @see <a href="https://blogs.oracle.com/javamagazine/post/java-reflection-performance">
     */
    @Throws(Exception::class)
    private fun getCachedMethod(cls: Class<*>, cache: MutableMap<Class<*>, Method>, methodName: String, vararg parameterTypes: Class<*>): Method {
        return cache.getOrPut(cls) {
            cls.getMethod(methodName, *parameterTypes)
        }
    }

    /**
     * Retrieves the cached `parseFrom(byte[] bytes)` method reference for a given class.
     *
     * @param cls The class to retrieve the method reference for.
     * @return The cached `parseFrom(byte[] bytes)` method reference.
     * @throws Exception If an error occurs during method retrieval.
     */
    @Throws(Exception::class)
    private fun getCachedParseMethod(cls: Class<*>): Method {
        return getCachedMethod(cls, methodCache, "parseFrom", ByteArray::class.java)
    }

    /**
     * Retrieves the cached `getDefaultInstance()` method reference for a given class.
     *
     * @param cls The class to retrieve the method reference for.
     * @return The cached `getDefaultInstance()` method reference.
     * @throws Exception If an error occurs during method retrieval.
     */
    @Throws(Exception::class)
    private fun getCachedDefaultInstanceMethod(cls: Class<*>): Method {
        return getCachedMethod(cls, defaultInstanceMethodCache, "getDefaultInstance")
    }

    /**
     * Serializes a Protobuf message using the Kryo framework.
     *
     * @param kryo The Kryo instance.
     * @param output The output stream to write the serialized data to.
     * @param message The Protobuf message to serialize.
     */
    override fun write(kryo: Kryo, output: Output, message: MessageLite) {
        val serializedMessage: ByteArray = message.toByteArray()
        output.writeInt(serializedMessage.size, true)
        output.writeBytes(serializedMessage)
    }

    /**
     * Deserializes a Protobuf message using the Kryo framework.
     *
     * @param kryo The Kryo instance.
     * @param input The input stream to read the serialized data from.
     * @param messageClass The class of the Protobuf message to deserialize.
     * @return The deserialized Protobuf message.
     * @throws Exception If an error occurs during deserialization.
     */
    override fun read(kryo: Kryo, input: Input, messageClass: Class<MessageLite>): MessageLite {
        try {
            val size: Int = input.readInt(true)
            if (size == 0) {
                return getCachedDefaultInstanceMethod(messageClass).invoke(null) as MessageLite
            }
            val byteArray = ByteArray(size)
            input.readBytes(byteArray)
            return getCachedParseMethod(messageClass).invoke(null, byteArray) as MessageLite
        } catch (e: Exception) {
            throw RuntimeException("Failed to deserialize $messageClass", e)
        }
    }

    /**
     * Creates a copy of a Protobuf message.
     *
     * @param kryo The Kryo instance.
     * @param original The original Protobuf message to copy.
     * @return The copied Protobuf message.
     * @throws Exception If an error occurs during copying.
     */
    override fun copy(kryo: Kryo, original: MessageLite): MessageLite {
        try {
            val bytes: ByteArray = original.toByteArray()
            return getCachedParseMethod(original::class.java).invoke(null, bytes) as MessageLite
        } catch (exception: Exception) {
            throw RuntimeException("Failed to copy message.", exception)
        }
    }
}

