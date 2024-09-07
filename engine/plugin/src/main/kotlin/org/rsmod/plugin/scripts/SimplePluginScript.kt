package org.rsmod.plugin.scripts

/**
 * A base class for simple plug-in scripts that avoids boilerplate code if desired by the caller.
 */
public open class SimplePluginScript : PluginScript() {
    override fun ScriptContext.startUp() {}
}
