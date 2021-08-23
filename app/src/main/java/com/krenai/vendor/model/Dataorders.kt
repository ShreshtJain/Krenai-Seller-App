package com.krenai.vendor.model

import org.json.JSONObject

data class Dataorders (
    var CustomerName: String,
    var Cost:String,
    var Contact:String,
    var Orderid: String,
    var description:String,
    var paymentmode:String,
    var createdDate:String,
    var updatedDate:String,
    var updatedBy:String,
    var position:Int,
    var jsonObject:JSONObject
//var Productname:String
//var Productcost:String
    )