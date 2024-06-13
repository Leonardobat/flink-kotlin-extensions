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

import org.apache.flink.api.java.functions.KeySelector

/**
 * Creates a [KeySelector] from a lambda function
 *
 * This function allows you to create a [KeySelector] using a lambda function, which can be more concise and readable.
 * This is necessary because for now the [org.apache.flink.api.java.typeutils.TypeExtractionUtils.validateLambdaType] isn't able to handle the kotlin lambda function.
 *
 * @param IN The type of the input elements.
 * @param KEY The type of the keys to be extracted.
 * @param block A lambda function that takes an input element and returns the corresponding key.
 * @return A [KeySelector] that extracts keys using the provided lambda function.
 */
inline fun <IN, KEY> keySelector(crossinline block: (IN) -> KEY): KeySelector<IN, KEY> =
    object : KeySelector<IN, KEY> {
        override fun getKey(it: IN) = block(it)
    }
