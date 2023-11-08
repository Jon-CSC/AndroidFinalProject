package jonathanespinal.com.androidfinalproject

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class AddProjectActivity : AppCompatActivity() {
    private lateinit var buttonAddProjectPhoto : Button
    private lateinit var editTextProjectNameEntry : EditText
    private lateinit var editTextProjectDescriptionEntry : EditText
    private lateinit var imageViewThumbnail : ImageView
    private lateinit var db : FirebaseFirestore
    private val storage = Firebase.storage
    private lateinit var currentPhotoPath: String

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (editTextProjectNameEntry.text.toString() != "") {
                // Set the image in imageview for display
                // get file image was stored in
                val file = Uri.fromFile(File(currentPhotoPath))
                imageViewThumbnail.setImageURI(file)
                // Create a reference to project's image folder
                val projectImagePath = storage.reference.child(editTextProjectNameEntry.text.toString() + "/thumbnail.jpg")
                // upload
                val uploadTask = projectImagePath.putFile(file)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(
                        this@AddProjectActivity,
                        "Error uploading image",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnSuccessListener {
                    val intent = Intent(this@AddProjectActivity, LibraryActivity::class.java)
                    startActivity(intent)
                }
                // upload project data to firebase
                val project = hashMapOf(
                    "name" to editTextProjectNameEntry.text.toString(),
                    "description" to editTextProjectDescriptionEntry.text.toString(),
                    "image" to "https://firebasestorage.googleapis.com/v0/b/labs-91cdc.appspot.com/o/"+ editTextProjectNameEntry.text.toString() + "%2Fthumbnail.jpg?alt=media",
                    "imageDirectory" to editTextProjectNameEntry.text.toString(),
                    "notes" to ""
                )

                db.collection("FinalModels").document(editTextProjectNameEntry.text.toString())
                    .set(project)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            }else{
                Toast.makeText(
                    this@AddProjectActivity,
                    "Project Name required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        buttonAddProjectPhoto = findViewById(R.id.buttonAddProjectPhoto)
        imageViewThumbnail = findViewById(R.id.imageView2)
        // Set up the listeners for take photo button
        buttonAddProjectPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
        db = FirebaseFirestore.getInstance()
        editTextProjectNameEntry = findViewById(R.id.editTextProjectNameEntry)
        editTextProjectDescriptionEntry = findViewById(R.id.editTextProjectDescriptionEntry)

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Toast.makeText(this@AddProjectActivity,"Error creating file", Toast.LENGTH_SHORT).show()
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startForResult.launch(takePictureIntent)
            }
        }
    }

}