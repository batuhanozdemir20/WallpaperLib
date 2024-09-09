package com.batuhanozdemir.wallpaperlib.view

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.batuhanozdemir.wallpaperlib.api.BASE_URL
import com.batuhanozdemir.wallpaperlib.api.PexelsApi
import com.batuhanozdemir.wallpaperlib.adapter.PhotoAdapter
import com.batuhanozdemir.wallpaperlib.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var photoAdapter: PhotoAdapter
    private var query = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //https://api.pexels.com/v1/

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val pexelsApi = retrofit.create(PexelsApi::class.java)


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    if (it.isNotEmpty()){
                        query = it
                    }
                    getData(pexelsApi)
                    binding.textView.visibility = View.GONE
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let {
                    if (it.isNotEmpty()){
                        query = it
                    }
                    getData(pexelsApi)
                    binding.textView.visibility = View.GONE
                }
                return true
            }
        })
    }

    private fun getData(pexelsApi: PexelsApi){
        CoroutineScope(Dispatchers.IO).launch {
            val searchResult = pexelsApi.searchPhotos(query, perPage = 10)
            val photoList = searchResult.photos

            launch(Dispatchers.Main) {
                photoAdapter = PhotoAdapter(photoList)
                binding.recyclerView.layoutManager = GridLayoutManager(this@MainActivity,2)
                binding.recyclerView.adapter = photoAdapter
            }
        }
    }
}