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

import org.apache.flink.api.common.functions.CoGroupFunction
import org.apache.flink.api.common.functions.CombineFunction
import org.apache.flink.api.common.functions.CrossFunction
import org.apache.flink.api.common.functions.FilterFunction
import org.apache.flink.api.common.functions.FlatJoinFunction
import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.common.functions.GroupCombineFunction
import org.apache.flink.api.common.functions.GroupReduceFunction
import org.apache.flink.api.common.functions.JoinFunction
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.functions.MapPartitionFunction
import org.apache.flink.api.common.functions.Partitioner
import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.util.Collector

/**
 * Creates a [MapFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [MapFunction].
 * @return A [MapFunction] that applies the lambda function to each element.
 */
inline fun <T, O> mapFunction(crossinline block: (value: T) -> O): MapFunction<T, O> =
    object : MapFunction<T, O> {
        override fun map(value: T): O = block(value)
    }

/**
 * Creates a [FlatMapFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [FlatMapFunction].
 * @return A [FlatMapFunction] that applies the lambda function to each element and emits zero or more elements.
 */
inline fun <T, O> flatMapFunction(crossinline block: (value: T, collector: Collector<O>) -> Unit): FlatMapFunction<T, O> =
    object : FlatMapFunction<T, O> {
        override fun flatMap(
            value: T,
            collector: Collector<O>,
        ): Unit = block(value, collector)
    }

/**
 * Creates a [FilterFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [FilterFunction].
 * @return A [FilterFunction] that applies the lambda function to each element and only emits elements for which the lambda function returns true.
 */
inline fun <T> filterFunction(crossinline block: (value: T) -> Boolean): FilterFunction<T> =
    object : FilterFunction<T> {
        override fun filter(value: T): Boolean = block(value)
    }

/**
 * Creates a [JoinFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [JoinFunction].
 * @return A [JoinFunction] that applies the lambda function to pairs of elements from two input streams.
 */
inline fun <IN1, IN2, OUT> joinFunction(crossinline block: (first: IN1, second: IN2) -> OUT): JoinFunction<IN1, IN2, OUT> =
    object : JoinFunction<IN1, IN2, OUT> {
        override fun join(
            first: IN1,
            second: IN2,
        ): OUT = block(first, second)
    }

/**
 * Creates a [FlatJoinFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [FlatJoinFunction].
 * @return A [FlatJoinFunction] that applies the lambda function to pairs of elements from two input streams and emits zero or more elements.
 */
inline fun <IN1, IN2, OUT> flatJoinFunction(
    crossinline block: (first: IN1, second: IN2, collector: Collector<OUT>) -> Unit,
): FlatJoinFunction<IN1, IN2, OUT> =
    object : FlatJoinFunction<IN1, IN2, OUT> {
        override fun join(
            first: IN1,
            second: IN2,
            collector: Collector<OUT>,
        ): Unit = block(first, second, collector)
    }

/**
 * Creates a [ReduceFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [ReduceFunction].
 * @return A [ReduceFunction] that applies the lambda function to pairs of elements and emits a single element.
 */
inline fun <T> reduceFunction(crossinline block: (value1: T, value2: T) -> T): ReduceFunction<T> =
    object : ReduceFunction<T> {
        override fun reduce(
            value1: T,
            value2: T,
        ): T = block(value1, value2)
    }

/**
 * Creates a [GroupReduceFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [GroupReduceFunction].
 * @return A [GroupReduceFunction] that applies the lambda function to each group of elements and emits zero or more elements.
 */
inline fun <T, O> groupReduceFunction(crossinline block: (values: Iterable<T>, out: Collector<O>) -> Unit): GroupReduceFunction<T, O> =
    object : GroupReduceFunction<T, O> {
        override fun reduce(
            values: Iterable<T>,
            out: Collector<O>,
        ): Unit = block(values, out)
    }

/**
 * Creates a [CombineFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [CombineFunction].
 * @return A [CombineFunction] that applies the lambda function to each group of elements and emits a single element.
 */
inline fun <IN, OUT> combineFunction(crossinline block: (first: Iterable<IN>) -> OUT): CombineFunction<IN, OUT> =
    object : CombineFunction<IN, OUT> {
        override fun combine(values: Iterable<IN>): OUT = block(values)
    }

/**
 * Creates a [GroupCombineFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [GroupCombineFunction].
 * @return A [GroupCombineFunction] that applies the lambda function to each group of elements and emits zero or more elements.
 */
inline fun <IN, OUT> groupCombineFunction(
    crossinline block: (values: Iterable<IN>, out: Collector<OUT>) -> Unit,
): GroupCombineFunction<IN, OUT> =
    object : GroupCombineFunction<IN, OUT> {
        override fun combine(
            values: Iterable<IN>,
            out: Collector<OUT>,
        ): Unit = block(values, out)
    }

/**
 * Creates a [CoGroupFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [CoGroupFunction].
 * @return A [CoGroupFunction] that applies the lambda function to pairs of groups of elements from two input streams and emits zero or more elements.
 */
inline fun <IN1, IN2, O> coGroupFunction(
    crossinline block: (first: Iterable<IN1>, second: Iterable<IN2>, out: Collector<O>) -> Unit,
): CoGroupFunction<IN1, IN2, O> =
    object : CoGroupFunction<IN1, IN2, O> {
        override fun coGroup(
            first: Iterable<IN1>,
            second: Iterable<IN2>,
            out: Collector<O>,
        ) = block(first, second, out)
    }

/**
 * Creates a [CrossFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [CrossFunction].
 * @return A [CrossFunction] that applies the lambda function to each pair of elements from two input streams and emits a single element.
 */
inline fun <IN1, IN2, OUT> crossFunction(crossinline block: (val1: IN1, val2: IN2) -> OUT): CrossFunction<IN1, IN2, OUT> =
    object : CrossFunction<IN1, IN2, OUT> {
        override fun cross(
            val1: IN1,
            val2: IN2,
        ): OUT = block(val1, val2)
    }

/**
 * Creates a [Partitioner] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [Partitioner].
 * @return A [Partitioner] that determines the partition for each key based on the lambda function.
 */
inline fun <K> partitioner(crossinline block: (key: K, numPartitions: Int) -> Int): Partitioner<K> =
    object : Partitioner<K> {
        override fun partition(
            key: K,
            numPartitions: Int,
        ): Int = block(key, numPartitions)
    }

/**
 * Creates a [MapPartitionFunction] from the given lambda function.
 *
 * @param block The lambda function that will be used as the [MapPartitionFunction].
 * @return A [MapPartitionFunction] that applies the lambda function to each partition of elements and emits zero or more elements.
 */
inline fun <T, O> mapPartitionFunction(
    crossinline block: (values: Iterable<T>, collector: Collector<O>) -> Unit,
): MapPartitionFunction<T, O> =
    object : MapPartitionFunction<T, O> {
        override fun mapPartition(
            values: Iterable<T>,
            out: Collector<O>,
        ) = block(values, out)
    }
