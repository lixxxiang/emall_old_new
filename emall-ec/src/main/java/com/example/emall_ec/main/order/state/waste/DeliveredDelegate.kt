package com.example.emall_ec.main.order.state.waste

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.AbsListView
import com.example.emall_core.delegates.EmallDelegate
import com.example.emall_ec.main.bottom.BottomItemDelegate
import com.example.emall_core.ui.progressbar.EmallProgressBar
import com.example.emall_core.util.log.EmallLogger
import com.example.emall_ec.R
import com.example.emall_ec.database.DatabaseManager
import com.example.emall_ec.api.ApiService
import com.example.emall_ec.api.NetUtils
import com.example.emall_ec.main.order.OrderDetailDelegate
import com.example.emall_ec.main.order.state.adapter.DeliveredListAdapter
import com.example.emall_ec.main.order.state.data.OrderDetail
import kotlinx.android.synthetic.main.delegate_delivered.*
import retrofit2.Retrofit
import java.util.*

/**
 * Created by lixiang on 2018/3/5.
 */
class DeliveredDelegate : EmallDelegate() {
    private var orderDetail = OrderDetail()
    private var data: MutableList<OrderDetail>? = mutableListOf()
    var findOrderListByUserIdParams: WeakHashMap<String, Any>? = WeakHashMap()
    var inited = false
    var adapter: DeliveredListAdapter? = null
    var delegate: DeliveredDelegate? = null
    internal var retrofit: Retrofit? = null
    internal var apiService: ApiService? = null
    override fun setLayout(): Any? {
        return R.layout.delegate_delivered
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initial() {
        setSwipeBackEnable(false)
        val head = View.inflate(activity, R.layout.orderlist_head_view, null)
        delivered_lv.addHeaderView(head)
        delegate = this

        data()
        delivered_lv.setOnItemClickListener { adapterView, view, i, l ->

            val delegate: OrderDetailDelegate = OrderDetailDelegate().create()!!
            val bundle: Bundle? = Bundle()
            bundle!!.putString("KEY", "ID")
            bundle.putParcelable("KEy", orderDetail)
            bundle.putInt("INDEX", i - 1)

            delegate.arguments = bundle
//            start(delegate)
//            showHideFragment(OrderDetailDelegate(), getParentDelegate())
//            getParentDelegate.start(OrderDetailDelegate())
            (parentFragment as BottomItemDelegate).start(delegate)

        }

        delivered_lv.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val firstView = view.getChildAt(firstVisibleItem)
                delivered_srl.isEnabled = firstVisibleItem == 0 && (firstView == null || firstView.top == 0)
            }
        })

        delivered_srl.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            adapter = null
            delivered_srl.isRefreshing = true
            Handler().postDelayed({
                data()
                delivered_srl.isRefreshing = false
            }, 1200)
        })
    }

    fun data() {

//        findOrderListByUserIdParams!!["userId"] = DatabaseManager().getInstance()!!.getDao()!!.loadAll()[0].userId
//        findOrderListByUserIdParams!!["state"] = "4"
//        findOrderListByUserIdParams!!["type"] = ""
//        RestClient().builder()
//                .url("http://59.110.164.214:8024/global/order/findOrderListByUserId")
//                .params(findOrderListByUserIdParams!!)
//                .success(object : ISuccess {
//                    override fun onSuccess(response: String) {
//                        orderDetail = Gson().fromJson(response, OrderDetail::class.java)
//                        if (orderDetail.data.isEmpty()) {
//                            delivered_lv.visibility = View.INVISIBLE
//                            delivered_rl.visibility = View.VISIBLE
//                            EmallProgressBar.hideProgressbar()
//
//                        } else {
//                            data!!.add(orderDetail)
//                            initRefreshLayout()
//                            delivered_lv.adapter = DeliveredListAdapter(delegate, data, R.layout.item_order, activity)
//                            EmallProgressBar.hideProgressbar()
//
//                        }
//                    }
//                })
//                .build()
//                .get()

        retrofit = NetUtils.getRetrofit()
        apiService = retrofit!!.create(ApiService::class.java)
        val call = apiService!!.findOrderListByUserId(DatabaseManager().getInstance()!!.getDao()!!.loadAll()[0].userId, "4", "","android")
        call.enqueue(object : retrofit2.Callback<OrderDetail> {
            override fun onResponse(call: retrofit2.Call<OrderDetail>, response: retrofit2.Response<OrderDetail>) {
                if (response.body() != null) {
                    orderDetail = response.body()!!
                    val data: MutableList<OrderDetail>? = mutableListOf()

                    if (orderDetail.data.isEmpty()) {
                        delivered_lv.visibility = View.INVISIBLE
                        delivered_rl.visibility = View.VISIBLE
                        EmallProgressBar.hideProgressbar()

                    } else {
                        delivered_lv.visibility = View.VISIBLE
                        delivered_rl.visibility = View.INVISIBLE
                        data!!.add(orderDetail)
                        initRefreshLayout()
                        delivered_lv.adapter = DeliveredListAdapter(delegate, data, R.layout.item_order, activity)
                        EmallProgressBar.hideProgressbar()

                    }
                } else {
                    EmallLogger.d("error")
                }
            }

            override fun onFailure(call: retrofit2.Call<OrderDetail>, throwable: Throwable) {}
        })
    }

    fun initRefreshLayout() {
        delivered_srl.setColorSchemeColors(Color.parseColor("#b80017"))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onSupportVisible() {
        super.onSupportVisible()
//        all_lv.visibility = View.INVISIBLE
        data()
    }
}