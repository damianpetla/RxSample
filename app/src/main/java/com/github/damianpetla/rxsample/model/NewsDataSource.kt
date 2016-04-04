package com.github.damianpetla.rxsample.model

import rx.Observable

/**
 * Created by loop on 01/04/16.
 */
interface NewsDataSource {

    fun getNewsList() : Observable<List<News>>

    fun storeNewsList(newses: List<News>)

    fun clearNewsList()

    fun getNews(position: Int) : Observable<News>
}