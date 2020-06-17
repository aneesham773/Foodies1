package com.aneesha.foodies.activity.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)
    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)
    @Query("SELECT * FROM Restaurants")
    fun getAllRestaurants():List<RestaurantEntity>
    @Query("SELECT * FROM Restaurants WHERE restaurant_id= :restaurantId")
    fun getRestaurantById(restaurantId:String):RestaurantEntity
}