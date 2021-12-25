package com.yuvrajdeshmukh.pokemongame

import android.location.Location

class PokemonCharacter {
    var title : String? = null
    var message: String? = null
    var icon : Int? = null
    var location : Location? = null
    var isKilled : Boolean? = false
    constructor(title:String,message:String,icon: Int,latitude:Double,longitude:Double)

    {
        location = Location("MyProvider")
        this.title = title
        this.message = message
        this.icon = icon
        this.location?.latitude = latitude
        this.location?.longitude = longitude



    }

}