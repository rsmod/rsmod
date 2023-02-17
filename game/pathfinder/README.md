# pathfinder
[![license][license-badge]][isc]

An efficient [BFS][bfs] pathfinder.

## Installation

```
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.rsmod:rsmod-pathfinder:1.3.0")
}
```

## Example

```kotlin
class PathFinderExample(collisionFlags: CollisionFlagMap) {

	private val pathFinder = PathFinder(collisionFlags)

	fun findPath(level: Int, srcX: Int, srcY: Int, destX: Int, destY: Int): Route {
		return pathFinder.findPath(
			level = level,
			srcX = srcX,
			srcY = srcY,
			destX = destX,
			destY = destY
		)
	}
}
```

`CollisionFlagMap` is responsible for storing collision flag data for the game map.
When loading map data on the game-server, each object - and special tiles, such
as roofs and bridges - should call the helper functions:
`fun CollisionFlagMap.add(x: Int, y: Int, level: Int, mask: Int)` and
`fun CollisionFlagMap.remove(x: Int, y: Int, level: Int, mask: Int)`
in order to add or remove, respectively, the `mask` flag data.

## Performance
**Benchmark sources:** [org/rsmod/game/pathfinder/benchmarks/][benchmark]

### Machine Specs
- **OS:** Windows 10 Pro (64-bit)
- **CPU:** Intel Core i7-7700k @ 4.20GHz (4 cores)
- **RAM:** 4 x 16GB DDR4-2132 (1066 MHz)
- **JMH:** 1.25
- **VM:** JDK 11.0.6, Java HotSpot(TM) 64-Bit Server VM, 11.0.6+8-LTS

### PathFinder (BFS)
Each benchmark calculates 2000 paths from short to out-of-bound (beyond search distance) destinations.

```
Benchmark                                                           Mode  Cnt    Score    Error  Units

GameClickAltPath.serverPathConstructOnIteration                     avgt    3  935.490 ± 24.586  ms/op
GameClickAltPath.serverPathResetOnIteration                         avgt    3  946.769 ± 61.221  ms/op
GameClickAltPath.serverPathCoroutineDispatcherConstruct             avgt    3  198.006 ± 31.676  ms/op
GameClickAltPath.serverPathCoroutineDispatcherThreadLocal           avgt    3  206.950 ± 40.522  ms/op

GameClickLongPath.serverPathConstructOnIteration                    avgt    3  451.048 ± 50.930  ms/op
GameClickLongPath.serverPathResetOnIteration                        avgt    3  443.949 ± 15.286  ms/op
GameClickLongPath.serverPathCoroutineDispatcherConstruct            avgt    3   93.495 ± 31.661  ms/op
GameClickLongPath.serverPathCoroutineDispatcherThreadLocal          avgt    3  108.801 ± 50.247  ms/op

GameClickMedPath.serverPathConstructOnIteration                     avgt    3  393.546 ± 56.843  ms/op
GameClickMedPath.serverPathResetOnIteration                         avgt    3  379.502 ±  4.892  ms/op
GameClickMedPath.serverPathCoroutineDispatcherConstruct             avgt    3   80.302 ± 12.809  ms/op
GameClickMedPath.serverPathCoroutineDispatcherThreadLocal           avgt    3  100.813 ± 77.434  ms/op

GameClickShortPath.serverPathConstructOnIteration                   avgt    3   26.333 ± 12.472  ms/op
GameClickShortPath.serverPathResetOnIteration                       avgt    3    8.103 ±  0.252  ms/op
GameClickShortPath.serverPathCoroutineDispatcherConstruct           avgt    3   19.160 ±  7.262  ms/op
GameClickShortPath.serverPathCoroutineDispatcherThreadLocal         avgt    3   20.322 ± 47.531  ms/op
```

#### Glossary
- **GameClickAltPath**: destination outside valid search distance (pathfinder forced to iterate the whole search area) (~72 tiles).
- **GameClickLongPath**: destination near upper limit of `PathFinder::searchMapSize` radius (~63 tiles).
- **GameClickMedPath**: destination about half of `PathFinder::searchMapSize` radius (~32 tiles).
- **GameClickShortPath**: destination near lower limit of `PathFinder::searchMapSize` radius (~8 tiles).
- **serverPathConstructOnIteration**: construct a new `PathFinder` for every iteration.
- **serverPathResetOnIteration**: reset values on same `PathFinder` instance to re-use every iteration.
- **serverPathCoroutineDispatcherConstruct**: similar to `serverPathConstructOnIteration`, but using coroutines for each iteration.
- **serverPathCoroutineDispatcherThreadLocal**: similar to `serverPathCoroutineDispatcherConstruct`, but uses `ThreadLocal` instead of always constructing a new `PathFinder` instance per iteration.

## Contributing
Pull requests are welcome on [GitHub][github].

## License
This project is available under the terms of the ISC license, which is similar to the 2-clause BSD license. The full copyright notice and terms are available in the [LICENSE][license] file.

[isc]: https://opensource.org/licenses/ISC
[license]: https://github.com/rsmod/rsmod/blob/master/LICENSE.md
[license-badge]: https://img.shields.io/badge/license-ISC-informational
[bfs]: https://en.wikipedia.org/wiki/Breadth-first_search
[github]: https://github.com/rsmod/rsmod
[benchmark]: https://github.com/rsmod/rsmod/tree/master/game/pathfinder/src/jmh/kotlin/org/rsmod/game/pathfinder/benchmarks

## Acknowledgement
**Kris#1337** *Discord ID: 202342920106409984*:
Found and fixed several gameplay bugs with the pathfinder.
Discovered the concept of ~~DumbPathFinder~~ does not exist.
