```
Benchmark                                                                               Mode  Cnt    Score   Error  Units
MultiThreadBufLimited.registerAndUpdateMaxHighResPlayersWithMovement                    avgt        79.593          ms/op
MultiThreadBufLimited.registerAndUpdateMaxPlayersNoExtInfo                              avgt        77.300          ms/op
MultiThreadBufLimited.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo               avgt        79.213          ms/op
MultiThreadBufLimitedStartHighRes.registerAndUpdateMaxHighResPlayersWithMovement        avgt        78.030          ms/op
MultiThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersNoExtInfo                  avgt        78.114          ms/op
MultiThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo   avgt        82.058          ms/op
MultiThreadNoBufLimit.registerAndUpdateMaxHighResPlayersWithMovement                    avgt        73.842          ms/op
MultiThreadNoBufLimit.registerAndUpdateMaxPlayersNoExtInfo                              avgt        75.187          ms/op
MultiThreadNoBufLimit.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo               avgt        78.823          ms/op

SingleThreadBufLimited.registerAndUpdateMaxHighResPlayersWithMovement                   avgt       191.676          ms/op
SingleThreadBufLimited.registerAndUpdateMaxPlayersNoExtInfo                             avgt       186.596          ms/op
SingleThreadBufLimited.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo              avgt       197.209          ms/op
SingleThreadBufLimitedStartHighRes.registerAndUpdateMaxHighResPlayersWithMovement       avgt       191.951          ms/op
SingleThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersNoExtInfo                 avgt       195.446          ms/op
SingleThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo  avgt       196.355          ms/op
SingleThreadNoBufLimit.registerAndUpdateMaxHighResPlayersWithMovement                   avgt       200.219          ms/op
SingleThreadNoBufLimit.registerAndUpdateMaxPlayersNoExtInfo                             avgt       205.668          ms/op
SingleThreadNoBufLimit.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo              avgt       225.798          ms/op
```
