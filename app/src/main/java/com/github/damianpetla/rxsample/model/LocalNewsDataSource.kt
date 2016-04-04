package com.github.damianpetla.rxsample.model

import rx.Observable

/**
 * Created by loop on 01/04/16.
 */
class LocalNewsDataSource : NewsDataSource {

    override fun getNewsList(): Observable<List<News>> {
        return Observable.empty()
    }

    override fun storeNewsList(newses: List<News>) {
        //TODO
    }

    override fun clearNewsList() {
        //TODO
    }

    override fun getNews(position: Int): Observable<News> {
        throw UnsupportedOperationException()
    }
}