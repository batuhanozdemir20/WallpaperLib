package com.batuhanozdemir.wallpaperlib.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.batuhanozdemir.wallpaperlib.databinding.RecyclerCardBinding
import com.batuhanozdemir.wallpaperlib.model.Photo
import com.batuhanozdemir.wallpaperlib.view.WallpaperActivity
import com.squareup.picasso.Picasso

class PhotoAdapter(val photoList: List<Photo>): Adapter<PhotoAdapter.PhotoHolder>() {

    class PhotoHolder(val binding: RecyclerCardBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val binding = RecyclerCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PhotoHolder(binding)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val photoUrl = photoList[position].src.portrait
        Picasso.get().load(photoUrl).into(holder.binding.recyclerImageView)
        holder.binding.recyclerImageView.setOnClickListener {
            val showWp = Intent(holder.itemView.context, WallpaperActivity::class.java)
            showWp.putExtra("wp",photoList[position])
            it.context.startActivity(showWp)
        }
    }

}