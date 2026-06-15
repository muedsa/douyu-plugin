package com.muedsa.tvbox.douyu

import com.muedsa.tvbox.api.plugin.TvBoxContext
import com.muedsa.tvbox.tool.IPv6Checker

val TestPlugin by lazy {
    DouyuPlugin(
        tvBoxContext = TvBoxContext(
            screenWidth = 1920,
            screenHeight = 1080,
            debug = true,
            store = FakePluginPrefStore(),
            iPv6Status = IPv6Checker.checkIPv6Support()
        )
    )
}