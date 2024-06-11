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

import io.github.leonardobat.flink.api.common.kotlin.extension.flatMapFunction
import io.github.leonardobat.flink.api.common.kotlin.extension.mapFunction
import io.github.leonardobat.flink.api.common.kotlin.extension.partitioner
import io.github.leonardobat.flink.api.common.kotlin.extension.reduceFunction
import io.github.leonardobat.flink.api.java.functions.kotlin.extension.keySelector
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.datastream.KeyedStream
import org.apache.flink.util.Collector

/**
 * Creates a KeyedStream from an input DataStream by extracting a key from each element.
 *
 * @param block A lambda function that takes an element of type [T] and returns a key of type [K].
 * @return A KeyedStream where the key is determined by the provided lambda function.
 */
inline fun <T, K> DataStream<T>.keyByLambda(crossinline block: (T) -> K): KeyedStream<T, K> =
    this.keyBy(keySelector(block))

/**
 * Applies a map transformation to the input DataStream.
 *
 * @param block A lambda function that takes an element of type [T] and returns an element of type [O].
 * @return A DataStream where each element is transformed by the provided lambda function.
 */
inline fun <T, O> DataStream<T>.mapLambda(crossinline block: (T) -> O): DataStream<O> =
    this.map(mapFunction(block))

/**
 * Applies a flatMap transformation to the input DataStream.
 *
 * @param block A lambda function that takes an element of type [T] and a Collector of type [O],
 * and performs the flatMap operation.
 * @return A DataStream where each element is transformed by the provided lambda function,
 * resulting in zero, one, or multiple output elements.
 */
inline fun <T, O> DataStream<T>.flatMapLambda(crossinline block: (T, Collector<O>) -> Unit): DataStream<O> =
    this.flatMap(flatMapFunction(block))

/**
 * Applies a reduce transformation to the input KeyedStream.
 *
 * @param block A lambda function that takes two elements of type [T] and returns a combined element of type [T].
 * @return A DataStream where each key-group is reduced to a single element by the provided lambda function.
 */
inline fun <T, KEY> KeyedStream<T, KEY>.reduceLambda(crossinline block: (T, T) -> T): DataStream<T> =
    this.reduce(reduceFunction(block))

/**
 * Applies a custom partitioning transformation to the input KeyedStream.
 *
 * @param keySelector A lambda function that takes an element of type [T] and returns a key of type [KEY].
 * @param block A lambda function that takes a key of type [KEY] and the total number of partitions,
 * and returns the partition index for the given key.
 * @return A DataStream where the elements are partitioned based on the provided lambda function.
 */
inline fun <T, KEY> KeyedStream<T, KEY>.partitionCustomLambda(
    crossinline keySelector: (T) -> KEY,
    crossinline block: (KEY, Int) -> Int
): DataStream<T> =
    this.partitionCustom(partitioner(block), keySelector(keySelector))