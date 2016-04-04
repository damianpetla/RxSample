package com.github.damianpetla.rxsample

import android.app.Application
import com.github.damianpetla.rxsample.model.LocalNewsDataSource
import com.github.damianpetla.rxsample.model.NewsDataSource
import com.github.damianpetla.rxsample.model.NewsRepository
import com.github.damianpetla.rxsample.model.RemoteNewsDataSource
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.android.KodeinApplication
import com.github.salomonbrys.kodein.singleton

/**
 * Created by loop on 26/03/16.
 */
class NewsApplication : Application(), KodeinApplication {

    override val kodein = Kodein {
        bind<NewsDataSource>("local") with singleton { LocalNewsDataSource() }
        bind<NewsDataSource>("remote") with singleton { RemoteNewsDataSource() }
        bind<NewsDataSource>() with singleton { NewsRepository(instance("local"), instance("remote")) }
    }

    override fun onCreate() {
        super.onCreate()
    }

}