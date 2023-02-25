```
Benchmark                                                                                      Mode  Cnt    Score   Error  Units
MultiThreadBufLimited.registerAndUpdateMaxHighResPlayersWithMovement                           avgt        81.654          ms/op
MultiThreadBufLimited.registerAndUpdateMaxPlayersNoExtInfo                                     avgt        78.548          ms/op
MultiThreadBufLimited.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo                      avgt        79.336          ms/op
MultiThreadBufLimitedStartHighRes.registerAndUpdateMaxHighResPlayersWithMovement               avgt        79.797          ms/op
MultiThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersNoExtInfo                         avgt        78.348          ms/op
MultiThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo          avgt        79.356          ms/op
MultiThreadNoBufLimit.registerAndUpdateMaxHighResPlayersWithMovement                           avgt        81.740          ms/op
MultiThreadNoBufLimit.registerAndUpdateMaxPlayersNoExtInfo                                     avgt        76.761          ms/op
MultiThreadNoBufLimit.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo                      avgt        79.281          ms/op

SingleThreadBufLimited.registerAndUpdateMaxHighResPlayersWithMovement                          avgt       199.164          ms/op
SingleThreadBufLimited.registerAndUpdateMaxPlayersNoExtInfo                                    avgt       200.806          ms/op
SingleThreadBufLimited.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo                     avgt       205.203          ms/op
SingleThreadBufLimitedStartHighRes.registerAndUpdateMaxHighResPlayersWithMovement              avgt       202.616          ms/op
SingleThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersNoExtInfo                        avgt       200.050          ms/op
SingleThreadBufLimitedStartHighRes.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo         avgt       198.281          ms/op
SingleThreadNoBufLimit.registerAndUpdateMaxHighResPlayersWithMovement                          avgt       205.985          ms/op
SingleThreadNoBufLimit.registerAndUpdateMaxPlayersNoExtInfo                                    avgt       205.398          ms/op
SingleThreadNoBufLimit.registerAndUpdateMaxPlayersWithMaxByteStaticExtInfo                     avgt       231.841          ms/op
```
