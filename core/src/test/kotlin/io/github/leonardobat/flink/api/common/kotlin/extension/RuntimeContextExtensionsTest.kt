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
package io.github.leonardobat.flink.api.common.kotlin.extension

import io.github.leonardobat.flink.api.java.functions.kotlin.extension.keySelector
import org.apache.flink.api.common.state.ListState
import org.apache.flink.api.common.state.MapState
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.test.junit5.MiniClusterExtension
import org.apache.flink.util.Collector
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.io.UncheckedIOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertContentEquals

@ExtendWith(MiniClusterExtension::class)
internal class RuntimeContextExtensionsTest {
    private data class SimpleClass(val id: String, val value: Int)

    @Nested
    inner class ValueStateTest {
        @Test
        fun `should pass when the state is not present`() {
            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(1, 1, 2, 3)
            val output = testLaggingRunner(elements)
            assertContentEquals(expectedOutput, output)
        }

        @Test
        fun `should fail when the type doesn't have a serializer`() {
            val elements =
                listOf(
                    AtomicBoolean(false),
                    AtomicBoolean(false),
                    AtomicBoolean(false),
                )
            assertThrows<UncheckedIOException> {
                testLaggingRunner(elements)
            }
        }

        @Test
        fun `should pass for a simple data class`() {
            val elements =
                listOf(
                    SimpleClass("someId-0", 1),
                    SimpleClass("someId-1", -1),
                    SimpleClass("someId-2", 0),
                )
            val expectedOutput =
                listOf(
                    SimpleClass("someId-0", 1),
                    SimpleClass("someId-0", 1),
                    SimpleClass("someId-1", -1),
                )
            val output = testLaggingRunner(elements)
            assertContentEquals(expectedOutput, output)
        }

        private inline fun <reified T> testLaggingRunner(elements: Collection<T>): List<T> {
            val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .keyBy(keySelector { "test" })
                .process(
                    object : KeyedProcessFunction<String, T, T>() {
                        @Transient
                        private lateinit var state: ValueState<T>

                        override fun open(parameters: Configuration) {
                            state = runtimeContext.getState("test-state")
                        }

                        override fun processElement(
                            value: T,
                            ctx: Context,
                            out: Collector<T>,
                        ) {
                            val oldValue = state.value() ?: value
                            state.update(value)
                            out.collect(oldValue)
                        }
                    },
                ).executeAndCollect().asSequence().toList()
        }
    }

    @Nested
    inner class ListStateTest {
        @Test
        fun `should pass when the state is not present, the value must be all the entries`() {
            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(listOf(1), listOf(1, 2), listOf(1, 2, 3), listOf(1, 2, 3, 4))
            val output = testAccumulatingRunner(elements)
            assertContentEquals(expectedOutput, output)
        }

        @Test
        fun `should fail when the type doesn't have a serializer`() {
            val elements =
                listOf(
                    AtomicBoolean(false),
                    AtomicBoolean(true),
                )
            assertThrows<UncheckedIOException> {
                testAccumulatingRunner(elements)
            }
        }

        @Test
        fun `should pass for a simple data class`() {
            val elements =
                listOf(
                    SimpleClass("someId-0", 1),
                    SimpleClass("someId-1", -1),
                )
            val expectedOutput =
                listOf(
                    listOf(SimpleClass("someId-0", 1)),
                    listOf(SimpleClass("someId-0", 1), SimpleClass("someId-1", -1)),
                )
            val output = testAccumulatingRunner(elements)
            assertContentEquals(expectedOutput, output)
        }

        private inline fun <reified T> testAccumulatingRunner(elements: Collection<T>): List<List<T>> {
            val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .keyBy(keySelector { "test" })
                .process(
                    object : KeyedProcessFunction<String, T, List<T>>() {
                        @Transient
                        private lateinit var state: ListState<T>

                        override fun open(parameters: Configuration) {
                            state = runtimeContext.getListState("test-state")
                        }

                        override fun processElement(
                            value: T,
                            ctx: Context,
                            out: Collector<List<T>>,
                        ) {
                            state.add(value)
                            out.collect(state.get().toList())
                        }
                    },
                ).executeAndCollect().asSequence().toList()
        }
    }

    @Nested
    inner class MapStateTest {
        @Test
        fun `should pass when the state is not present, the value must be all the entries`() {
            val elements = listOf(1, 2, 3)
            val expectedOutput = listOf(mapOf("1" to 1), mapOf("2" to 2), mapOf("3" to 3))
            val output = testAccumulatingRunner(elements, keySelector { it.toString() })
            assertContentEquals(expectedOutput, output)
        }

        @Test
        fun `should fail when the type doesn't have a serializer`() {
            val elements =
                listOf(
                    AtomicBoolean(false),
                    AtomicBoolean(true),
                )
            assertThrows<UncheckedIOException> {
                testAccumulatingRunner(elements, keySelector { it.toString() })
            }
        }

        @Test
        fun `should pass for a simple data class`() {
            val elements =
                listOf(
                    SimpleClass("someId-0", 1),
                    SimpleClass("someId-1", -1),
                    SimpleClass("someId-0", 0),
                )
            val expectedOutput =
                listOf(
                    mapOf("someId-0" to SimpleClass("someId-0", 1)),
                    mapOf("someId-1" to SimpleClass("someId-1", -1)),
                    mapOf("someId-0" to SimpleClass("someId-0", 1), "someId-0-1" to SimpleClass("someId-0", 0)),
                )
            val output = testAccumulatingRunner(elements, keySelector { it.id })
            assertContentEquals(expectedOutput, output)
        }

        private inline fun <reified T> testAccumulatingRunner(
            elements: Collection<T>,
            keySelector: KeySelector<T, String>,
        ): List<Map<String, T>> {
            val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .keyBy(keySelector)
                .process(
                    object : KeyedProcessFunction<String, T, Map<String, T>>() {
                        @Transient
                        private lateinit var state: MapState<String, T>

                        override fun open(parameters: Configuration) {
                            state = runtimeContext.getMapState("test-state")
                        }

                        override fun processElement(
                            value: T,
                            ctx: Context,
                            out: Collector<Map<String, T>>,
                        ) {
                            val key = keySelector.getKey(value)
                            if (!state.contains(key)) {
                                state.put(key, value)
                            } else {
                                state.put("$key-1", value)
                            }
                            val map = state.entries().associate { it.key to it.value }
                            out.collect(map)
                        }
                    },
                ).executeAndCollect().asSequence().toList()
        }
    }
}
