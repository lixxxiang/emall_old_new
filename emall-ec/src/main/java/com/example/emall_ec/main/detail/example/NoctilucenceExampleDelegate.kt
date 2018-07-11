package com.example.emall_ec.main.detail.example

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.Toast
import com.example.emall_core.delegates.EmallDelegate
import com.example.emall_core.util.log.EmallLogger
import com.example.emall_ec.R
import com.example.emall_ec.database.DatabaseManager
import com.example.emall_ec.main.coupon.data.CalculatePriceByCouponIdBean
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.DefaultHandler
import com.google.gson.Gson
import kotlinx.android.synthetic.main.delegate_discover.*
import kotlinx.android.synthetic.main.delegate_fill_order_coupon.*
import kotlinx.android.synthetic.main.delegate_noctilucence_example.*
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

class NoctilucenceExampleDelegate : EmallDelegate() {

    var mUploadMessage: ValueCallback<Uri>? = null

    
    fun create(): NoctilucenceExampleDelegate?{
        return NoctilucenceExampleDelegate()
    }
    override fun setLayout(): Any? {
        return R.layout.delegate_noctilucence_example

    }

    override fun initial() {
        setSwipeBackEnable(false)

        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        n_example_toolbar.title = getString(R.string.noctilucence)
        (activity as AppCompatActivity).setSupportActionBar(n_example_toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        n_example_toolbar.setNavigationIcon(R.drawable.ic_back_small)

        n_example_toolbar.setNavigationOnClickListener {
            pop()
        }

        initWebView()
    }

    private fun initWebView(){
        n_example_webView.setDefaultHandler(DefaultHandler())
        n_example_webView.webChromeClient = object : WebChromeClient() {

            fun openFileChooser(uploadMsg: ValueCallback<Uri>, AcceptType: String, capture: String) {
                this.openFileChooser(uploadMsg)
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri>, AcceptType: String) {
                this.openFileChooser(uploadMsg)
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUploadMessage = uploadMsg
            }
        }
        //加载服务器网页
        n_example_webView.loadUrl("http://59.110.164.214:8082/nightDemo.html")
        //必须和js同名函数，注册具体执行函数，类似java实现类。
        n_example_webView.registerHandler("submitFromWeb", BridgeHandler { data, function ->
            // 例如你可以对原始数据进行处理
            Toast.makeText(activity, data, Toast.LENGTH_SHORT).show()
//            calculatePriceByCouponIdBean = Gson().fromJson(data, CalculatePriceByCouponIdBean::class.java)
//            EmallLogger.d(data)
//            EmallLogger.d(calculatePriceByCouponIdBean.data.productPrice[0].coupon_type)
//            var size = calculatePriceByCouponIdBean.data.productPrice.size
//
//            for (i in 0 until size){
//                coupons += calculatePriceByCouponIdBean.data.productPrice[i].coupon_type
//                coupons += ","
//            }
//
//
//            val bundle = Bundle()
//            bundle.putString("COUPON", coupons)
//            setFragmentResult(ISupportFragment.RESULT_OK, bundle)
//            pop()
        })
    }
    
    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }
}