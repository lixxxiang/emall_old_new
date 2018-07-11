package com.example.emall_ec.main.me.collect.type

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.example.emall_core.delegates.EmallDelegate
import com.example.emall_core.net.RestClient
import com.example.emall_core.net.callback.IError
import com.example.emall_core.net.callback.IFailure
import com.example.emall_core.net.callback.ISuccess
import com.example.emall_core.util.log.EmallLogger
import com.example.emall_ec.R
import com.example.emall_ec.database.DatabaseManager
import com.example.emall_ec.main.classify.ClassifyDelegate
import com.example.emall_ec.main.index.dailypic.adapter.HomePageListViewAdapter
import com.example.emall_ec.main.index.dailypic.data.HomePageBean
import com.example.emall_ec.main.index.dailypic.pic.PicDetailDelegate
import com.example.emall_ec.main.index.dailypic.video.VideoDetailDelegate
import com.example.emall_ec.main.me.collect.CollectionDelegate
import com.example.emall_ec.main.me.collect.MyCollectionListViewAdapter
import com.example.emall_ec.main.me.collect.data.MyCollectionBean
import com.google.gson.Gson
import kotlinx.android.synthetic.main.delegate_content.*
import kotlinx.android.synthetic.main.delegate_daily_pic.*
import java.util.*


class ContentDelegate : EmallDelegate() {

    private var myCollectionParams: WeakHashMap<String, Any>? = WeakHashMap()
    private var myCollectionBean = MyCollectionBean()
    var myCollectionData: MutableList<MyCollectionBean.DataBean.CollectionsBean>? = mutableListOf()
    var adapter = MyCollectionListViewAdapter()
    var tempCollectionData: MutableList<MyCollectionBean.DataBean.CollectionsBean>? = mutableListOf()
    var firstIn = false

    fun create(): ContentDelegate? {
        return ContentDelegate()
    }

    override fun setLayout(): Any? {
        return R.layout.delegate_content
    }

    override fun initial() {
        setSwipeBackEnable(false)
        initContent()
        content_lv.setOnItemClickListener { adapterView, view, i, l ->
            if (myCollectionData!![i].type == "1") {
                val delegate: PicDetailDelegate = PicDetailDelegate().create()!!
                val bundle: Bundle? = Bundle()
                bundle!!.putString("IMAGE_ID", myCollectionData!![i].contentId)
                delegate.arguments = bundle
                getParentDelegate<CollectionDelegate>().start(delegate)
            } else if (myCollectionData!![i].type == "2") {
                val delegate: VideoDetailDelegate = VideoDetailDelegate().create()!!
                val bundle: Bundle? = Bundle()
                bundle!!.putString("VIDEO_ID", myCollectionData!![i].contentId)
                delegate.arguments = bundle
                getParentDelegate<CollectionDelegate>().start(delegate)
            }
        }

        content_content_srv.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            content_content_srv.isRefreshing = true
            Handler().postDelayed({
                initContent()
                content_content_srv.isRefreshing = false
            }, 1200)
        })

        collection_content_btn.setOnClickListener {
            val delegate = ClassifyDelegate().create()
            val bundle: Bundle? = Bundle()
            bundle!!.putString("TYPE", "SCENE")
            delegate!!.arguments = bundle
            getParentDelegate<CollectionDelegate>().start(delegate)
        }
    }

    private fun initContent() {
        myCollectionParams!!["userId"] = DatabaseManager().getInstance()!!.getDao()!!.loadAll()[0].userId
        myCollectionParams!!["pageSize"] = "5"
        myCollectionParams!!["pageNum"] = "1"
        myCollectionParams!!["client"] = "android"

        RestClient().builder()
                .url("http://202.111.178.10:28085/mobile/myCollection")
                .params(myCollectionParams!!)
                .success(object : ISuccess {
                    @SuppressLint("ApplySharedPref")
                    override fun onSuccess(response: String) {
                        EmallLogger.d(response)
                        myCollectionBean = Gson().fromJson(response, MyCollectionBean::class.java)

                        myCollectionData!!.clear()



                        if (myCollectionBean.message == "success") {
                            for (i in 0 until myCollectionBean.data.collections.size)
                                myCollectionData!!.add(myCollectionBean.data.collections[i])
                            if (content_lv.visibility == View.GONE) {
                                content_lv.visibility = View.VISIBLE
                                collection_content_no_result_rl.visibility = View.GONE
                            }

                            adapter = MyCollectionListViewAdapter(context, myCollectionData)
                            adapter.notifyDataSetChanged()
                            content_lv.adapter = adapter

                        } else {
                            content_lv.visibility = View.GONE
                            collection_content_no_result_rl.visibility = View.VISIBLE
                        }

                    }
                })
                .failure(
                        object : IFailure {
                            override fun onFailure() {}
                        })
                .error(
                        object : IError {
                            override fun onError(code: Int, msg: String) {}
                        })
                .build()
                .post()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        myCollectionData!!.clear()
        initContent()

    }
}