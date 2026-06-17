package com.muedsa.tvbox.douyu

import com.muedsa.tvbox.api.plugin.IPlugin
import com.muedsa.tvbox.api.plugin.PluginOptions
import com.muedsa.tvbox.api.plugin.TvBoxContext
import com.muedsa.tvbox.api.service.IMainScreenService
import com.muedsa.tvbox.api.service.IMediaCatalogService
import com.muedsa.tvbox.api.service.IMediaDetailService
import com.muedsa.tvbox.api.service.IMediaSearchService
import com.muedsa.tvbox.api.store.IPluginPerfStore
import com.muedsa.tvbox.douyu.service.DouyuMobileService
import com.muedsa.tvbox.douyu.service.DouyuService
import com.muedsa.tvbox.douyu.service.MainScreenService
import com.muedsa.tvbox.douyu.service.MediaCatalogService
import com.muedsa.tvbox.douyu.service.MediaDetailService
import com.muedsa.tvbox.douyu.service.MediaSearchService
import com.muedsa.tvbox.tool.IPv6Checker
import com.muedsa.tvbox.tool.PluginCookieJar
import com.muedsa.tvbox.tool.SharedCookieSaver
import com.muedsa.tvbox.tool.createJsonRetrofit
import com.muedsa.tvbox.tool.createOkHttpClient

class DouyuPlugin(tvBoxContext: TvBoxContext) : IPlugin(tvBoxContext = tvBoxContext) {

    private val store: IPluginPerfStore = tvBoxContext.store

    private val cookieSaver by lazy { SharedCookieSaver(store = store) }

    override var options: PluginOptions = PluginOptions(enableDanDanPlaySearch = true)

    override suspend fun onInit() {}

    override suspend fun onLaunched() {}

    private val okHttpClient by lazy {
        createOkHttpClient(
            debug = tvBoxContext.debug,
            cookieJar = PluginCookieJar(saver = cookieSaver),
            onlyIpv4 = tvBoxContext.iPv6Status != IPv6Checker.IPv6Status.SUPPORTED
        )
    }

    private val douyuService by lazy {
        createJsonRetrofit(
            baseUrl = DouyuConst.PC_URL,
            service = DouyuService::class.java,
            okHttpClient = okHttpClient,
        )
    }

    private val douyuMobileService by lazy {
        createJsonRetrofit(
            baseUrl = DouyuConst.MOBILE_URL,
            service = DouyuMobileService::class.java,
            okHttpClient = okHttpClient,
        )
    }

    private val mainScreenService by lazy {
        MainScreenService(
            douyuService = douyuService,
            douyuMobileService = douyuMobileService,
            debug = tvBoxContext.debug,
        )
    }
    private val mediaDetailService by lazy {
        MediaDetailService(
            douyuService = douyuService,
            okHttpClient = okHttpClient,
            store = store,
        )
    }
    private val mediaSearchService by lazy { MediaSearchService(douyuService = douyuService) }
    private val mediaCatalogService by lazy { MediaCatalogService(douyuMobileService = douyuMobileService) }

    override fun provideMainScreenService(): IMainScreenService = mainScreenService

    override fun provideMediaDetailService(): IMediaDetailService = mediaDetailService

    override fun provideMediaSearchService(): IMediaSearchService = mediaSearchService

    override fun provideMediaCatalogService(): IMediaCatalogService = mediaCatalogService
}