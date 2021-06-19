package nl.twoofone.luuk.photobase

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.contentValuesOf
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_layout.view.*

class ImageAdapter(private var context: Context, imageList: MutableList<Image>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = imageList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageUri: Uri = itemList.get(position).uri
        val view: View
        val rowHolder: ListRowHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.image_layout, parent, false)
            rowHolder = ListRowHolder(view)
            view.tag = rowHolder
        } else {
            view = convertView
        }
        Glide.with(context)
            .load(imageUri)
            .placeholder(R.drawable.robot)
            .into(view.imageView)
        return view
    }
    override fun getItem(index: Int): Any {
        return itemList.get(index)
    }
    override fun getItemId(index: Int): Long {
        return index.toLong()
    }
    override fun getCount(): Int {
        return itemList.size
    }
    private class ListRowHolder(row: View?) {
        val image: ImageView = row!!.imageView
    }
}