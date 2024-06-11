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

import org.apache.flink.annotation.PublicEvolving
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.common.state.AggregatingState
import org.apache.flink.api.common.state.AggregatingStateDescriptor
import org.apache.flink.api.common.state.ListState
import org.apache.flink.api.common.state.ListStateDescriptor
import org.apache.flink.api.common.state.MapState
import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.state.ReducingState
import org.apache.flink.api.common.state.ReducingStateDescriptor
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.TypeInformation

/**
 * Retrieves a [ValueState] with the given name and the type inferred from the generic parameter.
 *
 * @param name The name of the state.
 * @return The [ValueState] with the given name and the inferred type.
 *
 * @see <a href="https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/dev/datastream/fault-tolerance/serialization/types_serialization/#creating-a-typeinformation-or-typeserializer">Creating a TypeInformation or TypeSerializer</a>
 * @see RuntimeContext.getState
 */
@PublicEvolving
inline fun <reified T> RuntimeContext.getState(name: String): ValueState<T> = getState(ValueStateDescriptor(name, T::class.java))

/**
 * Retrieves a [ValueState] with the given name and type information.
 *
 * @param name The name of the state.
 * @param typeInformation The type information of the state.
 * @return The [ValueState] with the given name and type information.
 *
 * @see <a href="https://nightlies.apache.org/flink/flink-docs-release-1.18/docs/dev/datastream/fault-tolerance/serialization/types_serialization/#creating-a-typeinformation-or-typeserializer">Creating a TypeInformation or TypeSerializer</a>
 * @see RuntimeContext.getState
 */
@PublicEvolving
fun <T> RuntimeContext.getState(
    name: String,
    typeInformation: TypeInformation<T>,
): ValueState<T> = getState(ValueStateDescriptor(name, typeInformation))

/**
 * Retrieves a [ListState] with the given name and the type inferred from the generic parameter.
 *
 * @param name The name of the state.
 * @return The [ListState] with the given name and the inferred type.
 * @see RuntimeContext.getListState
 */
@PublicEvolving
inline fun <reified T> RuntimeContext.getListState(name: String): ListState<T> = getListState(ListStateDescriptor(name, T::class.java))

/**
 * Retrieves a [ListState] with the given name and type information.
 *
 * @param name The name of the state.
 * @param typeInformation The type information of the state.
 * @return The [ListState] with the given name and type information.
 * @see RuntimeContext.getListState
 */
@PublicEvolving
fun <T> RuntimeContext.getListState(
    name: String,
    typeInformation: TypeInformation<T>,
): ListState<T> = getListState(ListStateDescriptor(name, typeInformation))

/**
 * Retrieves a [ReducingState] with the given name, reducing function, and the type inferred from the generic parameter.
 *
 * @param name The name of the state.
 * @param reducingFunction The reducing function.
 * @return The [ReducingState] with the given name, reducing function, and the inferred type.
 * @see RuntimeContext.getReducingState
 */
@PublicEvolving
inline fun <reified T> RuntimeContext.getReducingState(
    name: String,
    reducingFunction: ReduceFunction<T>,
): ReducingState<T> = getReducingState(ReducingStateDescriptor(name, reducingFunction, T::class.java))

/**
 * Retrieves a [ReducingState] with the given name, reducing function, and type information.
 *
 * @param name The name of the state.
 * @param reducingFunction The reducing function.
 * @param typeInformation The type information of the state.
 * @return The [ReducingState] with the given name, reducing function, and type information.
 * @see RuntimeContext.getReducingState
 */
@PublicEvolving
fun <T> RuntimeContext.getReducingState(
    name: String,
    reducingFunction: ReduceFunction<T>,
    typeInformation: TypeInformation<T>,
): ReducingState<T> = getReducingState(ReducingStateDescriptor(name, reducingFunction, typeInformation))

/**
 * Retrieves an [AggregatingState] with the given name, aggregate function, and the type inferred from the generic parameters.
 *
 * @param name The name of the state.
 * @param aggregateFunction The aggregate function.
 * @return The [AggregatingState] with the given name, aggregate function, and the inferred accumulator and output types.
 * @see RuntimeContext.getAggregatingState
 */
@PublicEvolving
inline fun <IN, reified ACC, OUT> RuntimeContext.getAggregatingState(
    name: String,
    aggregateFunction: AggregateFunction<IN, ACC, OUT>,
): AggregatingState<IN, OUT> = getAggregatingState(AggregatingStateDescriptor(name, aggregateFunction, ACC::class.java))

/**
 * Retrieves a [MapState] with the given name and the types inferred from the generic parameters.
 *
 * @param name The name of the state.
 * @return The [MapState] with the given name and the inferred key and value types.
 * @see RuntimeContext.getMapState
 */
@PublicEvolving
inline fun <reified UK, reified UV> RuntimeContext.getMapState(name: String): MapState<UK, UV> =
    getMapState(MapStateDescriptor(name, UK::class.java, UV::class.java))

/**
 * Retrieves a [MapState] with the given name, key type information, and value type information.
 *
 * @param name The name of the state.
 * @param keyTypeInformation The type information of the key.
 * @param valueTypeInformation The type information of the value.
 * @return The [MapState] with the given name, key type information, and value type information.
 * @see RuntimeContext.getMapState
 */
@PublicEvolving
fun <UK, UV> RuntimeContext.getMapState(
    name: String,
    keyTypeInformation: TypeInformation<UK>,
    valueTypeInformation: TypeInformation<UV>,
): MapState<UK, UV> = getMapState(MapStateDescriptor(name, keyTypeInformation, valueTypeInformation))
