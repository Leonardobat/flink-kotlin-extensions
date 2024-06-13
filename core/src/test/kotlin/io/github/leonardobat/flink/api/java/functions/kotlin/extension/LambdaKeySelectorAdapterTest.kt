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
package io.github.leonardobat.flink.api.java.functions.kotlin.extension

import org.apache.flink.api.common.functions.InvalidTypesException
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.test.junit5.MiniClusterExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertContentEquals

@ExtendWith(MiniClusterExtension::class)
internal class LambdaKeySelectorAdapterTest {
    private data class SimpleClass(val id: String, val value: Int)

    @Test
    fun `should pass for simple type with lambda function`() {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        val elements = listOf(1, 2, 3, 4)
        val output =
            env.fromCollection(elements)
                .keyBy { it }
                .executeAndCollect().asSequence().toList()
        assertContentEquals(elements, output)
    }

    @Test
    fun `should pass for lambda function with simple data class`() {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        val elements =
            listOf(
                SimpleClass("someId-0", 1),
                SimpleClass("someId-1", -1),
                SimpleClass("someId-2", 0),
            )
        val output =
            env.fromCollection(elements)
                .keyBy { it.id }
                .executeAndCollect().asSequence().toList()
        assertContentEquals(elements, output)
    }

    @Test
    fun `should fail for lambda function when using a generic runner because of type erasure`() {
        val elements = listOf(1, 2, 3)
        assertThrows<InvalidTypesException> {
            genericRunner(elements) {
                it * 2
            }
        }
    }

    @Test
    fun `should pass for mapFunction when using a generic runner`() {
        val elements = listOf(1, 2, 3, 4)
        val output =
            genericRunnerWithExtensionFunction(elements) {
                it * 2
            }
        assertContentEquals(elements, output)
    }

    private inline fun <reified T, reified K> genericRunner(
        elements: Collection<T>,
        crossinline block: (T) -> K,
    ): List<T> {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        return env.fromCollection(elements)
            .keyBy {
                block(it)
            }
            .executeAndCollect().asSequence().toList()
    }

    private inline fun <reified T, reified K> genericRunnerWithExtensionFunction(
        elements: Collection<T>,
        crossinline block: (T) -> K,
    ): List<T> {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        return env.fromCollection(elements)
            .keyBy(keySelector(block))
            .executeAndCollect().asSequence().toList()
    }
}
