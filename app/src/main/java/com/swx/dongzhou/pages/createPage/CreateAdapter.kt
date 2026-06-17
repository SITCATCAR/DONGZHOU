package com.swx.dongzhou.pages.createPage

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.swx.dongzhou.Activities.CreateActivities.FaceBookCreateActivity
import com.swx.dongzhou.R

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
        holder.itemImage.setImageResource(getItemImage(itemList[position]))
        holder.itemView.setOnClickListener {
            val intent = Intent(context, FaceBookCreateActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun getItemImage(createItem: CreateItem): Int{
       return when(createItem.type){
            CreateItemType.Website -> R.mipmap.ic_url
            CreateItemType.WIFI -> R.mipmap.ic_wifi
            CreateItemType.Text -> R.mipmap.ic_text
            CreateItemType.Contact -> R.mipmap.ic_contact
            CreateItemType.Tel -> R.mipmap.ic_tel
            CreateItemType.Email -> R.mipmap.ic_email
            CreateItemType.SMS -> R.mipmap.ic_sms
            CreateItemType.Calendar -> R.mipmap.ic_calendar
            CreateItemType.MyCard -> R.mipmap.ic_mecard
            CreateItemType.FaceBook -> R.mipmap.ic_facebook
            CreateItemType.Instagram -> R.mipmap.ic_ins
            CreateItemType.WhatsApp -> R.mipmap.ic_whatsapp
            CreateItemType.Youtube -> R.mipmap.ic_youtobe
            CreateItemType.Twitter -> R.mipmap.ic_twitter
            CreateItemType.Spotify -> R.mipmap.ic_spotify
            CreateItemType.Paypal -> R.mipmap.ic_paypal
            CreateItemType.Viber -> R.mipmap.ic_viber
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.createName)
        val itemImage: AppCompatImageView = view.findViewById(R.id.createImg)
    }
}