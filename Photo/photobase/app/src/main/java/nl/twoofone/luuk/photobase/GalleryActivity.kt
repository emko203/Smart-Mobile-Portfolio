package nl.twoofone.luuk.photobase

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    var imageList = mutableListOf<Image>()
    lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        val reference = database.getReference("images")
        adapter = ImageAdapter(this, imageList)
        imageGrid!!.adapter = adapter
        reference.addChildEventListener(itemListener)
    }

    var itemListener: ChildEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            imageList.remove(snapshotToImage(snapshot))
            adapter.notifyDataSetChanged()
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            imageList.add(snapshotToImage(snapshot))
            adapter.notifyDataSetChanged()
        }

    }

    private fun snapshotToImage(snapshot: DataSnapshot): Image {
        return Image(snapshot.key, Uri.parse(snapshot.value as String), "");
    }
}

