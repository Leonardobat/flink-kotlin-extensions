# Flink Kotlin Extension Project

This repository provides a collection of helper functions enhance the usability of Apache Flink® for Kotlin projects. It includes the following components:

1. `flink-core`: Contains essential utilities and extensions for working with Apache Flink® in Kotlin.
2. `flink-streaming`: Provides additional utilities and extensions for Apache Flink®'s streaming API in Kotlin.
3. `flink-extras`: Provides additional utilities for common third-party-components, like Protobuf and Apache Kafka®.

## Extensions & Adapters Overview

The `flink-core` module provides the following utilities and extensions:
- [LambdaAdapters.kt](flink-core/src/main/kotlin/io/github/leonardobat/flink/api/common/kotlin/extension/LambdaAdapters.kt): Contains lambda adapter for the functional interfaces defined on `org.apache.flink.api.common.functions`, such as `map`, `filter`, `reduce`, and `aggregate`.
- [RuntimeContextExtensions.kt](flink-core/src/main/kotlin/io/github/leonardobat/flink/api/common/kotlin/extension/RuntimeContextExtensions.kt): Contains extension functions for creating a state on the `RuntimeContext`.
- [LambdaKeySelectorAdapter.kt](flink-core/src/main/kotlin/io/github/leonardobat/flink/api/java/functions/kotlin/extension/LambdaKeySelectorAdapter.kt): Contains a lambda adapter for the `KeySelector`.

The `flink-streaming` module provides the following additional utilities and extensions for Apache Flink®'s streaming API:

- `FlinkStreamingExtensions`: Contains extension functions for Flink's streaming operations, such as `keyBy`, `window`, and `reduceByKey`.
- `FlinkWindowFunctions`: Contains extension functions for Flink's window functions, such as `sum`, `average`, and `max`.

## Contributing

Contributions to this project are welcome! If you have any ideas, suggestions, or improvements, please feel free to open an issue or submit a pull request.

## License
This project is licensed under the Apache License, Version 2.0. See the [LICENSE](https://github.com/flink-kotlin-extensions/flink-kotlin-extensions/blob/main/LICENSE) file for more information.

### Acknowledgments

This project was inspired by the [Classpass' Flink Kotlin](https://github.com/classpass/flink-kotlin) repository. 

It also includes some work from [Chill](https://github.com/twitter/chill/), for the protobuf serialization.
