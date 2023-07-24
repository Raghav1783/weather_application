package com.example.weather_app

class WeatherModal {
    var time :String? =null
    var temperature : String? =null
    var icon : String? =null
    var windspeed : String? =null

    constructor(time: String?, temperature: String?, icon: String?, windspeed: String?) {
        this.time = time
        this.temperature = temperature
        this.icon = icon
        this.windspeed = windspeed
    }

}