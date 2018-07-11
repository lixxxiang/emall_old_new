package com.example.emall_ec.main.search

import android.view.View
import android.view.WindowManager
import com.example.emall_core.net.RestClient
import com.example.emall_core.net.callback.IError
import com.example.emall_core.net.callback.IFailure
import com.example.emall_core.net.callback.ISuccess
import com.example.emall_core.util.log.EmallLogger
import com.example.emall_ec.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.delegate_search_poi.*
import com.example.emall_ec.main.search.adapter.SearchPoiCitiesAdapter
import com.example.emall_ec.main.search.adapter.SearchPoiPoisAdapter
import com.example.emall_ec.main.search.data.CitiesBean
import com.example.emall_ec.main.search.data.PoiBean
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.blankj.utilcode.util.KeyboardUtils
import com.example.emall_core.delegates.EmallDelegate
import com.example.emall_ec.R.id.*
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator


/**
 * Created by lixiang on 2018/3/20.
 */
class SearchPoiDelegate : EmallDelegate() {
    var citiesList: MutableList<String>? = mutableListOf()
    var countList: MutableList<String>? = mutableListOf()
    var poiList: MutableList<String>? = mutableListOf()
    var poiAddressList: MutableList<String>? = mutableListOf()

    var searchPoiCitiesAdapter: SearchPoiCitiesAdapter? = null
    var searchPoiPoisAdapter: SearchPoiPoisAdapter? = null
    var pages = 0
    val bundle = Bundle()

    var poiInfo = PoiBean()

    fun create(): SearchPoiDelegate? {
        return SearchPoiDelegate()
    }

    override fun setLayout(): Any? {
        return R.layout.delegate_search_poi
    }

    override fun initial() {
        search_poi_et.isFocusable = true
        search_poi_et.isFocusableInTouchMode = true
        search_poi_et.requestFocus()
        setSwipeBackEnable(false)
//        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        search_back_iv_rl.setOnClickListener {
            EmallLogger.d(pages)
            if (pages == 2) {
                search_poi_poi_listview.visibility = View.INVISIBLE
                search_poi_cities_listview.visibility = View.VISIBLE
                pages = 1
            } else {

                bundle.putString("LOCATION", "")
                this.setFragmentResult(ISupportFragment.RESULT_OK, bundle)
                KeyboardUtils.hideSoftInput(activity)
                pop()
            }

        }
        addHeadView()

        search_poi_cities_listview.setOnItemClickListener { adapterView, view, index, l ->
            if (index != 0){
                RestClient().builder()
                        .url(String.format("http://59.110.161.48:8023/GetPoiByGaode.do?poiName=%s&cityName=%s&client=android", search_poi_et.text, citiesList!![index - 1]))
                        .success(object : ISuccess {
                            override fun onSuccess(response: String) {
                                clearPois()
                                pages = 2
                                showPois(response)
                            }
                        })
                        .failure(object : IFailure {
                            override fun onFailure() {

                            }
                        })
                        .error(object : IError {
                            override fun onError(code: Int, msg: String) {

                            }
                        })
                        .build()
                        .post()
            }

        }

        search_poi_poi_listview.setOnItemClickListener { adapterView, view, i, l ->
            EmallLogger.d(poiInfo.gdPois.poiList[i].location)
            bundle.putString("LOCATION", poiInfo.gdPois.poiList[i].location)
            this.setFragmentResult(ISupportFragment.RESULT_OK, bundle)
            pop()

        }

        search_poi_et.setOnEditorActionListener { v, actionId, event ->
            KeyboardUtils.hideSoftInput(activity)
            search()
            false
        }

        index_noti_tv_rl.setOnClickListener {
            KeyboardUtils.hideSoftInput(activity)
            search()
        }
    }

    private fun search() {
        if(search_poi_et.text.toString() != ""){
            search_progressBar.visibility = View.VISIBLE

            RestClient().builder()
                    .url(String.format("http://59.110.161.48:8023/GetPoiByGaode.do?poiName=%s&client=android", search_poi_et.text))
                    .success(object : ISuccess {
                        override fun onSuccess(response: String) {
//                            hideText()
                            search_progressBar.visibility = View.INVISIBLE
                            EmallLogger.d(response)
                            val tempCities = Gson().fromJson(response, CitiesBean::class.java)
                            val tempPois = Gson().fromJson(response, PoiBean::class.java)

//                        if(tempCities.sug != null){
//                            if(tempCities.sug.isEmpty()){
//                                hideText()
//                                clearCities()
//                                clearPois()
//                                showNoResult()
//                            }else {
                            if (tempPois.type == "0" || tempCities.type == "0") {//is cities
                                if (tempCities.sug.isEmpty()) {
                                    hideText()
                                    clearCities()
                                    clearPois()
                                    showNoResult()
                                } else {
                                    hideNoResult()
                                    pages = 1
                                    clearCities()
                                    showCities(response)
                                }

                            } else if (tempPois.type == "1" || tempCities.type == "1") {//is pois
                                hideNoResult()
                                clearPois()
                                pages = 3
                                showPois(response)
                            } else {//no return
                                hideText()
                                clearCities()
                                clearPois()
                                showNoResult()
                            }
//                            }
//                        }

                        }
                    })
                    .failure(object : IFailure {
                        override fun onFailure() {

                        }
                    })
                    .error(object : IError {
                        override fun onError(code: Int, msg: String) {

                        }
                    })
                    .build()
                    .post()
        }else{
            KeyboardUtils.hideSoftInput(activity)
            val snackBar = Snackbar.make(view!!, "输入信息为空", Snackbar.LENGTH_SHORT)
            snackBar.show()
        }

    }

    private fun addHeadView() {
        val head = View.inflate(activity, R.layout.item_head_search_poi_cities, null)
        search_poi_cities_listview.addHeaderView(head)
    }

    fun clearPois() {
        if (poiList!!.size != 0 || poiAddressList!!.size != 0) {
            poiList!!.clear()
            poiAddressList!!.clear()
        }
    }

    fun clearCities() {
        if (citiesList!!.size != 0 || countList!!.size != 0) {
            citiesList!!.clear()
            countList!!.clear()
        }
    }

    fun hideText() {
        search_poi_text1_tv.visibility = View.INVISIBLE
        search_poi_text2_tv.visibility = View.INVISIBLE
    }

    fun showNoResult() {
        if (search_poi_cities_listview.visibility == View.VISIBLE || search_poi_poi_listview.visibility == View.VISIBLE) {
            search_poi_poi_listview.visibility = View.INVISIBLE
            search_poi_cities_listview.visibility = View.INVISIBLE
        }
        search_poi_no_result_rl.visibility = View.VISIBLE
    }

    fun hideNoResult(){
//        if (search_poi_cities_listview.visibility == View.INVISIBLE || search_poi_poi_listview.visibility == View.INVISIBLE) {
//            search_poi_poi_listview.visibility = View.VISIBLE
//            search_poi_cities_listview.visibility = View.VISIBLE
//        }
        search_poi_no_result_rl.visibility = View.INVISIBLE
    }


    fun showCities(response: String) {
        if (search_poi_cities_listview.visibility == View.INVISIBLE) {
            search_poi_poi_listview.visibility = View.INVISIBLE
            search_poi_cities_listview.visibility = View.VISIBLE
        }
        val citiesInfo = Gson().fromJson(response, CitiesBean::class.java)
        for (i in 0 until citiesInfo.sug.size) {
            for (j in 0 until citiesInfo.sug[i].cites.size) {
                citiesList!!.add(citiesInfo.sug[i].cites[j].name)
                countList!!.add(citiesInfo.sug[i].cites[j].num)
            }
        }

        searchPoiCitiesAdapter = SearchPoiCitiesAdapter(citiesList, countList, context)
        search_poi_cities_listview.adapter = searchPoiCitiesAdapter
    }

    fun showPois(response: String) {

        if (search_poi_poi_listview.visibility == View.INVISIBLE) {
            search_poi_cities_listview.visibility = View.INVISIBLE
            search_poi_poi_listview.visibility = View.VISIBLE
        }

        poiInfo = Gson().fromJson(response, PoiBean::class.java)

        if(poiInfo.gdPois == null){
            hideText()
            clearCities()
            clearPois()
            showNoResult()
        }else{
            for (i in 0 until poiInfo.gdPois.poiList.size) {
                poiList!!.add(poiInfo.gdPois.poiList[i].name)

                if (poiInfo.gdPois.poiList[i].cityname == null) {
                    poiInfo.gdPois.poiList[i].cityname = poiInfo.gdPois.poiList[i].name
                }
                if (poiInfo.gdPois.poiList[i].adname == null) {
                    poiInfo.gdPois.poiList[i].adname = ""
                }
                if (poiInfo.gdPois.poiList[i].address == null) {
                    poiInfo.gdPois.poiList[i].address = ""
                }

                poiAddressList!!.add(String.format("%s %s %s",
                        poiInfo.gdPois.poiList[i].cityname,
                        poiInfo.gdPois.poiList[i].adname,
                        poiInfo.gdPois.poiList[i].address))
            }

            EmallLogger.d(poiList!!.size)

            searchPoiPoisAdapter = SearchPoiPoisAdapter(poiList, poiAddressList, context)
            search_poi_poi_listview.adapter = searchPoiPoisAdapter
        }

    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
    }

    override fun onBackPressedSupport(): Boolean {
        super.onBackPressedSupport()
        EmallLogger.d(pages)
        if (pages == 2) {
            search_poi_poi_listview.visibility = View.INVISIBLE
            search_poi_cities_listview.visibility = View.VISIBLE
            pages = 1
            return true
        } else {
            bundle.putString("LOCATION", "")
            this.setFragmentResult(ISupportFragment.RESULT_OK, bundle)
            KeyboardUtils.hideSoftInput(activity)
//            pop()
            return false
        }
    }
}