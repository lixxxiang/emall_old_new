package com.example.emall_ec.main.index

import com.example.emall_ec.main.index.move.recycler.MultipleItemEntity
import com.alibaba.fastjson.JSON
import com.example.emall_core.util.log.EmallLogger
import com.example.emall_ec.main.index.move.recycler.DataConverter
import com.example.emall_ec.main.index.move.recycler.MultipleFields
import com.example.emall_ec.main.index.move.recycler.data.GuessLikeBean
import com.example.emall_ec.main.index.move.recycler.data.TheThreeBean
import com.example.emall_ec.main.index.move.recycler.data.UnitBean


/**
 * Created by lixiang on 17/02/2018.
 */
class IndexDataConverter : DataConverter() {


    var unit: MutableList<UnitBean>? = mutableListOf()
    var theThree: MutableList<TheThreeBean>? = mutableListOf()
    var guessLike: MutableList<GuessLikeBean>? = mutableListOf()
    var dailyPic: MutableList<String>? = mutableListOf()


    override fun guessLikeConvert(): ArrayList<MultipleItemEntity> {
        var jsonObject = JSON.parseObject(getJsonData()).getJSONArray("data").getJSONObject(2).getJSONArray("pieces")
        var size = jsonObject.size
        var dataType: String?
        var imageUrl: String?
        var posTitle: String?
        var posDescription: String?
        var price: String?
        var productId: String?

        for (i in 0 until size) {
            dataType = jsonObject.getJSONObject(i).getString("dataType")
            imageUrl = jsonObject.getJSONObject(i).getString("imageUrl")
            posTitle = jsonObject.getJSONObject(i).getString("posTitle")
            posDescription = jsonObject.getJSONObject(i).getString("posDescription")
            price = jsonObject.getJSONObject(i).getString("price")
            productId = jsonObject.getJSONObject(i).getString("productId")

            guessLike!!.add(GuessLikeBean(dataType, imageUrl, posTitle, posDescription, price, productId))
        }
        val entity = MultipleItemEntity.builder()
                .setField(MultipleFields.GUESS_LIKE, guessLike!!)
                .setField(MultipleFields.SPAN_SIZE, 2)
                .setField(MultipleFields.ITEM_TYPE, 4)
                .build()
        ENTITIES.add(entity)
        return ENTITIES
    }

    override fun theThreeConvert(): ArrayList<MultipleItemEntity> {
        var jsonObject = JSON.parseObject(getJsonData()).getJSONArray("data").getJSONObject(1).getJSONArray("pieces")
        var size = jsonObject.size
        var imageUrl: String?
        var link: String?
        var type: String?
        var productId: String?
        for (i in 0 until size) {
            imageUrl = jsonObject.getJSONObject(i).getString("imageUrl")
            link = jsonObject.getJSONObject(i).getString("link")
            type = jsonObject.getJSONObject(i).getString("dataType")
            productId = jsonObject.getJSONObject(i).getString("productId")
            theThree!!.add(TheThreeBean(imageUrl, type, link, productId))
        }
        val entity = MultipleItemEntity.builder()
                .setField(MultipleFields.THE_THREE, theThree!!)
                .setField(MultipleFields.SPAN_SIZE, 2)
                .setField(MultipleFields.ITEM_TYPE, 3)
                .build()
        ENTITIES.add(entity)
        return ENTITIES
    }


    override fun horizontalScrollConvert(): ArrayList<MultipleItemEntity> {
        var jsonObject = JSON.parseObject(getJsonData()).getJSONArray("data").getJSONObject(0).getJSONArray("pieces")
        var size = jsonObject.size
        var title: String?
        var detail: String?
        var imageUrl: String?
        var link: String?
        var type: String?
        var productId: String?

        for (i in 0 until size) {
            title = jsonObject.getJSONObject(i).getString("posTitle")
            detail = jsonObject.getJSONObject(i).getString("posDescription")
            imageUrl = jsonObject.getJSONObject(i).getString("imageUrl")
            link = jsonObject.getJSONObject(i).getString("link")
            type = jsonObject.getJSONObject(i).getString("dataType")
            productId = jsonObject.getJSONObject(i).getString("productId")

            unit!!.add(UnitBean(title, detail, imageUrl, type, link, productId))
        }
        val entity = MultipleItemEntity.builder()
                .setField(MultipleFields.HORIZONTAL_SCROLL, unit!!)
                .setField(MultipleFields.SPAN_SIZE, 2)
                .setField(MultipleFields.ITEM_TYPE, 2)
                .build()

        ENTITIES.add(entity)
        return ENTITIES
    }

    override fun everyDayPicConvert(): ArrayList<MultipleItemEntity> {
        val jsonObject = JSON.parseObject(getJsonData()).getJSONObject("data").getJSONArray("MixedContentList")
        for (i in 0..2) {
            dailyPic!!.add(jsonObject.getJSONObject(i).getString("contentName"))
        }
        val entity = MultipleItemEntity.builder()
                .setField(MultipleFields.EVERY_DAY_PIC_TITLE, dailyPic!!)
                .setField(MultipleFields.SPAN_SIZE, 2)
                .setField(MultipleFields.ITEM_TYPE, 1)
                .build()
//        val entity = MultipleItemEntity.builder()
//                .setField(MultipleFields.EVERY_DAY_PIC_TITLE, "title")
//                .setField(MultipleFields.SPAN_SIZE, 2)
//                .setField(MultipleFields.ITEM_TYPE, 1)
//                .build()
        ENTITIES.add(entity)
        return ENTITIES
    }

    override fun bannerConvert(): ArrayList<MultipleItemEntity> {
        val dataArray = JSON.parseObject(getJsonData()).getJSONArray("data")
        val size = dataArray.size
        var entity = MultipleItemEntity.builder().build()
        val bannerImages: MutableList<String>? = mutableListOf()
        val bannerDataTypes: MutableList<String>? = mutableListOf()
        val bannerProductIds: MutableList<String>? = mutableListOf()
        val bannerLinks: MutableList<String>? = mutableListOf()

        for (i in 0 until size) {
            val data = dataArray.getJSONObject(i)
            val bannerDataType = data.getString("dataType")
            val bannerImageUrl = data.getString("imageUrl")
            val bannerLink = data.getString("link")
            var bannerProductId = String()
            bannerProductId = if (data.getString("productId") != null) {
                data.getString("productId")
            } else
                "NULL"

            bannerLinks!!.add(bannerLink)
            bannerImages!!.add(bannerImageUrl)
            bannerDataTypes!!.add(bannerDataType)
            bannerProductIds!!.add(bannerProductId)

            entity = MultipleItemEntity.builder()
                    .setField(MultipleFields.BANNERS_COUNT, size)
                    .setField(MultipleFields.BANNERS_DATA_TYPE, bannerDataTypes)
                    .setField(MultipleFields.BANNERS_IMAGEURL, bannerImages)
                    .setField(MultipleFields.BANNERS_LINK, bannerLinks)
                    .setField(MultipleFields.BANNERS_PRODUCT_ID, bannerProductIds)
                    .setField(MultipleFields.SPAN_SIZE, 2)
                    .setField(MultipleFields.ITEM_TYPE, 0)
                    .build()


        }
        ENTITIES.add(entity)

        return ENTITIES
    }


    override fun convert(): ArrayList<MultipleItemEntity> {
        val dataArray = JSON.parseObject(getJsonData()).getJSONArray("MixedContentList")
        val size = dataArray.size
        for (i in 0 until size) {
            val data = dataArray.getJSONObject(i)
            val imageUrl1 = data.getString("thumbnail1Path")
            val contentDate = data.getString("contentDate")
            val entity = MultipleItemEntity.builder()
//                    .setField(MultipleFields.HORIZONTAL_SCROLL_TITLE, imageUrl1)
                    .setField(MultipleFields.CONTENT_DATE, contentDate)
                    .setField(MultipleFields.SPAN_SIZE, 1)
                    .setField(MultipleFields.ITEM_TYPE, 2)
                    .build()

            ENTITIES.add(entity)
        }
        return ENTITIES
    }
}