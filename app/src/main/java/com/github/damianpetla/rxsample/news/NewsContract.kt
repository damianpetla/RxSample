package com.github.damianpetla.rxsample.news

import rx.Observable

/**
 * Created by loop on 26/03/16.
 */
interface NewsContract {

    interface View

    interface Presenter {

        fun loadNewsList(force: Boolean) : Observable<List<NewsItemView>>

    }
}