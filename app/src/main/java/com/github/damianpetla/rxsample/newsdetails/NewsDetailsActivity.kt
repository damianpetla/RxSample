package com.github.damianpetla.rxsample.newsdetails

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.damianpetla.rxsample.R
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.activity_newsdetails.*
import org.jetbrains.anko.toast
import rx.android.schedulers.AndroidSchedulers

class NewsDetailsActivity : AppCompatActivity(), NewsDetailsContract.View {

    companion object {
        val EXTRA_POSITION = "position"
    }

    lateinit var presenter: NewsDetailsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsdetails)
        presenter = NewsDetailsPresenter(appKodein().instance(), this)
    }

    override fun onStart() {
        super.onStart()
        presenter.loadNews(intent.getIntExtra(EXTRA_POSITION, 0)).observeOn(AndroidSchedulers.mainThread()).subscribe({
            titleView.text = it.title
            descriptionView.text = it.description
            authorView.text = it.author
            sourceView.text = it.source
        },{
            toast("News error. OMG!")
        })
    }
}
