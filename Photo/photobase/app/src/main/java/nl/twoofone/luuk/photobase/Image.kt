package nl.twoofone.luuk.photobase

import android.net.Uri

data class Image (
    val objectId: String?,
    val uri: Uri,
    val name: String?
)