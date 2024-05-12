package com.example.loginsignup

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class add_item : Fragment() {
private lateinit var spinnerCategory: Spinner
private lateinit var categoryAdapter: ArrayAdapter<String>
private val categoryNamesList = mutableListOf<String>()
private lateinit var databaseReference: DatabaseReference
private lateinit var storageReference: StorageReference
private lateinit var etItemName: EditText
private lateinit var etItemDetail: EditText
private lateinit var etItemPrice: EditText
private lateinit var etPlace: EditText
private lateinit var btnAction: Button
private lateinit var ivItemImage: ImageView
private lateinit var tvUploadImage: TextView
private lateinit var selectedImageUri: Uri

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        val view = inflater.inflate(R.layout.fragment_add_items, container, false)

        etItemName = view.findViewById(R.id.etItemName)
        etItemDetail = view.findViewById(R.id.etItemDetail)
        etItemPrice = view.findViewById(R.id.etItemPrice)
        etPlace = view.findViewById(R.id.etPlace) // Added this line
        ivItemImage = view.findViewById(R.id.ivItemImage)
        btnAction = view.findViewById(R.id.btnAction)
        tvUploadImage = view.findViewById(R.id.tv_upload_image)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNamesList)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        databaseReference = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference
        fetchCategoryNames()

        btnAction.setOnClickListener {
        uploadDataToFirebase()
        }
        ivItemImage.setOnClickListener {
        selectImage()
        }

        return view
        }

private fun fetchCategoryNames() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        currentUserUid?.let { userId ->
        val categoriesRef = databaseReference.child("categories").child(userId)
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
        for (categorySnapshot in dataSnapshot.children) {
        val categoryName = categorySnapshot.child("categoryName").value.toString()
        categoryNamesList.add(categoryName)
        }
        categoryAdapter.notifyDataSetChanged()
        }

        override fun onCancelled(databaseError: DatabaseError) {
        // Handle errors
        }
        })
        }
        }

private fun uploadDataToFirebase() {
        val itemName = etItemName.text.toString()
        val itemDetail = etItemDetail.text.toString()
        val itemPrice = etItemPrice.text.toString().toDouble()
        val quantity = etPlace.text.toString().toInt()
        val category = spinnerCategory.selectedItem.toString()

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        currentUserUid?.let { userId ->
        val imageFileName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("itemimages/$imageFileName")
        val uploadTask = imageRef.putFile(selectedImageUri)
        uploadTask.addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { uri ->
        val currentItemRef = databaseReference.child("items").child(userId).push()
        val item = Item(itemName, itemDetail, itemPrice, category, uri.toString())
        currentItemRef.setValue(item)
        .addOnSuccessListener {
        Toast.makeText(requireContext(), "Item added successfully", Toast.LENGTH_SHORT).show()
        // Clear all the fields after adding the item
        etItemName.text.clear()
        etItemDetail.text.clear()
        etItemPrice.text.clear()
        etPlace.text.clear()
        ivItemImage.setImageResource(android.R.color.transparent)
        }
        .addOnFailureListener { exception ->
        // Display toast message if item is not added
        Toast.makeText(requireContext(), "Failed to add item", Toast.LENGTH_SHORT).show()
        }
        }
        }.addOnFailureListener { exception ->
        // Handle image upload failure
        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
        }
        }

private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
        selectedImageUri = data.data!!
        ivItemImage.setImageURI(selectedImageUri)
        }
        }

        companion object {
private const val PICK_IMAGE_REQUEST = 1
        }
        }
