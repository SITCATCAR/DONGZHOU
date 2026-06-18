package com.swx.dongzhou.pages.createPage

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.swx.dongzhou.Activities.CreateActivities.AppCreateActivity
import com.swx.dongzhou.Activities.CreateActivities.CreatePageMode
import com.swx.dongzhou.Activities.CreateActivities.CreatePageConfigs
import com.swx.dongzhou.Activities.CreateActivities.FormCreateActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.Utils

class CreateAdapter(val context: FragmentActivity?, val itemList: List<CreateItem>) : RecyclerView.Adapter<CreateAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.create_rcv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.itemName.text=itemList[position].name
        holder.itemImage.setImageResource(Utils.getItemImage(itemList[position].type))
        holder.itemView.setOnClickListener {
            val config = CreatePageConfigs.getConfig(itemList[position].type)
            val targetActivity = if (config.mode == CreatePageMode.APP) {
                AppCreateActivity::class.java
            } else {
                FormCreateActivity::class.java
            }
            val intent = Intent(context, targetActivity)
            intent.putExtra(CreatePageConfigs.EXTRA_CREATE_TYPE, itemList[position].type.name)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.createName)
        val itemImage: AppCompatImageView = view.findViewById(R.id.createImg)
    }
}
