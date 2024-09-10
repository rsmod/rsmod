package org.rsmod.api.testing.factory.font

import org.rsmod.game.type.font.FontMetricsTypeBuilder
import org.rsmod.game.type.font.UnpackedFontMetricsType

public class TestFontMetricsTypeFactory {
    public fun create(
        id: Int = 0,
        init: FontMetricsTypeBuilder.() -> Unit = {},
    ): UnpackedFontMetricsType {
        val builder = FontMetricsTypeBuilder().apply { internal = "test_font_metrics_type" }
        return builder.apply(init).build(id)
    }
}
