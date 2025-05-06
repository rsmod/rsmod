package org.rsmod.api.realm.config.updater

import jakarta.inject.Inject
import java.lang.ref.WeakReference
import org.rsmod.api.realm.Realm
import org.rsmod.api.realm.RealmConfig

public class RealmConfigUpdater @Inject constructor(private val realm: Realm) {
    private var writeAccessThread = WeakReference<Thread>(null)
    private var enforceThreadCheck = false

    public fun update(newConfig: RealmConfig) {
        assertWriteAccess()
        realm.updateConfig(newConfig)
    }

    public fun attachWriteThread(thread: Thread) {
        writeAccessThread = WeakReference(thread)
        enforceThreadCheck = true
    }

    public fun allowUnsafeUpdates() {
        enforceThreadCheck = false
    }

    private fun assertWriteAccess() {
        if (!enforceThreadCheck) {
            return
        }
        val expected = writeAccessThread.get()
        if (expected == null) {
            val message = "Write thread has been garbage collected; cannot assert write access."
            throw IllegalStateException(message)
        }

        if (expected != Thread.currentThread()) {
            val message =
                "Realm config update is not permitted from non-write thread. " +
                    "(expected=${expected.name}, actual=${Thread.currentThread().name})"
            throw IllegalStateException(message)
        }
    }
}
