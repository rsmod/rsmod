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
class PathFinderExample(collisionFlags: CollisionFlagMap) {

	private val pathFinder = PathFinder(collisionFlags)

	fun findPath(level: Int, srcX: Int, srcZ: Int, destX: Int, destZ: Int): Route {
		return pathFinder.findPath(
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
When loading map data on the game-server, each object - and special tiles, such
as roofs and bridges - should call the helper functions:
`fun CollisionFlagMap.add(x: Int, z: Int, level: Int, mask: Int)` and
`fun CollisionFlagMap.remove(x: Int, z: Int, level: Int, mask: Int)`
in order to add or remove, respectively, the `mask` flag data.

## Performance
**Benchmark sources:** [org/rsmod/game/pathfinder/benchmarks/][benchmark]

### Machine Specs
- **OS:** Windows 10 Pro (64-bit)
- **CPU:** Intel Core i7-7700k @ 4.20GHz (4 cores)
- **RAM:** 4 x 16GB DDR4-2132 (1066 MHz)
- **JMH:** 1.36
- **VM:** JDK 11.0.6, Java HotSpot(TM) 64-Bit Server VM, 11.0.6+8-LTS

### PathFinder (BFS)
```
Benchmark                                 Mode  Cnt    Score   Error  Units
GameClickAltPath.sequentialPathFinder     avgt       733.039          ms/op
GameClickLongPath.sequentialPathFinder    avgt       367.350          ms/op
GameClickMedPath.sequentialPathFinder     avgt       310.479          ms/op
GameClickShortPath.sequentialPathFinder   avgt         7.429          ms/op

GameClickAltPath.threadLocalPathFinder    avgt       168.779          ms/op
GameClickLongPath.threadLocalPathFinder   avgt        85.639          ms/op
GameClickMedPath.threadLocalPathFinder    avgt        80.181          ms/op
GameClickShortPath.threadLocalPathFinder  avgt        19.010          ms/op
```

#### Glossary
- **sequentialPathFinder**: calculates 2000 paths sequentially using the same `PathFinder` instance.
- **threadLocalPathFinder**: similar to `sequentialPathFinder`, but uses `ThreadLocal<PathFinder>` to find paths asynchronously.
- **GameClickAltPath**: destination outside valid search distance (pathfinder forced to iterate the whole search area) (~72 tiles).
- **GameClickLongPath**: destination near upper limit of `PathFinder::searchMapSize` radius (~63 tiles).
- **GameClickMedPath**: destination about half of `PathFinder::searchMapSize` radius (~32 tiles).
- **GameClickShortPath**: destination near lower limit of `PathFinder::searchMapSize` radius (~8 tiles).

## Contributing
Pull requests are welcome on [GitHub][github].

## License
This project is available under the terms of the ISC license, which is similar to the 2-clause BSD license. The full copyright notice and terms are available in the [LICENSE][license] file.

## Acknowledgement
**Kris#1337** *Discord ID: 202342920106409984*:
Found and fixed several gameplay bugs with the pathfinder.
Discovered the concept of ~~DumbPathFinder~~ does not exist.

[isc]: https://opensource.org/licenses/ISC
[license]: https://github.com/rsmod/rsmod/blob/master/LICENSE.md
[license-badge]: https://img.shields.io/badge/license-ISC-informational
[maven]: https://maven-badges.herokuapp.com/maven-central/org.rsmod/rsmod-pathfinder
[maven-badge]: https://maven-badges.herokuapp.com/maven-central/org.rsmod/rsmod-pathfinder/badge.svg
[bfs]: https://en.wikipedia.org/wiki/Breadth-first_search
[github]: https://github.com/rsmod/rsmod
[benchmark]: https://github.com/rsmod/rsmod/tree/master/game/pathfinder/src/jmh/kotlin/org/rsmod/game/pathfinder/benchmarks
