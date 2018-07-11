package com.example.emall_ec.main.search

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.ImageView
import android.widget.ZoomControls
import com.baidu.location.*
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.example.emall_ec.main.bottom.BottomItemDelegate
import com.example.emall_ec.R
import kotlinx.android.synthetic.main.delegate_search.*
import android.util.DisplayMetrics
import com.example.emall_core.util.log.EmallLogger
import com.example.emall_ec.main.index.dailypic.video.data.Gps
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator


/**
 * Created by lixiang on 2018/3/20.
 */
class SearchDelegate : BottomItemDelegate(), SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        val x = event!!.values[SensorManager.DATA_X].toDouble()
        if (Math.abs(x - lastX!!) > 1.0) {
            mCurrentDirection = x.toInt()
            locData = MyLocationData.Builder()
                    .accuracy(0F)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection.toFloat()).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build()
            mBaiduMap!!.setMyLocationData(locData)
        }
        lastX = x
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    var mMapView: MapView? = null
    private var lastX: Double? = 0.0
    private var mCurrentDirection = 0
    private var mCurrentLat = 0.0
    private var mCurrentLon = 0.0
    private var mCurrentAccracy: Float = 0.toFloat()
    var myListener = MyLocationListenner()
    private var mLocClient: LocationClient? = null
    private var mBaiduMap: BaiduMap? = null
    private var isFirstLoc = true
    private var locData: MyLocationData? = null
    private var mSensorManager: SensorManager? = null
    private var lati_lt: Double? = 0.0
    private var longi_lt: Double? = 0.0
    private var lati_rb = 0.0
    private var longi_rb = 0.0
    var mSharedPreferences: SharedPreferences? = null
    var pi = 3.141592653589793 * 3000.0 / 180.0
    var located = false
    fun create(): SearchDelegate? {
        return SearchDelegate()
    }

    override fun setLayout(): Any? {
        return R.layout.delegate_search
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun initial() {
        mSensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSharedPreferences = activity.getSharedPreferences("GEO_INFO", Context.MODE_PRIVATE)
        handlePermisson()
        initMap()

        val listener: BaiduMap.OnMapStatusChangeListener = object : BaiduMap.OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(p0: MapStatus?) {

            }

            override fun onMapStatusChangeStart(p0: MapStatus?, p1: Int) {
            }

            override fun onMapStatusChange(p0: MapStatus?) {

            }

            override fun onMapStatusChangeFinish(p0: MapStatus?) {
                val pt = Point()
                pt.x = 0
                pt.y = 0
                if (mBaiduMap != null) {
                    val ll = mBaiduMap?.projection?.fromScreenLocation(pt)
                    lati_lt = ll?.latitude
                    longi_lt = ll?.longitude

                    if (lati_lt != null && longi_lt != null) {
                        val dm = DisplayMetrics()
                        activity.windowManager.defaultDisplay.getMetrics(dm)
                        val pty = Point()
                        pty.x = dm.widthPixels
                        pty.y = dm.heightPixels
                        val lly = mBaiduMap!!.projection.fromScreenLocation(pty)
                        lati_rb = lly.latitude
                        longi_rb = lly.longitude
                    }
                    //右下角经纬度
                }
            }
        }

        mMapView!!.map.setOnMapStatusChangeListener(listener)
        search_searchbar_rl.setOnClickListener {
            startForResult(SearchPoiDelegate().create(), 100)
        }

        search_btn.setOnClickListener {
            if (!located) {
                val snackBar = Snackbar.make(view!!, "暂未获取到您的位置，请打开手机定位功能，使用无线网络或更好的网络环境继续操作", Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(getString(R.string.confirm_2), { snackBar.dismiss() })
                snackBar.show()
            } else {

                val geo = String.format("%s %s %s %s", longi_lt!!.toString(), lati_lt!!.toString(), longi_rb.toString(), lati_rb.toString())
                val editor = mSharedPreferences!!.edit()
                editor.putString("GEO", geo)
                editor.commit()
                val delegate = SearchResultDelegate().create()
                val bundle = Bundle()
                bundle.putString("GEO", geo)
                bundle.putInt("PRODUCT_TYPE", arguments.getInt("PRODUCT_TYPE"))
                delegate!!.arguments = bundle
                start(delegate)
            }

        }

        search_back_iv_rl.setOnClickListener {
            pop()
        }

        search_zoom_in_btn.setOnClickListener {
            val zoomIn: MapStatusUpdate? = MapStatusUpdateFactory.zoomIn()
            mBaiduMap!!.animateMapStatus(zoomIn)
        }

        search_zoom_out_btn.setOnClickListener {
            val zoomOut: MapStatusUpdate? = MapStatusUpdateFactory.zoomOut()
            mBaiduMap!!.animateMapStatus(zoomOut)
        }

        search_locate.setOnClickListener {
            if (located) {
                var mMapStatus = MapStatus.Builder()
                        .target(LatLng(mCurrentLat, mCurrentLon))
                        .zoom(14F)
                        .build()
                var mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus)
                mBaiduMap!!.animateMapStatus(mMapStatusUpdate)
            } else {
                val snackBar = Snackbar.make(view!!, "暂未获取到您的位置，请打开手机定位功能，使用无线网络或更好的网络环境继续操作", Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(getString(R.string.confirm_2), { snackBar.dismiss() })
                snackBar.show()
            }

        }
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (!data.isEmpty) {
            mBaiduMap!!.clear()
            var location = data.getString("LOCATION")
            if (!location.isEmpty()) {
                var latitude = location.split(",")[1]
                var longitude = location.split(",")[0]
                var gps = gcj02_To_Bd09(longitude.toDouble(), latitude.toDouble())
                var point = LatLng(gps.lat, gps.lon)
                var bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.location_mark)
                var option = MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                mBaiduMap!!.addOverlay(option)
                var mMapStatus = MapStatus.Builder()
                        .target(LatLng(latitude.toDouble(), longitude.toDouble()))
                        .zoom(14F)
                        .build()
                var mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus)
                mBaiduMap!!.animateMapStatus(mMapStatusUpdate);
            }

        }
    }

    fun gcj02_To_Bd09(gg_lon: Double, gg_lat: Double): Gps {
        val z = Math.sqrt(gg_lon * gg_lon + gg_lat * gg_lat) + 0.00002 * Math.sin(gg_lat * pi)
        val theta = Math.atan2(gg_lat, gg_lon) + 0.000003 * Math.cos(gg_lon * pi)
        val bd_lon = z * Math.cos(theta) + 0.0065
        val bd_lat = z * Math.sin(theta) + 0.006
        return Gps(bd_lon, bd_lat)
    }


    private fun initMap() {
        val mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL
        mMapView = activity.findViewById<MapView>(R.id.mMapView)

        mLocClient = LocationClient(activity)
        mLocClient!!.registerLocationListener(myListener)
        val option = LocationClientOption()
        option.isOpenGps = true
        option.setCoorType("bd09ll")
        option.setScanSpan(1000)
        option.setAddrType("all")
        option.setIsNeedLocationPoiList(true)
        mLocClient!!.locOption = option
        mLocClient!!.start()

        mBaiduMap = mMapView!!.map
        mBaiduMap!!.isMyLocationEnabled = true
        mBaiduMap!!.setMyLocationConfigeration(MyLocationConfiguration(mCurrentMode, true, null))
        val builder = MapStatus.Builder()
        builder.overlook(0f)
        mBaiduMap!!.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
        val child = mMapView!!.getChildAt(1)
        if (child != null && (child is ImageView || child is ZoomControls)) {
            child.visibility = View.INVISIBLE
        }
        mMapView!!.showScaleControl(false)
        mMapView!!.showZoomControls(false)
        val mUiSettings = mBaiduMap!!.uiSettings
        mUiSettings.isScrollGesturesEnabled = true
        mUiSettings.isOverlookingGesturesEnabled = true
        mUiSettings.isZoomGesturesEnabled = true
    }

    inner class MyLocationListenner : BDLocationListener {
        var lati: Double = 0.toDouble()
        var longi: Double = 0.toDouble()
        var address: String = ""
        internal lateinit var poi: List<Poi>

        override fun onReceiveLocation(location: BDLocation?) {
            if (location == null || mMapView == null) {
                return
            }

            val locData = MyLocationData.Builder()
                    .accuracy(0F)
                    .direction(mCurrentDirection.toFloat())
                    .latitude(location.latitude)
                    .longitude(location.longitude).build()
            lati = location.latitude
            longi = location.longitude
            mCurrentLat = location.latitude
            mCurrentLon = location.longitude
            address = location.addrStr
            mCurrentAccracy = location.radius
            poi = location.poiList
            mBaiduMap!!.setMyLocationData(locData)
            if (isFirstLoc) {
                isFirstLoc = false
                val ll = LatLng(location.latitude,
                        location.longitude)
                located = true
                val builder = MapStatus.Builder()
                builder.target(ll).zoom(8.0f)
                mBaiduMap!!.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
            }
        }

        fun onConnectHotSpotMessage(s: String, i: Int) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun handlePermisson() {
        val permission = Manifest.permission.ACCESS_COARSE_LOCATION
        val checkSelfPermission = ActivityCompat.checkSelfPermission(activity, permission)
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            } else {
                myRequestPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun myRequestPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissions(permissions, 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onResume() {
        mMapView!!.onResume()
        super.onResume()
        mSensorManager!!.registerListener(this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

}