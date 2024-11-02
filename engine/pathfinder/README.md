# pathfinder
[![Maven Central][maven-badge]][maven] [![license][license-badge]][isc]

An efficient [BFS][bfs] pathfinder.

## Installation

```
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.rsmod:rsmod-pathfinder:5.0.0")
}
```

## Example

```kotlin
class RouteFindingExample(collisionFlags: CollisionFlagMap) {

	private val routeFinding = RouteFinding(collisionFlags)

	fun findRoute(level: Int, srcX: Int, srcZ: Int, destX: Int, destZ: Int): Route {
		return routeFinding.findRoute(
			level = level,
			srcX = srcX,
			srcZ = srcZ,
			destX = destX,
			destZ = destZ
		)
	}
}
```

`CollisionFlagMap` is responsible for storing collision flag data for the game map.
When loading map data on the game-server, each loc - and special tiles, such
as roofs and bridges - should call the helper functions:
`fun CollisionFlagMap.add(x: Int, z: Int, level: Int, mask: Int)` and
`fun CollisionFlagMap.remove(x: Int, z: Int, level: Int, mask: Int)`
in order to add or remove, respectively, the `mask` flag data.

## Performance
**Benchmark sources:** [org/rsmod/pathfinder/benchmarks/][benchmark]

### Machine Specs
- **OS:** Windows 10 Pro (64-bit)
- **CPU:** Intel Core i7-7700k @ 4.20GHz (4 cores)
- **RAM:** 4 x 16GB DDR4-2132 (1066 MHz)
- **JMH:** 1.36
- **VM:** JDK 11.0.6, Java HotSpot(TM) 64-Bit Server VM, 11.0.6+8-LTS

### RouteFinding (BFS)
```
Benchmark                                 Mode  Cnt    Score   Error  Units
GameClickAltRoute.sequential              avgt       733.039          ms/op
GameClickLongRoute.sequential             avgt       367.350          ms/op
GameClickMedRoute.sequential              avgt       310.479          ms/op
GameClickShortRoute.sequential            avgt         7.429          ms/op

GameClickAltRoute.threadLocal             avgt       168.779          ms/op
GameClickLongRoute.threadLocal            avgt        85.639          ms/op
GameClickMedRoute.threadLocal             avgt        80.181          ms/op
GameClickShortRoute.threadLocal           avgt        19.010          ms/op
```

#### Glossary
- **sequential**: calculates 2000 routes sequentially using the same `RouteFinding` instance.
- **threadLocal**: similar to `sequential`, but uses `ThreadLocal<RouteFinding>` to find routes asynchronously.
- **GameClickAltRoute**: destination outside valid search distance (route-finder forced to iterate the whole search area) (~72 tiles).
- **GameClickLongRoute**: destination near upper limit of `RouteFinding::searchMapSize` radius (~63 tiles).
- **GameClickMedRoute**: destination about half of `RouteFinding::searchMapSize` radius (~32 tiles).
- **GameClickShortRoute**: destination near lower limit of `RouteFinding::searchMapSize` radius (~8 tiles).

## Contributing
Pull requests are welcome on [GitHub][github].

## License
This project is available under the terms of the ISC license, which is similar to the 2-clause BSD license. The full copyright notice and terms are available in the [LICENSE][license] file.

[isc]: https://opensource.org/licenses/ISC
[license]: https://github.com/rsmod/rsmod/blob/main/LICENSE.md
[license-badge]: https://img.shields.io/badge/license-ISC-informational
[maven]: https://maven-badges.herokuapp.com/maven-central/org.rsmod/rsmod-pathfinder
[maven-badge]: https://maven-badges.herokuapp.com/maven-central/org.rsmod/rsmod-pathfinder/badge.svg
[bfs]: https://en.wikipedia.org/wiki/Breadth-first_search
[github]: https://github.com/rsmod/rsmod
[benchmark]: https://github.com/rsmod/rsmod/tree/main/engine/pathfinder/src/jmh/kotlin/org/rsmod/pathfinder/benchmarks
