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

import org.apache.flink.api.common.functions.InvalidTypesException
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.test.junit5.MiniClusterExtension
import org.apache.flink.util.Collector
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertContentEquals

@ExtendWith(MiniClusterExtension::class)
internal class LambdaAdaptersTest {
    private data class SimpleClass(val id: String, val value: Int)

    @Nested
    inner class MapFunctionTest {
        @Test
        fun `should pass for simple type with lambda function`() {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()

            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(2, 4, 6, 8)
            val output =
                env.fromCollection(elements)
                    .keyBy { it }
                    .map { it * 2 }
                    .executeAndCollect().asSequence().toList()
            assertContentEquals(expectedOutput, output)
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
            val expectedOutput =
                listOf(
                    SimpleClass("someId-0", 2),
                    SimpleClass("someId-1", -2),
                    SimpleClass("someId-2", 0),
                )
            val output =
                env.fromCollection(elements)
                    .keyBy { it.id }
                    .map {
                        it.copy(value = it.value * 2)
                    }
                    .executeAndCollect().asSequence().toList()
            assertContentEquals(expectedOutput, output)
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
            val expectedOutput = listOf(2, 4, 6, 8)
            val output =
                genericRunnerWithExtensionFunction(elements) {
                    it * 2
                }
            assertContentEquals(expectedOutput, output)
        }

        @Test
        fun `should pass when using lambda function and using typeInformation on the generic runner`() {
            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(2, 4, 6, 8)
            val output =
                genericRunnerWithTypeInformation(elements) {
                    it * 2
                }
            assertContentEquals(expectedOutput, output)
        }

        private inline fun <reified T, reified O> genericRunner(
            elements: Collection<T>,
            crossinline block: (T) -> O,
        ): List<O> {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .map {
                    block(it)
                }
                .executeAndCollect().asSequence().toList()
        }

        private inline fun <reified T, reified O> genericRunnerWithExtensionFunction(
            elements: Collection<T>,
            crossinline block: (T) -> O,
        ): List<O> {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .map(mapFunction(block))
                .executeAndCollect().asSequence().toList()
        }

        private inline fun <reified T, reified O> genericRunnerWithTypeInformation(
            elements: Collection<T>,
            crossinline block: (T) -> O,
        ): List<O> {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .map {
                    block(it)
                }
                .returns(TypeInformation.of(O::class.java))
                .executeAndCollect().asSequence().toList()
        }
    }

    @Nested
    inner class FilterFunctionTest {
        @Test
        fun `should pass for simple type with lambda function`() {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()

            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(3, 4)
            val output =
                env.fromCollection(elements)
                    .keyBy { it }
                    .filter { it > 2 }
                    .executeAndCollect().asSequence().toList()
            assertContentEquals(expectedOutput, output)
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
            val expectedOutput =
                listOf(
                    SimpleClass("someId-0", 1),
                )
            val output =
                env.fromCollection(elements)
                    .keyBy { it.id }
                    .filter {
                        it.id == "someId-0"
                    }
                    .executeAndCollect().asSequence().toList()
            assertContentEquals(expectedOutput, output)
        }

        @Test
        fun `should pass for lambda function when using a generic runner`() {
            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(3, 4)
            val output =
                genericRunner(elements) {
                    it > 2
                }
            assertContentEquals(expectedOutput, output)
        }

        @Test
        fun `should pass for filterFunction when using a generic runner`() {
            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(3, 4)
            val output =
                genericRunnerWithExtensionFunction(elements) {
                    it > 2
                }
            assertContentEquals(expectedOutput, output)
        }

        private inline fun <reified T> genericRunner(
            elements: Collection<T>,
            crossinline block: (T) -> Boolean,
        ): List<T> {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .filter {
                    block(it)
                }
                .executeAndCollect().asSequence().toList()
        }

        private inline fun <reified T> genericRunnerWithExtensionFunction(
            elements: Collection<T>,
            crossinline block: (T) -> Boolean,
        ): List<T> {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .filter(filterFunction(block))
                .executeAndCollect().asSequence().toList()
        }
    }

    @Nested
    inner class FlatMapFunctionTest {
        @Test
        fun `should fails for simple type with lambda function because of type erasure`() {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()

            val elements = listOf(1, 2, 3, 4)
            assertThrows<InvalidTypesException> {
                env.fromCollection(elements)
                    .flatMap { value, out -> out.collect(value * 2) }
                    .executeAndCollect().asSequence().toList()
            }
        }

        @Test
        fun `should pass for flatMapFunction when using a generic runner`() {
            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(2, 4, 6, 8)
            val output =
                genericRunnerWithExtensionFunction(elements) { value, out ->
                    out.collect(value * 2)
                }
            assertContentEquals(expectedOutput, output)
        }

        @Test
        fun `should pass when using lambda function and using typeInformation on the generic runner`() {
            val elements = listOf(1, 2, 3, 4)
            val expectedOutput = listOf(2, 4, 6, 8)
            val output =
                genericRunnerWithTypeInformation(elements) { value, out ->
                    out.collect(value * 2)
                }
            assertContentEquals(expectedOutput, output)
        }

        private inline fun <reified T, reified O> genericRunnerWithExtensionFunction(
            elements: Collection<T>,
            crossinline block: (T, Collector<O>) -> Unit,
        ): List<O> {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .flatMap(flatMapFunction(block))
                .executeAndCollect().asSequence().toList()
        }

        private inline fun <reified T, reified O> genericRunnerWithTypeInformation(
            elements: Collection<T>,
            crossinline block: (T, Collector<O>) -> Unit,
        ): List<O> {
            val env = StreamExecutionEnvironment.getExecutionEnvironment()
            return env.fromCollection(elements)
                .flatMap { value, out ->
                    block(value, out)
                }
                .returns(TypeInformation.of(O::class.java))
                .executeAndCollect().asSequence().toList()
        }
    }
}
