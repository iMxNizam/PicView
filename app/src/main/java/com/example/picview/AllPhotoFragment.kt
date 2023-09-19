package com.example.picview

import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.FragmentAllPhotoBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AllPhotoFragment : Fragment() {

    private var _binding: FragmentAllPhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context
    private lateinit var allPhotoAdapter: AllPhotoAdapter

    companion object {
        lateinit var imageList: ArrayList<ImageData>

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        context = activity?.applicationContext!!
        _binding = FragmentAllPhotoBinding.inflate(inflater, container, false)

        imageList = getImageList()

        binding.allPhotoRecyclerView.setHasFixedSize(true)
        binding.allPhotoRecyclerView.layoutManager = GridLayoutManager(context, 4)
        allPhotoAdapter = AllPhotoAdapter(context, imageList,"AllPhotos")
        binding.allPhotoRecyclerView.adapter = allPhotoAdapter

        return binding.root

    }

    private fun getImageList(): ArrayList<ImageData> {

        val tempImageList = ArrayList<ImageData>()

        val selection = MediaStore.Images.Media._ID

        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATA)

        val sortBy = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortBy
        )

        if (cursor != null) {
            if (cursor.moveToNext()) {

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

                do {
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    val id = cursor.getLong(idColumn)
                    val dateTaken = cursor.getLong(dateTakenColumn)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    val formattedDate = if (dateTaken == 0L) {
                        dateFormat.format(getDateModified(path))
                    } else {
                        dateFormat.format(dateTaken)
                    }

                    tempImageList.add(ImageData(imageUri, formattedDate,path))
                } while (cursor.moveToNext())
            }
        }

        cursor?.close()
        return tempImageList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDateModified(path: String): Long {
        return File(path).lastModified()
    }
}