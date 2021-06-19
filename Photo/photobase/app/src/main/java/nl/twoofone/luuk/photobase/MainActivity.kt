package nl.twoofone.luuk.photobase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.os.Build





class MainActivity : AppCompatActivity() {

    val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String
    lateinit var photoURI: Uri
    val database = FirebaseDatabase.getInstance()
    var storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        galleryButton.setOnClickListener {
            val galleryIntent = Intent(this, GalleryActivity::class.java)
            startActivity(galleryIntent)
        }

        photoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    /**
     * Creates a file reference for image storing
     */
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", getCurrentLocale()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    @Suppress("DEPRECATION")
    fun getCurrentLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0) //  .getConfiguration().getLocales().get(0)
        } else {
            resources.configuration.locale
        }
    }

    /**
     * Dispatches the intent that triggers the camera app
     */
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = createImageFile()

                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "nl.twoofone.luuk.photobase.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    /**
     * Function that gets notified once the photo has been taken
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            imageView.setImageURI(photoURI)
            saveImageToFirebase(photoURI)
        }
    }

    /**
     * Saves the image to firebase storage
     */
    fun saveImageToFirebase(file: Uri){
        val ref = storage.getReference().child("images/" + file.lastPathSegment)

        ref.putFile(file)
            .addOnSuccessListener {
                // Get a URL to the uploaded content
                val downloadUrl = it.metadata!!.reference!!.downloadUrl
                downloadUrl.addOnSuccessListener {
                    saveUriToFirebase(it)
                    showImage(it)
                }
            }
            .addOnFailureListener {
                Log.e("FB", "Upload failed")
            }
    }

    /**
     * Save the image Firebase uri to the realtime database
     */
    fun saveUriToFirebase(uri: Uri){
        val images = database.getReference("images")
        images.push().setValue(uri.toString())
    }


    fun showImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.robot)
            .into(imageView)
    }

}
