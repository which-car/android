package com.mahmutalperenunal.whichcar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.model.CarDetail

class CarAdapter(val context: Context): RecyclerView.Adapter<CarAdapter.MyViewHolder>() {

    private var carList = emptyList<CarDetail>()


    //item click process
    private lateinit var carListener: OnItemClickListener

    interface OnItemClickListener { fun onItemClick(position: Int) }

    fun setOnItemClickListener(listener: OnItemClickListener) { carListener = listener }


    inner class MyViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        val brand: TextView = itemView.findViewById(R.id.models_items_list_brand_textView)
        val model: TextView = itemView.findViewById(R.id.models_items_list_model_textView)
        val photo: ImageView = itemView.findViewById(R.id.models_items_list_modelPhoto_imageView)
        val cardView: MaterialCardView = itemView.findViewById(R.id.models_items_list)

        //on click
        init { itemView.setOnClickListener { listener.onItemClick(adapterPosition) } }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_item_list, parent, false), carListener)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //set animation
        holder.cardView.animation = AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation)

        holder.brand.text = carList[position].brand
        holder.model.text = carList[position].model

        Glide.with(context)
            .load(carList[position].carPhoto)
            .centerCrop()
            .into(holder.photo)
    }


    //set data
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<CarDetail>){
        carList = newList
        notifyDataSetChanged()
    }

}