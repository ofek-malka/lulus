# LuluS - Functional Programming Data Structures Kit

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## Overview

LuluS is a **functional programming toolkit** that demonstrates modern perspectives on data structures and functional abstractions in Java.  
This project is designed primarily as a **learning and experimental platform** rather than for production use, focusing on clarity, functional purity, and extensibility.

---

## Philosophy

- **Functional-first design**: Emphasizes immutability, composability, and expressive APIs modeled on functional programming principles.  
- **Minimal dependencies**: Implements core data structures with custom functional interfaces to avoid unnecessary overhead or third-party complexity.  
- **Educational focus**: Intended to explore and teach advanced FP concepts applied to Java collections, including monadic transformations, recursion, and folding.  
- **Performance trade-offs**: While the design favors clarity and FP idioms, performance is **not optimized for production workloads**. Use with caution for real-world projects.  
- **Extensibility**: Designed to grow and incorporate additional functional patterns and data structures over time.

---

## Features

- Immutable and persistent list implementations with rich operations such as `map`, `fold`, `filter`, `groupBy`, and more.  
- Support for common FP constructs: `Result` handling, lazy evaluation, and tuple manipulations.  
- Factory interfaces for creating primitive and complex lists functionally.  
- Comprehensive behavioral interfaces separating concerns like slicing, aggregation, appending, and error handling.

---

## Usage

1. Clone the repository:  
   ```bash
   git clone <your-repo-url>
   ```

2. Build with Maven:  
   ```bash
   mvn clean install
   ```

3. Explore examples in the `lulus-examples` module and start extending or using the data structures in your projects.

---

## Known Limitations

- **Performance**: Not optimized for speed or memory efficiency; better suited for learning or prototyping FP data structures.  
- **API stability**: As an evolving project, APIs may change in future versions.

---

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

---

## Contributing

Contributions are welcome! Please open issues or pull requests to suggest improvements or report bugs.

---

## Contact

Ofek Malka  
📧 om.ofekmalka@gmail.com

---

Enjoy exploring functional programming in Java with LuluS! 🚀