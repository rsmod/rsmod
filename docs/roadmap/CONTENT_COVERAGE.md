# OSRS Content Coverage Inventory

Use this as the top-level completeness audit. Split each row into smaller issues when an individual entity or behavior can be implemented and tested independently. This is a taxonomy checklist, not a pre-enumerated list of every individual OSRS entity.

Legend: `[ ]` not audited · `[~]` partial/in progress · `[x]` verified · `[-]` intentionally excluded (explain why)

## Shared engine and runtime
- [ ] Protocol, login/session, packets, client synchronization
- [ ] Tick scheduler, queues, timers, event ordering
- [ ] Entity lifecycle: player, NPC, object, ground item, projectile, spot graphic
- [ ] Pathfinding, collision, line of sight, reachability, movement
- [ ] Region loading, instancing, map/landscape data, world flags
- [ ] Persistence, account variables, quest variables, settings, unlocks
- [ ] Containers, inventory, bank, equipment, trade, shop, exchange
- [ ] Interfaces/widgets, dialogue, chat, animations, graphics, sounds/music
- [ ] Definitions/data loading, validation, ID/name cross-reference and migration
- [ ] Admin/debug tooling, logging, metrics, security and anti-duplication safeguards

## Skills — audit each skill's actions, tools, requirements, products, XP, failure states and unlocks
- [ ] Attack
- [ ] Strength
- [ ] Defence
- [ ] Ranged
- [ ] Prayer
- [ ] Magic
- [ ] Runecraft
- [ ] Hitpoints
- [ ] Crafting
- [ ] Mining
- [ ] Smithing
- [ ] Fishing
- [ ] Cooking
- [ ] Firemaking
- [ ] Woodcutting
- [ ] Agility
- [ ] Herblore
- [ ] Thieving
- [ ] Fletching
- [ ] Slayer
- [ ] Farming
- [ ] Construction
- [ ] Hunter
- [ ] Sailing — verify exact live game revision and Wiki scope

## Combat and progression
- [ ] Melee, ranged, magic, special attacks, styles and combat formulas
- [ ] Player combat stats, boosts/drains, prayer effects, status effects
- [ ] NPC combat definitions, aggression, retaliate, immunities, animations
- [ ] Death, respawn, grave/reclaim, wilderness death drops, loot ownership
- [ ] Slayer tasks, streaks, points, unlocks, superior creatures
- [ ] Spells, spellbooks, rune costs, target rules, autocast and effects
- [ ] Prayers, drain rates, overheads, protection, activation conflicts
- [ ] Equipment bonuses, requirements, set effects, charges, degradation, ammo
- [ ] Bosses and variants (each boss/variant should become a separate audited record)
- [ ] Raids: Chambers of Xeric, Theatre of Blood, Tombs of Amascut
- [ ] Wilderness PvP, skulls, wilderness level, multi-combat, loot keys

## Quests, tasks and unlocks
- [ ] All quests from current Wiki quest list, including prerequisite chains
- [ ] Miniquests and special quest variants where applicable
- [ ] Quest stages, dialogue branches, item/object/NPC interactions, rewards
- [ ] Quest points, journal text, post-quest dialogue, repeatable quest states
- [ ] Achievement Diaries: all regions and tiers
- [ ] Combat Achievements: all tasks, tiers, unlocks and rewards
- [ ] Collection Log categories, progress and unlock hooks
- [ ] Music unlocks, emotes, pets, areas, teleports and account unlocks

## World and geography
- [ ] All overworld regions and subregions
- [ ] Cities, villages, dungeons, caves, towers, ruins and instance maps
- [ ] All NPC spawn points, object spawns, ground spawns and region attributes
- [ ] Doors, gates, ladders, stairs, bridges, traps, shortcuts and agility obstacles
- [ ] Teleports, ships, charter ships, fairy rings, spirit trees, gliders, minecarts
- [ ] Wilderness boundaries, PvP rules, multi-combat, safe zones and instanced arenas
- [ ] Farming patches, POH/Construction areas and player-owned content
- [ ] Region music, ambient/environmental behavior and map transitions

## Entities and definitions — require individual records and IDs
- [ ] Items: base items, variants, noted/placeholder forms, charges, transforms
- [ ] NPCs: dialogue, shops, combat stats, drops, transforms, respawns and actions
- [ ] Monsters: stats, behavior, animations, graphics, drops, immunities and locations
- [ ] Objects: actions, state changes, collision, animations, replacements and coordinates
- [ ] Ground items and item spawn behavior
- [ ] Projectiles, spot animations, animations and graphics required by server behavior
- [ ] Item-on-item, item-on-NPC, item-on-object, item-on-player, spell-on-target interactions
- [ ] Examine text, action labels, option menus, dialogue trees and interaction edge cases

## Economy and item systems
- [ ] Inventory, banking, notes, placeholders, deposits, withdrawals, searches
- [ ] Equipment, gear requirements, set bonuses, degradables, charged items
- [ ] Shops: stock, restock, buy/sell pricing, currencies, specialty stock
- [ ] Trading: offer updates, confirmation, cancellation, persistence and exploit checks
- [ ] Grand Exchange: offers, matching, limits, taxes, collection and persistence (if in scope)
- [ ] Item creation/consumption recipes, resource sinks, rewards and currency flows
- [ ] Drop tables, rare/unique drops, tertiary drops, pets, clue rewards and rates
- [ ] Death drops, ground item ownership, visibility timers and despawn behavior

## Minigames and activities — enumerate every current Wiki-listed entry
- [ ] All minigames, entry rules, rounds, score, rewards and exit conditions
- [ ] All activities and recurring activities
- [ ] Clue scrolls: all tiers, step types, puzzles, ciphers, coordinates and rewards
- [ ] Treasure Trails, reward caskets, unique tables and collection log
- [ ] Random events, eligibility, behavior and rewards
- [ ] Skilling bosses and skilling activities
- [ ] PvP activities and competitive modes
- [ ] Seasonal/holiday events
- [ ] Temporary modes: Leagues, Deadman and other modes (explicitly define target support)
- [ ] Pets, emotes, music, collection logs and achievements

## UI, social and supporting systems
- [ ] Interfaces/widgets and interaction synchronization
- [ ] Dialogue, chat options, quest journals and notifications
- [ ] Friends/ignore/chat channels and clans (if in scope)
- [ ] Party/group state, raid grouping and shared encounter rules
- [ ] Account settings, game options, accessibility/client compatibility
- [ ] Death interface, bank/shop/trade/exchange interfaces
- [ ] Music system, emotes, pets and follower interactions

## Verification
- [ ] Unit tests for shared mechanics and formula boundaries
- [ ] Integration tests for representative gameplay journeys
- [ ] Golden tests for XP, rewards, drop rates, dialogue branches and tick timing
- [ ] Content definition consistency checks and unresolved-reference report
- [ ] Current Wiki review date and source URL recorded for each inventory slice
- [ ] Implemented/tested/verified/intentional-exclusion counts reported by domain
- [ ] Known differences from the current game documented
