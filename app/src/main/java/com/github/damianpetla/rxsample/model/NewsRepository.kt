package com.github.damianpetla.rxsample.model

import rx.Observable
import rx.lang.kotlin.withIndex
import rx.observables.ConnectableObservable

/**
 * Created by loop on 01/04/16.
 */
class NewsRepository(val localDataSource: NewsDataSource, val remoteDataSource: NewsDataSource) : NewsDataSource {

    var request: ConnectableObservable<List<News>>? = null

    override fun getNewsList(): Observable<List<News>> {
        if (request == null) {
            val localWithSave = localDataSource.getNewsList()
            val networkWithSave = remoteDataSource.getNewsList().doOnNext {
                localDataSource.storeNewsList(it)
            }
            request = Observable.concat(localWithSave, networkWithSave).first().replay()
            request!!.connect()
        }

        return request!!
    }

    override fun clearNewsList() {
        request?.connect { it.unsubscribe() }
        request = null
        localDataSource.clearNewsList()
    }

    override fun storeNewsList(newses: List<News>) {
        throw UnsupportedOperationException()
    }

    /*
    Use cached data
     */
    override fun getNews(position: Int): Observable<News> = getNewsList()
            .flatMap { Observable.from(it) }
            .withIndex()
            .first { it.index == position }
            .map { it.value }
}