package com.krenai.vendor.model

class ProductListings(
    var id: Number,
    var skuCode: Number,
    var sellingPrice: Number,
    var mrp: Number,
    var purchasePrice: Number,
    var inStockQuantity: Number,
    var thresholdQuantity: Number,
    var variantValues: Array<String>,
    var gst: Number,
    var barcode: String,
    var isTaxIncluded: Number,
    var minOrderQty: String,
    var maxOrderQty: String,
    var itemWeight: String,
    var packagingWeight: String,
    var medias: Media
)