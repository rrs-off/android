package com.example.lab1

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.lab1.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IntentsandDeepLinking: Fragment() {

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_intentsand_deep_linking, container, false)

        val btnPickAndShare = view.findViewById<Button>(R.id.btn_idl)

        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    imageUri = uri
                    openInstagramStories(uri)
                }
            }
        }

        btnPickAndShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        return view
    }

    private fun openInstagramStories(imageUri: Uri) {
        try {
            val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                setDataAndType(imageUri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.instagram.android")
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                redirectToPlayStore()
            }
        } catch (e: ActivityNotFoundException) {
            redirectToPlayStore()
        }
    }

    private fun redirectToPlayStore() {
        val playStoreIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.instagram.android")
        )
        startActivity(playStoreIntent)
    }
}
