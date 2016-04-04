package com.github.damianpetla.rxsample.model

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by loop on 01/04/16.
 */
class RemoteNewsDataSource : NewsDataSource {

    val mockService: MockService = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl("http://www.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MockService::class.java)

    override fun getNewsList(): Observable<List<News>> {
        return mockService.getNewsList("http://www.mocky.io/v2/570299291000002228904527")
                .subscribeOn(Schedulers.io())
    }

    override fun clearNewsList() {
    }

    override fun storeNewsList(newses: List<News>) {
        throw UnsupportedOperationException("We should never store on remote")
    }

    interface MockService {

        @GET
        fun getNewsList(@Url url: String): Observable<List<News>>
    }

    override fun getNews(position: Int): Observable<News> {
        throw UnsupportedOperationException()
    }
}