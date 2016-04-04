package com.github.damianpetla.rxsample.newsdetails

import com.github.damianpetla.rxsample.model.News
import rx.Observable

/**
 * Created by loop on 04/04/16.
 */
interface NewsDetailsContract {

    interface View

    interface Presenter {

        fun loadNews(position: Int) : Observable<News>
    }
}