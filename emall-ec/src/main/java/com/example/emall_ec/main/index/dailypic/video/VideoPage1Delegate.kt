package com.example.emall_ec.main.index.dailypic.video

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.example.emall_ec.main.bottom.BottomItemDelegate
import com.example.emall_core.util.log.EmallLogger
import com.example.emall_ec.R
import kotlinx.android.synthetic.main.delegate_video_page_1.*
import java.util.*

class VideoPage1Delegate : BottomItemDelegate() {
    var timer = Timer()

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {

            // TODO Auto-generated method stub
            if (msg.what == 0) {
                //这里可以进行UI操作，如Toast，Dialog等
                val sp = activity.getSharedPreferences("VIDEO_DETAIL", Context.MODE_PRIVATE)
                if(sp.getString("videoName", "") != "Empty/NULL log message"){
                    EmallLogger.d(sp.getString("videoName", ""))
                    video_brief.text = sp.getString("videoName", "")
                    video_date.text = String.format("脉动地球 · %s", sp.getString("videoDate", ""))
                    timer.cancel()
                    timer.purge()
                }
            }
        }
    }
    fun create(): VideoPage1Delegate? {
        return VideoPage1Delegate()
    }

    override fun setLayout(): Any? {
        return R.layout.delegate_video_page_1
    }

    @SuppressLint("ApplySharedPref")
    override fun initial() {
        setSwipeBackEnable(false)
//        val sp = activity.getSharedPreferences("VIDEO_DETAIL", Context.MODE_PRIVATE) //取得user_id和手机号
//        EmallLogger.d(sp.getString("videoName",""))
//        video_brief.text = sp.getString("videoName", "")
//        video_date.text = String.format("脉动地球 · %s", sp.getString("videoDate", ""))

        timer.schedule(object : TimerTask() {
            override fun run() {
                var msg = Message()
                msg.what = 0
                mHandler.sendMessage(msg)
            }

        },500,500)
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        EmallLogger.d("stop")
    }
}