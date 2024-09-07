## TODO
- Explain types' computeIdentityHash.
- Nested EnumTypes in EnumProvider. Due to type-erasure, it is extremely tedious
to even begin to try and make the inner EnumTypes type-safe (checked during
boot-up). For now, we will just trust that the developers are careful (BAD!),
but solely because the other choice is to load them via reflection and try to
do comparisons that way, which may have a significant impact in future
scalability when it comes to boot-up performance.
- InvTransactions uses a cached singleton. It would become annoying for coders
to have to constantly get access to the required "lookup" maps in order to
perform an inv transaction. We willfully allow this antipattern for ease-of-use.
- Private type reference subclasses not allowed. Must be internal or public.
- .local in symbols directory being loaded by default and overrides any possible
colliding symbols from the root symbols.
- All game-related randoms should be generated through `GameRandom` functions.
- The great teleport/telejump tragedy of 24' where we had to choose between
anti-patterns that would blow a huge hit to testability or jeopardizing
user-friendliness by requiring the what-should-be simple teleport functions to
take a [CollisionFlagMap] instance arg. We went with the latter. The biggest
weakness of this approach is increasing the barrier-to-entry when a developer
wants to perform simple tasks. Teleporting _should_ be a simple task, but alas,
if we want to emulate core mechanics, teleporting needs to _instantly_ update
the collision flag map. We weighed our options from using singletons of the
collision map, to passing the collision map to each player/npc, to having a pub
sub system strictly for map changes - though at that point it'd be practically
the same thing as passing the game map pointer to the entities in question.
The solution of taking in the collision flag map as an arg is something that
will, *eventually*, solve itself by way of adding API-friendly wrapper classes
for systems such as "fade out" teleports, teleporting after interacting with a
loc, and so on. Though admittedly, it's a cumbersome ask for beginners to have
to deal with until those systems are in place.
- Decision to not make ClientScript arguments type-safe. Just not worth it,
especially if you consider that the arguments would have to be converted to
their primitive form dynamically during runtime.
- `delay` functions, AKA `p_delay`, does _not_ follow the "official" convention.
This means that calling `delay(1)` will delay the player by 1 tick, not 2.
`delay(0)` will not delay the player at all.
- Mention the purpose of each api submodule (in a README within api module).

## Types
```
engine
    └─ game
        └─ type
```
#### <u>_Internal `id` field_</u>
```kotlin
class ObjType(internal var id: Int?)
```
There was a conscious decision to allow for configuration types' "id" to be
mutable, and nullable.
#### What problem does this solve?
- Type-safety:
  - For years, frameworks used magic numbers for config types, whether it was
    for worn obj conditions, or to define a rewards table. This comes with
    obvious flaws (see [magic-numbers]). With time people moved onto static
    classes that held each individual id with a reasonably-named field. Again,
    this still comes with its problems. For example, you are still able to
    compare an obj to a constant that's not an obj:
    `if (player.righthand == Spotanims.iron_arrow_projectile)`.
- Consistency between references and configs in cache:
  - Another major flaw with the traditional approach, is that there was never a
    feasible way to make sure all references actually exist in the game cache.
    As the developer, you just had to trust that these constants were up-to-date
    and their naming stayed 100% true to what they originally meant to
    represent.
- Creating custom config types:
  - TODO:
#### What are the pros and cons?
- Cons
  - Can be abused and the `id` may not be accurate after runtime if a developer
    sees fit to modify it.
  - TODO:
- Pros
  - TODO:

[magic-numbers]: https://en.wikipedia.org/wiki/Magic_number_(programming)
