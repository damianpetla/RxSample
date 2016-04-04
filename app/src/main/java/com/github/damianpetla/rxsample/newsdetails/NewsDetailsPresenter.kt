package com.github.damianpetla.rxsample.newsdetails

import com.github.damianpetla.rxsample.model.NewsDataSource

/**
 * Created by loop on 04/04/16.
 */
class NewsDetailsPresenter(val newsDataSource: NewsDataSource, val newsView: NewsDetailsContract.View) : NewsDetailsContract.Presenter{

    override fun loadNews(position: Int) = newsDataSource.getNews(position)
}