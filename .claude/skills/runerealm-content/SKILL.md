---
name: runerealm-content
description: Conventions, pitfalls and test workflow for the RuneRealm OSRS server (Kotlin, rev 233). Use before adding or changing game content (skills, NPCs, locs, interfaces, items, combat, quests), type edits, `.data/symbols`, or running/writing integration tests in this repo.
---

# RuneRealm content development

RuneRealm is an OSRS game server (revision 233, Kotlin, packages `org.runerealm.*`), played with
the RSProx RuneLite client. **The OSRS Wiki is the source of truth** for behaviour, messages,
levels, XP and formulas. Cite the wiki page in a KDoc when porting behaviour.

## Project rules
- **No members distinction.** Every player is a member (`Player.members` is always true). Never
  add a members/F2P check. Ignore "members only" on the wiki.
- Rename leftovers: the product is **RuneRealm**. Never write "RuneScape" or "RS Mod" in
  player-facing text. Wiki URLs (`oldschool.runescape.wiki`) are fine.

## Where things live
- `api/*`: shared engine-facing APIs (combat, death, player, net handlers, area checks). Content
  must not be depended on by `api`.
- `content/<area>/<module>`: game content as `PluginScript` classes with
  `ScriptContext.startup()`. New modules under `content/` are auto-included in the build.
- Look at a similar existing module first and copy its structure. Good examples:
  `content/skills/{woodcutting,mining,smithing,cooking,fletching,crafting}`,
  `content/areas/city/lumbridge`.

## Gotchas that have broken things before
1. **Keyed events allow only ONE handler per key.** For example `onOpNpc1(type)`,
   `onOpLoc1(type)`, `onIfOpen(interface)`, `onOpPlayerN` and
   `SessionStateEvent.EngineLogin`. A duplicate crashes server startup. Grep before adding one,
   and use the unbound `onPlayerLogin` for extra login logic.
2. **Use the cache's op slot.** Check the npc/loc/obj op list in the cache (`locTypes`,
   `npcTypes`, `objTypes` or the symbol dumps) before choosing `onOpNpc2` vs `onOpNpc3`. A wrong
   op number means the right-click option does nothing.
3. **Multi-NPCs publish ops under the base NPC id.** Register handlers on the base type.
4. **TypeReferences objects must not be `private`.** Use `internal`, or startup crashes.
5. **A LocEditor/ObjEditor op edit replaces the whole op list.** Restate every op you want to
   keep.
6. **Removing a type edit does NOT repack an already-packed game cache.** Servers keep serving
   the old values. When you change what an edit sets, make the edit restate the final (even
   vanilla) values explicitly.
7. **Server-only types** (varps, timers, queues, params) get hand-added ids in
   `.data/symbols/*.sym`. Pick the next free id, and when merging branches check for id
   clashes. Server varps count down from 65535, params down from 65385.
8. **Dynamic interface components** need `ifSetEvents` from the server before the client sends
   their ops.
9. **Modal handling.** `ifClose` asserts all modals are closed, so don't open a modal from inside
   an `onIfClose` callback; defer it with a one-shot soft timer.
10. **openrs2 `Cache`.** After writing a group, reading it again in the same session fails, so
    read everything first and then write. Never keep the js5 cache open across the type
    updater's repack.

## Tests
- Integration tests live in `src/integration/kotlin` and run with
  `./gradlew :path:to:module:integration`. **The `test` task does NOT run them.**
- Use `runGameTest(ScriptClass::class) { ... }` and the helpers in
  `api/testing/.../GameTestScope.kt` (`opNpc1`, `opLoc1`, `opHeld`, `advance`,
  `assertMessageSent`, `placeMapLoc`, `teleport`, ...).
- Messages are only visible during the tick they are sent, so advance tick by tick when asserting
  on them.
- Some ground-obj tests need `player.observerUUID = 1`. Teleport destinations need allocated
  zone collision (the `teleport` helper does this).
- Within one `GameTestScope`, avoid ~45+ register/spawn cycles; batch with fresh scopes.
- Run one module at a time with `--max-workers=1`. Concurrent test JVMs clash on the network port
  ("Address already in use") or get OOM-killed (exit 137); just rerun.
- The first run after adding types repacks the cache and can hang. Kill only your own test JVM
  (`ps aux | grep "[G]radle Test Executor" | grep "$(pwd)"`), never `pkill -f`, then rerun.
- Run `./gradlew spotlessApply` before committing.

## Worktrees / fresh checkouts
A fresh checkout or worktree has no game cache or keys. Either run `./gradlew install`, or copy
these from the main checkout's `.data/` (they are gitignored, never commit them):
`cache/vanilla`, `cache/xteas.json`, `game.key`, `client.key`, `server.toml`.
