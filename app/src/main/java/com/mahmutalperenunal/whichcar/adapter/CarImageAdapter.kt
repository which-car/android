package com.mahmutalperenunal.whichcar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mahmutalperenunal.whichcar.R

class CarImageAdapter(private val imageList: ArrayList<Int>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<CarImageAdapter.CarImageViewHolder>() {

    class CarImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.car_container_imageView);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.car_image_container, parent, false)
        return CarImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarImageViewHolder, position: Int) {
        holder.imageView.setImageResource(imageList[position])
        if (position == imageList.size-1){
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private val runnable = Runnable {
        imageList.addAll(imageList)
        notifyDataSetChanged()
    }
}