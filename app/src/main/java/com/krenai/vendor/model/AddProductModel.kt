package com.krenai.vendor.model

class AddProductModel(
    var id:Int,
    var name: String,
    var description: String,
    var category: String,
    var subData:ArrayList<Dataproductsimage>,
    var url:String,
    var i:Int,
    var checked:Boolean
//    var subCategory: SubCategory,
//    var hsnCode: String,
//    var brand: String,
//    var iconMedia: String,
//    var media: Media,
//    var variantValues: VariantValues,
//    var productListings: ProductListings,
)