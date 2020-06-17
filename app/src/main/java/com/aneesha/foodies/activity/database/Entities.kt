package com.aneesha.foodies.activity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Restaurants")

data class RestaurantEntity(
    @ColumnInfo(name="restaurant_id")@PrimaryKey var restaurantId:String,
    @ColumnInfo(name="restaurant_name") var restaurantName:String,
    @ColumnInfo(name="cost_for_one") var cost_for_one:String,
    @ColumnInfo(name="rating") var restaurantRating:String,
    @ColumnInfo(name="image_url") var restaurantImage:String
)