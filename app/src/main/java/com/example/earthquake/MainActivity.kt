package com.example. earthquake

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.earthquake.databinding.ActivityMainBinding
import org.json.JSONException

class MainActivity : AppCompatActivity(), RVAdapterListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: EarthquakeAdapter
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val isChecked = sharedPreferences.getString("sort", "time")
        if(isChecked == "time"){
            binding.time.isChecked = true
        }else{
            binding.magnitude.isChecked = true
        }
        val mag = sharedPreferences.getString("magnitude","3")
        binding.minimunET.setText(mag)
        binding.earthquakeRV.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.resultTV.visibility = View.GONE
        fetchData(sharedPreferences.getString("sort","time")!!,sharedPreferences.getString("magnitude","3")!!)
        mAdapter = EarthquakeAdapter(this, this)
        binding.earthquakeRV.adapter = mAdapter
        binding.earthquakeRV.layoutManager = LinearLayoutManager(this)
        binding.search.setOnClickListener {
            binding.earthquakeRV.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            binding.resultTV.visibility = View.GONE
            val magnitude = binding.minimunET.text
            var sort = ""
            if(binding.time.isChecked){
                sort = "time"
            }else{
                sort = "magnitude"
            }
            if(sort=="magnitude" && magnitude.toString()<"4"){
                Toast.makeText(this,"Enter a magnitude greater than 4 for sorting by magnitude",Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }else {
                sharedPreferences.edit().putString("sort", sort).apply()
                sharedPreferences.edit().putString("magnitude", magnitude.toString()).apply()
                fetchData(sort, magnitude.toString())
            }
        }
    }

    override fun onClick(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun fetchData(sort: String, minmag: String) {
        val url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=$sort&limit=100&minmag=$minmag"
        val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener {
                    val earthquakes: ArrayList<Earthquake> = ArrayList()

                    try {
                        val rootJson = it
                        val count = rootJson.getJSONObject("metadata").getInt("count")
                        Log.d("MainActivity", count.toString())
                        binding.resultTV.text = "$count, Results Found!"
                        val earthquakeArray = rootJson.getJSONArray("features")
                        for (i in 0 until earthquakeArray.length()) {
                            val currentEarthquake = earthquakeArray.getJSONObject(i)
                            val properties = currentEarthquake.getJSONObject("properties")
                            val magnitude = properties.getDouble("mag")
                            val place = properties.getString("place")
                            val time = properties.getLong("time")
                            val url = properties.getString("url")
                            val earthquake = Earthquake(magnitude, place, time, url)
                            earthquakes.add(earthquake)
                        }
                    } catch (e: JSONException) {
                        Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e)
                    }
                    binding.resultTV.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.earthquakeRV.visibility = View.VISIBLE
                    mAdapter.updateData(earthquakes)
                },
                Response.ErrorListener {
                    Toast.makeText(this, "An Error occurred", Toast.LENGTH_SHORT).show()
                }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        MySingleton.getInstance(this).addToQueue(jsonObjectRequest)
    }
}