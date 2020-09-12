package gg.rsmod.plugins.protocol.codec.login

enum class LoginResponseType(val opcode: Int) {
    NORMAL(opcode = 2),
    RECONNECT(opcode = 15),
    PROFILE_TRANSFER(opcode = 21),
    RESTART_DECODER(opcode = 23),
    CUSTOM_ERROR(opcode = 29)
}
