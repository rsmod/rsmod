package org.rsmod.plugins.info.player

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.plugins.info.player.PlayerInfo.Companion.DEFAULT_BUFFER_LIMIT
import org.rsmod.plugins.info.player.PlayerInfo.Companion.MAX_PLAYER_LIMIT
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_AVATAR_INFO_OPCODE
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_CHANGE_RESOLUTION
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_HIGH_RES_COORDS_DISPLACEMENT
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_HIGH_RES_COORDS_TELEPORT_CHANGE
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_SKIP_COUNT_11BITS
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_SKIP_COUNT_5BITS
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_SKIP_COUNT_NO_BITS
import org.rsmod.plugins.info.player.PlayerInfoOpcodes.READ_SKIP_COUNT_OPCODE
import org.rsmod.plugins.info.player.buffer.BitBuffer
import org.rsmod.plugins.info.player.model.PlayerInfoMetadata
import org.rsmod.plugins.info.player.model.coord.HighResCoord
import java.nio.ByteBuffer

class PlayerInfoTransmissionTest {

    @Test
    fun testSinglePlayerFullReadNoUpdate() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(DEFAULT_BUFFER_LIMIT)
        val index = DUMMY_INDEX
        val metadata = PlayerInfoMetadata()
        info.register(index)
        info.updateCoords(index, HighResCoord(3200, 3200), HighResCoord(3200, 3200))
        info.put(buf, index, metadata)
        assertEquals(3, buf.remaining())
        run getHighRes@{
            BitBuffer(buf).use { bitBuf ->
                val readOpcode = bitBuf.getBits(1)
                val skipOpcode = bitBuf.getBits(2)
                assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
            }
        }
        run getLowRes@{
            BitBuffer(buf).use { bitBuf ->
                val readOpcode = bitBuf.getBits(1)
                val skipOpcode = bitBuf.getBits(2)
                val skipCount = bitBuf.getBits(11)
                assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                assertEquals(MAX_PLAYER_LIMIT - 2, skipCount)
            }
        }
        assertEquals(0, buf.remaining())
    }

    @Test
    fun testSinglePlayerFullReadExtInfoNoMovement() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(DEFAULT_BUFFER_LIMIT)
        val index = DUMMY_INDEX
        val metadata = PlayerInfoMetadata()
        info.register(index)
        info.updateCoords(index, HighResCoord(3200, 3200), HighResCoord(3200, 3200))
        info.put(buf, index, metadata)
        assertEquals(3, buf.remaining())
        run getHighRes@{
            BitBuffer(buf).use { bitBuf ->
                val readOpcode = bitBuf.getBits(1)
                val skipOpcode = bitBuf.getBits(2)
                assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
            }
        }
        run getLowRes@{
            BitBuffer(buf).use { bitBuf ->
                val readOpcode = bitBuf.getBits(1)
                val skipOpcode = bitBuf.getBits(2)
                val skipCount = bitBuf.getBits(11)
                assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                assertEquals(MAX_PLAYER_LIMIT - 2, skipCount)
            }
        }
        assertEquals(0, buf.remaining())
    }

    /**
     * In this scenario there are two players: Player A and Player B
     *
     * Tick X:
     * Player A: stationary at coords 3200,3200
     * Player B: stationary at coords 4800,4800
     *
     * Tick Y:
     * Player B: teleports to player A
     *
     * Tick Z:
     * Player B: teleports outside of player A view distance
     *
     * Where X < Y < Z
     */
    @Test
    fun testCase1() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(DEFAULT_BUFFER_LIMIT)
        val playerA = 1
        val playerB = 2
        val extInfoA = mockExtendedInfo(buf, "PlayerA")
        val extInfoB = mockExtendedInfo(buf, "PlayerB")
        info.register(playerIndex = playerA)
        info.register(playerIndex = playerB)

        info.avatars[playerA].coords = HighResCoord(3200, 3200)
        info.avatars[playerA].prevCoords = HighResCoord(3200, 3200)

        info.avatars[playerB].coords = HighResCoord(4800, 4800)
        info.avatars[playerB].prevCoords = HighResCoord(4800, 4800)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2045, metadata.lowResolutionSkip)
            assertEquals(3, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 2, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2045, metadata.lowResolutionSkip)
            assertEquals(3, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 2, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        info.cacheStaticExtendedInfo(playerIndex = playerA, extInfoA)
        info.cacheStaticExtendedInfo(playerIndex = playerB, extInfoB)

        info.avatars[playerB].coords = HighResCoord(3200, 3200)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            val client = info.clients[playerA]
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(client.isHighResolution[playerB])
            assertEquals(7 + extInfoB.size, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    run getPlayerB@{
                        val readOpcode = bitBuf.getBits(1)
                        val updateType = bitBuf.getBits(2)
                        val updateLowResCoords = bitBuf.getBoolean()
                        val x = bitBuf.getBits(13)
                        val y = bitBuf.getBits(13)
                        val extended = bitBuf.getBoolean()
                        assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                        assertEquals(READ_CHANGE_RESOLUTION, updateType)
                        assertFalse(updateLowResCoords)
                        assertEquals(info.avatars[playerB].coords.x, x)
                        assertEquals(info.avatars[playerB].coords.y, y)
                        assertTrue(extended)
                    }

                    run getOtherLowRes@{
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        val skipCount = bitBuf.getBits(11)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                        assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                    }
                }
            }
            run getExtendedInfo@{
                val remaining = ByteArray(buf.remaining())
                buf.get(remaining)
                assertArrayEquals(extInfoB, remaining)
            }
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            val client = info.clients[playerB]
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertTrue(client.isHighResolution[playerA])
            assertEquals(11 + extInfoA.size, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val extended = bitBuf.getBoolean()
                    val displaceType = bitBuf.getBits(2)
                    val largeTeleport = bitBuf.getBoolean()
                    val diffLevel = bitBuf.getBits(2)
                    val diffX = bitBuf.getBits(14)
                    val diffY = bitBuf.getBits(14)
                    val expected = info.avatars[playerB].coords - info.avatars[playerB].prevCoords
                    assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                    assertFalse(extended)
                    assertEquals(READ_HIGH_RES_COORDS_TELEPORT_CHANGE, displaceType)
                    assertTrue(largeTeleport)
                    assertEquals(expected.level, diffLevel)
                    assertEquals(expected.x, diffX)
                    assertEquals(expected.y, diffY)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    run getPlayerA@{
                        val readOpcode = bitBuf.getBits(1)
                        val updateType = bitBuf.getBits(2)
                        val updateLowResCoords = bitBuf.getBoolean()
                        val x = bitBuf.getBits(13)
                        val y = bitBuf.getBits(13)
                        val extended = bitBuf.getBoolean()
                        assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                        assertEquals(READ_CHANGE_RESOLUTION, updateType)
                        assertFalse(updateLowResCoords)
                        assertEquals(info.avatars[playerA].coords.x, x)
                        assertEquals(info.avatars[playerA].coords.y, y)
                        assertTrue(extended)
                    }

                    run getOtherLowRes@{
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        val skipCount = bitBuf.getBits(11)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                        assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                    }
                }
            }
            run getExtendedInfo@{
                val remaining = ByteArray(buf.remaining())
                buf.get(remaining)
                assertArrayEquals(extInfoA, remaining)
            }
        }

        info.avatars[playerB].prevCoords = HighResCoord(3200, 3200)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(1, metadata.highResolutionSkip)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(3, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(5)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_5BITS, skipOpcode)
                    assertEquals(metadata.highResolutionSkip, skipCount)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(4, buf.remaining())
            run getHighRes@{
                run activeOnly@{
                    BitBuffer(buf).use { bitBuf ->
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                    }
                }
                run inactiveOnly@{
                    BitBuffer(buf).use { bitBuf ->
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                    }
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(1, metadata.highResolutionSkip)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(3, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(5)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_5BITS, skipOpcode)
                    assertEquals(metadata.highResolutionSkip, skipCount)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(1, metadata.highResolutionSkip)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(3, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(5)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_5BITS, skipOpcode)
                    assertEquals(metadata.highResolutionSkip, skipCount)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        info.avatars[playerB].coords = HighResCoord(4800, 4800)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            val client = info.clients[playerA]
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertFalse(client.isHighResolution[playerB])
            assertEquals(3, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    run readPlayerA@{
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                    }
                    run readPlayerB@{
                        val readOpcode = bitBuf.getBits(1)
                        val updateType = bitBuf.getBits(2)
                        val updateLowResCoords = bitBuf.getBoolean()
                        assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                        assertEquals(READ_CHANGE_RESOLUTION, updateType)
                        assertFalse(updateLowResCoords)
                    }
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            val client = info.clients[playerB]
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertFalse(client.isHighResolution[playerA])
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    run readPlayerA@{
                        val readOpcode = bitBuf.getBits(1)
                        val extended = bitBuf.getBoolean()
                        val updateType = bitBuf.getBits(2)
                        val updateLowResCoords = bitBuf.getBoolean()
                        assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                        assertFalse(extended)
                        assertEquals(READ_CHANGE_RESOLUTION, updateType)
                        assertFalse(updateLowResCoords)
                    }
                    run readPlayerB@{
                        val readOpcode = bitBuf.getBits(1)
                        val extended = bitBuf.getBoolean()
                        val displaceType = bitBuf.getBits(2)
                        val largeTeleport = bitBuf.getBoolean()
                        val diffLevel = bitBuf.getBits(2)
                        val diffX = bitBuf.getBits(14)
                        val diffY = bitBuf.getBits(14)
                        val expected = info.avatars[playerB].coords - info.avatars[playerB].prevCoords
                        assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                        assertFalse(extended)
                        assertEquals(READ_HIGH_RES_COORDS_TELEPORT_CHANGE, displaceType)
                        assertTrue(largeTeleport)
                        assertEquals(expected.level, diffLevel)
                        assertEquals(expected.x, diffX)
                        assertEquals(expected.y, diffY)
                    }
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                }
            }
            assertEquals(0, buf.remaining())
        }

        info.avatars[playerB].prevCoords = HighResCoord(4800, 4800)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(4, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                }
            }
            run getLowRes@{
                run inactiveOnly@{
                    BitBuffer(buf).use { bitBuf ->
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        val skipCount = bitBuf.getBits(11)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                        assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                    }
                }
                run activeOnly@{
                    BitBuffer(buf).use { bitBuf ->
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                    }
                }
            }
            assertEquals(0, buf.remaining())
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerIndex = playerB, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(4, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                }
            }
            run getLowRes@{
                run inactiveOnly@{
                    BitBuffer(buf).use { bitBuf ->
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        val skipCount = bitBuf.getBits(11)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                        assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                    }
                }
                run activeOnly@{
                    BitBuffer(buf).use { bitBuf ->
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                    }
                }
            }
            assertEquals(0, buf.remaining())
        }
    }

    /**
     * In this scenario there are two players: Player A and Player B
     *
     * Tick 1
     * Player A: logs in at coords 3200,3200
     *
     * Tick 2:
     * Player A: stationary at coords 3200,3200
     * Player B: logs in at coords 3203,3202
     *
     * Tick 3:
     * Player A: stationary at coords 3200,3200
     * Player B: stationary at coords 3203,3202
     */
    @Test
    fun testCase2() {
        val info = PlayerInfo()
        val buf = ByteBuffer.allocate(DEFAULT_BUFFER_LIMIT)
        val playerA = 1
        val playerB = 2
        val extInfoA = mockExtendedInfo(buf, "PlayerA")
        val extInfoB = mockExtendedInfo(buf, "PlayerB")

        info.register(playerA)
        info.updateCoords(playerA, HighResCoord(3200, 3200), HighResCoord(0, 0))
        info.updateExtendedInfo(playerA, extInfoA)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerA, metadata)
            assertEquals(0, metadata.highResolutionSkip)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2045, metadata.lowResolutionSkip)
            assertEquals(1, metadata.extendedInfoCount)
            assertEquals(3 + extInfoA.size, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val extended = bitBuf.getBoolean()
                    val displaceType = bitBuf.getBits(2)
                    assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                    assertTrue(extended)
                    assertEquals(READ_HIGH_RES_COORDS_DISPLACEMENT, displaceType)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 2, skipCount)
                }
            }
            run getExtendedInfo@{
                val remaining = ByteArray(buf.remaining())
                buf.get(remaining)
                assertArrayEquals(extInfoA, remaining)
            }
            assertEquals(0, buf.remaining())
        }

        info.register(playerB)
        info.updateCoords(playerA, HighResCoord(3200, 3200), HighResCoord(3200, 3200))
        info.updateCoords(playerB, HighResCoord(3203, 3202), HighResCoord(0, 0))
        info.updateExtendedInfo(playerA, byteArrayOf())
        info.updateExtendedInfo(playerB, extInfoB)
        info.cacheStaticExtendedInfo(playerA, extInfoA)
        info.cacheStaticExtendedInfo(playerB, extInfoB)

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerA, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(1, metadata.extendedInfoCount)
            assertEquals(7 + extInfoB.size, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    run getPlayerB@{
                        val readOpcode = bitBuf.getBits(1)
                        val updateType = bitBuf.getBits(2)
                        val updateLowResCoords = bitBuf.getBoolean()
                        val x = bitBuf.getBits(13)
                        val y = bitBuf.getBits(13)
                        val extended = bitBuf.getBoolean()
                        assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                        assertEquals(READ_CHANGE_RESOLUTION, updateType)
                        assertFalse(updateLowResCoords)
                        assertEquals(info.avatars[playerB].coords.x, x)
                        assertEquals(info.avatars[playerB].coords.y, y)
                        assertTrue(extended)
                    }

                    run getOtherLowRes@{
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        val skipCount = bitBuf.getBits(11)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                        assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                    }
                }
            }
            run getExtendedInfo@{
                val remaining = ByteArray(buf.remaining())
                buf.get(remaining)
                assertArrayEquals(extInfoB, remaining)
            }
            assertEquals(0, buf.remaining())
        }

        run playerB@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerB, metadata)
            assertEquals(1, metadata.highResolutionCount)
            assertEquals(2046, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(2, metadata.extendedInfoCount)
            assertEquals(7 + extInfoA.size + extInfoB.size, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val extended = bitBuf.getBoolean()
                    val displaceType = bitBuf.getBits(2)
                    assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                    assertTrue(extended)
                    assertEquals(READ_HIGH_RES_COORDS_DISPLACEMENT, displaceType)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    run getPlayerA@{
                        val readOpcode = bitBuf.getBits(1)
                        val updateType = bitBuf.getBits(2)
                        val updateLowResCoords = bitBuf.getBoolean()
                        val x = bitBuf.getBits(13)
                        val y = bitBuf.getBits(13)
                        val extended = bitBuf.getBoolean()
                        assertEquals(READ_AVATAR_INFO_OPCODE, readOpcode)
                        assertEquals(READ_CHANGE_RESOLUTION, updateType)
                        assertFalse(updateLowResCoords)
                        assertEquals(info.avatars[playerA].coords.x, x)
                        assertEquals(info.avatars[playerA].coords.y, y)
                        assertTrue(extended)
                    }

                    run getOtherLowRes@{
                        val readOpcode = bitBuf.getBits(1)
                        val skipOpcode = bitBuf.getBits(2)
                        val skipCount = bitBuf.getBits(11)
                        assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                        assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                        assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                    }
                }
            }
            run getExtendedInfo@{
                val remainingB = ByteArray(extInfoB.size)
                buf.get(remainingB)
                assertArrayEquals(extInfoB, remainingB)

                val remainingA = ByteArray(extInfoA.size)
                buf.get(remainingA)
                assertArrayEquals(extInfoA, remainingA)
            }
            assertEquals(0, buf.remaining())
        }

        info.updateCoords(playerB, HighResCoord(3203, 3202), HighResCoord(3203, 3202))

        run playerA@{
            val metadata = PlayerInfoMetadata()
            info.put(buf, playerA, metadata)
            assertEquals(2, metadata.highResolutionCount)
            assertEquals(2045, metadata.lowResolutionCount)
            assertEquals(2044, metadata.lowResolutionSkip)
            assertEquals(1, metadata.extendedInfoCount)
            assertEquals(3 + extInfoB.size, buf.remaining())
            run getHighRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_NO_BITS, skipOpcode)
                }
            }
            run getLowRes@{
                BitBuffer(buf).use { bitBuf ->
                    val readOpcode = bitBuf.getBits(1)
                    val skipOpcode = bitBuf.getBits(2)
                    val skipCount = bitBuf.getBits(11)
                    assertEquals(READ_SKIP_COUNT_OPCODE, readOpcode)
                    assertEquals(READ_SKIP_COUNT_11BITS, skipOpcode)
                    assertEquals(MAX_PLAYER_LIMIT - 3, skipCount)
                }
            }
            run getExtendedInfo@{
                val remaining = ByteArray(extInfoB.size)
                buf.get(remaining)
                assertArrayEquals(extInfoB, remaining)
            }
            assertEquals(0, buf.remaining())
        }
    }

    private fun mockExtendedInfo(buf: ByteBuffer, displayName: String): ByteArray {
        check(buf.position() == 0)
        val appearanceFlag = 0x40
        buf.put(appearanceFlag.toByte())
        val gender = 1.toByte()
        val skull = 2.toByte()
        val prayer = (-1).toByte()
        buf.put(0.toByte())
        buf.put(gender)
        buf.put(skull)
        buf.put(prayer)
        repeat(12) { buf.putShort((0x100 + it).toShort()) }
        repeat(5) { buf.put(it.toByte()) }
        repeat(7) { buf.putShort((800 + it).toShort()) }
        buf.put(displayName.toByteArray()).put(0)
        // update length header
        buf.put(1, buf.position().toByte())
        val data = ByteArray(buf.position())
        buf.flip()
        buf.get(data, 0, data.size)
        buf.clear()
        return data
    }

    private companion object {

        private const val DUMMY_INDEX = 5
    }
}
