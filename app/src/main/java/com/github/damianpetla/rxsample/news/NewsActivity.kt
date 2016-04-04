package com.github.damianpetla.rxsample.news

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.damianpetla.rxsample.R
import com.github.damianpetla.rxsample.newsdetails.NewsDetailsActivity
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.activity_news_list.*
import kotlinx.android.synthetic.main.item_news_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class NewsActivity : AppCompatActivity(), NewsContract.View, AnkoLogger {

    lateinit var presenter: NewsContract.Presenter
    var subscribers = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)
        swiperefreshlayout.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE)
        swiperefreshlayout.setOnRefreshListener {
            startLoad(true)
        }
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = NewsListAdapter(this)
        presenter = NewsPresenter(appKodein().instance(), this)
    }

    override fun onStart() {
        super.onStart()
        info("Start loading news")
        swiperefreshlayout.post {
            startLoad(false)
        }
    }

    private fun startLoad(force: Boolean) {
        swiperefreshlayout.isRefreshing = true
        subscribers.add(presenter.loadNewsList(force).observeOn(AndroidSchedulers.mainThread()).subscribe({
            swiperefreshlayout.isRefreshing = false
            showTechniqueList(it)
        }, {
            swiperefreshlayout.isRefreshing = false
            Snackbar.make(swiperefreshlayout, "Loading news failed!", Snackbar.LENGTH_SHORT).show()
            error("Loading news list failed!", it)
        }))
    }

    override fun onStop() {
        subscribers.clear()
        super.onStop()
    }

    private fun showTechniqueList(list: List<NewsItemView>) {
        val adapter = recyclerview.adapter as NewsListAdapter
        adapter.items.clear()
        adapter.items.addAll(list)
        adapter.notifyDataSetChanged()
    }

    class NewsListAdapter(val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items: MutableList<NewsItemView> = arrayListOf()

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            return NewsItemHolder(activity.layoutInflater.inflate(R.layout.item_news_list, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val item = items[position]
            holder as NewsItemHolder
            holder.textView.text = item.title
            holder.subtitleView.text = item.subtitle
        }

    }

    class NewsItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.itemTitleView
        val subtitleView: TextView = view.itemSubtitleView

        init {
            itemView.setOnClickListener {
                view.context.startActivity<NewsDetailsActivity>(NewsDetailsActivity.EXTRA_POSITION to adapterPosition)
            }
        }
    }
}
