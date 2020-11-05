package org.rsmod.game.model.domain.repo

interface Repository<ID, T> {

    fun entries(): Map<ID, T>

    fun keys(): Collection<ID>

    fun values(): Collection<T>

    fun findById(id: ID): T?

    fun insert(entity: T, id: ID)

    fun update(entity: T, id: ID)

    fun delete(entity: T, id: ID)

    operator fun get(id: ID) = findById(id)

    operator fun set(id: ID, entity: T) = insert(entity, id)
}
