package org.rsmod.plugins.api.lang

public sealed class APIException(public override val message: String) : Throwable() {

    public class KeyedEventAlreadyMapped(message: String) : APIException(message)
}
