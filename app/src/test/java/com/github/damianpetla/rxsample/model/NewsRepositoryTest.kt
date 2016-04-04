package com.github.damianpetla.rxsample.model

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by loop on 04/04/16.
 */
class NewsRepositoryTest {

    lateinit var repo: NewsRepository

    @Mock
    lateinit var localDataSource: NewsDataSource

    @Mock
    lateinit var remoteDataSource: NewsDataSource

    lateinit var mockList: List<News>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repo = NewsRepository(localDataSource, remoteDataSource)
        mockList = listOf(createFakeNews())
        Mockito.`when`(localDataSource.getNewsList()).thenReturn(Observable.empty())
        Mockito.`when`(remoteDataSource.getNewsList()).thenReturn(Observable.defer { Observable.just(mockList) })
    }

    private fun createFakeNews() = News("Title", "Description", "Damian", "Omni")

    @Test
    fun getNewsList_second_subscriber() {
        var testSubscriber = TestSubscriber.create<List<News>>()
        repo.getNewsList().subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValue(mockList)
        //repeat call
        testSubscriber = TestSubscriber.create()
        repo.getNewsList().subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValue(mockList)

        Mockito.verify(remoteDataSource, Mockito.times(1)).getNewsList()
    }

    @Test
    fun getNewsList_after_unsubscribe() {
        var testSubscriber = TestSubscriber.create<List<News>>()
        repo.getNewsList().observeOn(Schedulers.newThread()).subscribe(testSubscriber)
        testSubscriber.unsubscribe()
        testSubscriber.awaitTerminalEventAndUnsubscribeOnTimeout(2, TimeUnit.SECONDS)
        testSubscriber.assertNoValues()

        //repeat call
        testSubscriber = TestSubscriber.create()
        repo.getNewsList().subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()
        testSubscriber.assertValue(mockList)

        Mockito.verify(remoteDataSource, Mockito.times(1)).getNewsList()
    }

    @Test
    fun clearNewsList_clear() {
        repo.clearNewsList()
        Mockito.verify(localDataSource).clearNewsList()
        Mockito.verify(remoteDataSource).clearNewsList()
    }

}