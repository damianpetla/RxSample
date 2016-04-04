package com.github.damianpetla.rxsample.news

import com.github.damianpetla.rxsample.model.News
import com.github.damianpetla.rxsample.model.NewsDataSource
import rx.Observable

/**
 * Created by loop on 26/03/16.
 */
class NewsPresenter(val dataSource: NewsDataSource, val view: NewsContract.View) : NewsContract.Presenter {

    override fun loadNewsList(force: Boolean): Observable<List<NewsItemView>> {
        if (force)
            dataSource.clearNewsList()
        return dataSource.getNewsList().flatMap { Observable.from(it) }.map { it.mapToItemView() }.toList()
    }

    fun News.mapToItemView() = NewsItemView(title, description)

}