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
package io.github.leonardobat.flink.streaming.api.datastream

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.test.junit5.MiniClusterExtension
import org.apache.flink.util.Collector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertContentEquals


@ExtendWith(MiniClusterExtension::class)
internal class DataStreamAdapterTest {

    @Test
    fun `should pass for lambda key and map function when using a generic runner`() {
        val elements = listOf(1, 2, 3, 4)
        val expectedOutput = listOf(2, 4, 6, 8)
        val output = genericRunner(elements,
            keyBlock = { it },
            mapBlock = { it * 2 }
        )
        assertContentEquals(expectedOutput, output)
    }

    @Test
    fun `should pass for lambda key and flatmap function when using a generic runner`() {
        val elements = listOf(1, 2, 3, 4)
        val expectedOutput = listOf(2, 4, 6, 8)
        val output = genericRunner(elements,
            keyBlock = { it },
            flatMapBlock = { value, out -> out.collect(value * 2) }
        )
        assertContentEquals(expectedOutput, output)
    }

    private inline fun <reified T, reified K, reified O> genericRunner(
        elements: Collection<T>,
        crossinline keyBlock: (T) -> K,
        crossinline mapBlock: (T) -> O,
    ): List<O> {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        return env.fromCollection(elements)
            .keyByLambda(keyBlock)
            .mapLambda(mapBlock)
            .executeAndCollect().asSequence().toList()
    }

    private inline fun <reified T, reified K, reified O> genericRunner(
        elements: Collection<T>,
        crossinline keyBlock: (T) -> K,
        crossinline flatMapBlock: (T, Collector<O>) -> Unit,
    ): List<O> {
        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        return env.fromCollection(elements)
            .keyByLambda(keyBlock)
            .flatMapLambda(flatMapBlock)
            .executeAndCollect().asSequence().toList()
    }
}