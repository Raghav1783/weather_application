package com.example.weather_app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.IOException
import java.security.AccessController.getContext
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(){
    private lateinit var homeRL : RelativeLayout
    private lateinit var loadingPB : ProgressBar
    private  lateinit var cityNameTV :TextView
    private lateinit var temperatureTV: TextView
    private lateinit var conditionTV: TextView
    private  lateinit var weatherRV : RecyclerView
    private lateinit var  cityEdt : TextInputEditText
    private lateinit var backIV : ImageView
    private lateinit var iconIV : ImageView
    private lateinit var searchIV: ImageView
    private lateinit var WeatherList:ArrayList<WeatherModal>
    private lateinit var adapter: WeatherRVAdapter
    private lateinit var locationManager:LocationManager
    private lateinit var CityName:String
    private lateinit var wmodal:WeatherModal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_main)

        homeRL = findViewById(R.id.idRlhome)
        loadingPB = findViewById(R.id.idPbloading)
        cityEdt = findViewById(R.id.idEDTcity)
        cityNameTV = findViewById(R.id.idTvCityName)
        temperatureTV = findViewById(R.id.idTVTemperature)
        conditionTV = findViewById(R.id.idTvCondition)
        weatherRV = findViewById(R.id.idRvWeather)
        backIV = findViewById(R.id.idBgimg)
        iconIV = findViewById(R.id.idIVIcon)
        searchIV = findViewById(R.id.idIvsearch)

        WeatherList= ArrayList()
//        val permission = Manifest.permission.ACCESS_FINE_LOCATION
//        val requestCode = 1 // Can be any value
//        requestPermissions(arrayOf(permission), requestCode)
        if(ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_DENIED ){
            ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),1)

        }

        locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager


        val lastKnownLocation = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER)

        CityName = lastKnownLocation?.let { getcityName(it.longitude, it.latitude) } ?: "NotFound"


        retrieveWeatherData(CityName)

        searchIV.setOnClickListener{
                var city=cityEdt.text.toString()
                if(city.isEmpty()){
                    Toast.makeText(this,"Enter the city name",Toast.LENGTH_SHORT).show()
                }
                else{
                    cityNameTV.text=city
                    retrieveWeatherData(city)
                }
        }



        adapter= WeatherRVAdapter(this,WeatherList)
        weatherRV.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        weatherRV.adapter=adapter

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(this,"Granted",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Give Permission",Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }
    private fun getcityName(longitude: Double,latitude: Double): String {
        var cityName="NotFound"
        val geocoder = Geocoder(baseContext, Locale.getDefault())
        var address: Address? = null
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 10) as? List<Address>
                ?: emptyList()
            for (adr in addresses) {
                val city = adr.locality
                if (city != null && city.isNotEmpty()) {
                    cityName = city
                    break
                } else {
                    Log.d("TAG", "CITY NOT FOUND")
                    Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName

    }

    private fun retrieveWeatherData(city: String) {

        val apiKey = "47675ace754d4bc391b152453233105"
        val location =city
        cityNameTV.text = city
        val call=RetrofitClient.create().getWeatherForecast(apiKey, location)
        call.enqueue(object :Callback<WeatherResponse>{
               override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                   if (response.isSuccessful) {
                       val weatherResponse = response.body()
                       if (weatherResponse != null) {
                           // Logging the response
                           Log.d("API Response", weatherResponse.toString())

                           val currentTemperature = weatherResponse.current?.temp_c ?: 0.0
                           temperatureTV.text = "$currentTemperature°C"

                           val isDay = weatherResponse.current?.is_day
                           val condition = weatherResponse.current?.condition?.text
                           val conditionIcon = weatherResponse.current?.condition?.icon
                           Picasso.get().load("http:$conditionIcon").into(iconIV)
                           conditionTV.text = condition

                           if (isDay == 1) {
                               Picasso.get().load("https://i.pinimg.com/originals/6f/7b/88/6f7b88465c6dc793ccc993c6ff8544ea.jpg").into(backIV)
                           } else {
                               Picasso.get().load("https://i.pinimg.com/originals/61/08/4b/61084bc14386f01db3bcb79817365626.jpg").into(backIV)
                           }

                           val hours = weatherResponse.forecast?.forecastday?.get(0)?.hour
                           hours?.let {
                               for (hour in it) {
                                   val time = hour.time
                                   val temperature = hour.tempC.toString()

                                   val img = hour.condition?.icon
                                   Log.d("icon", "$img")

                                   val wind = hour.condition?.text
                                   val weatherModal = WeatherModal(time,temperature,img,wind)
                                   WeatherList.add(weatherModal)
                               }
                               adapter.notifyDataSetChanged()
                           }
                       } else {
                           // Handle null response body
                           Toast.makeText(this@MainActivity, "Empty response body", Toast.LENGTH_SHORT).show()
                       }
                   } else {
                       val errorBody = response.errorBody()?.string()
                       Log.d("API Response", "API request failed: $errorBody")
                       Log.e("API Error", "Request failed with code: ${response.code()}")
                       Log.e("API Error", "Error body: ${response.errorBody()?.string()}")
                       Toast.makeText(this@MainActivity, "API request failed ${errorBody}", Toast.LENGTH_SHORT).show()
                   }
               }


               override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                   Log.e("API Request", "Failed", t)
                   Toast.makeText(this@MainActivity, "API request failed: ${t.message}", Toast.LENGTH_LONG).show()
           }

       })

    }
}