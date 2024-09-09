package com.batuhanozdemir.wallpaperlib.view

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.batuhanozdemir.wallpaperlib.R
import com.batuhanozdemir.wallpaperlib.databinding.ActivityWallpaperBinding
import com.batuhanozdemir.wallpaperlib.model.Photo
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WallpaperActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWallpaperBinding
    private lateinit var wallpaperPortraitUrl: String
    private lateinit var wallpaperOriginalUrl: String
    private var wallpaper: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWallpaperBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            wallpaper = intent.getSerializableExtra("wp",Photo::class.java)
        }else{
            wallpaper = intent.getSerializableExtra("wp") as Photo
        }

        wallpaper?.let {
            wallpaperPortraitUrl = wallpaper!!.src.portrait
            wallpaperOriginalUrl = wallpaper!!.src.original
            Picasso.get().load(wallpaperPortraitUrl).into(binding.imageView)
        }
    }

    fun setWallpaper(view: View){
        val alert = AlertDialog.Builder(this@WallpaperActivity)
            .setTitle("Set Wallpaper")
            .setMessage("Are you sure?")
            .setPositiveButton("YES", object : OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    try {
                        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                        val bitmap = (binding.imageView.drawable as? BitmapDrawable)?.bitmap

                        if (bitmap != null){
                            wallpaperManager.setBitmap(bitmap)
                            Toast.makeText(this@WallpaperActivity,"Successful",Toast.LENGTH_SHORT).show()
                        }else{
                            println("Wallpaper Error")
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    finish()
                }
            })
            .setNegativeButton("NO", object : OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }
            })

        alert.show()
    }

    fun downloadWallpaper(view: View){
        AlertDialog.Builder(this@WallpaperActivity)
            .setMessage("What size do you want to download the image?")
            .setPositiveButton("Original",object : OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val fileName = "image_$timestamp"
                        downloadImage(applicationContext,wallpaperOriginalUrl,fileName)
                    }
                }
            })
            .setNegativeButton("Portrait",object : OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val fileName = "image_$timestamp"
                        downloadImage(applicationContext,wallpaperPortraitUrl,fileName)
                    }
                }
            })
            .setNeutralButton("Cancel",object : OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }
            })
            .show()
    }

    fun downloadImage(context: Context, imageURL: String, fileName: String){
        val resolver = context.contentResolver

        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,"$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(imageCollection,newImageDetails)

        imageUri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                try {
                    URL(imageURL).openStream().use { inputStream ->
                        inputStream.copyTo(outputStream!!)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val updatedImageDetails = ContentValues().apply {
                            put(MediaStore.Images.Media.IS_PENDING, 0)
                        }
                        resolver.update(it, updatedImageDetails, null, null)
                        toastMessage("Downloaded to Gallery")
                    } else {
                        toastMessage("Your android version is not  supported")
                    }
                }catch (e: Exception) {
                    // Hata yönetimi
                    Log.e("Error", "Image download failed: ${e.message}")
                    toastMessage("Image download failed")
                }
            }
        }
    }

    private fun toastMessage(message: String){
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@WallpaperActivity,message,Toast.LENGTH_LONG).show()
        }
    }
}