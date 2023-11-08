package jonathanespinal.com.androidfinalproject

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProjectDetailsActivity : AppCompatActivity() {
    private lateinit var textViewProjectNameHeader : TextView
    private lateinit var textViewProjectNameDescription : TextView
    private lateinit var imageViewProjectImageTop : ImageView
    private lateinit var buttonAddPhotoToGallery : Button
    private lateinit var editTextNotes : EditText
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var projectImageAdapter : ProjectImageAdapter
    private lateinit var recyclerViewProjectDetails : RecyclerView
    private lateinit var data : ArrayList<ProjectImage>
    private lateinit var currentPhotoPath: String

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // get file image was stored in
            var file = Uri.fromFile(File(currentPhotoPath))
            // Create a reference to project's image folder
            val projectImagePath = storage.reference.child(textViewProjectNameHeader.text.toString()+"/${file.lastPathSegment}")
            // upload
            var uploadTask = projectImagePath.putFile(file)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Toast.makeText(
                    this@ProjectDetailsActivity,
                    "Error uploading image",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener {
                getProject()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_details)
        textViewProjectNameHeader = findViewById(R.id.textViewProjectNameHeader)
        textViewProjectNameDescription = findViewById(R.id.textViewProjectDescriptionHeader)
        imageViewProjectImageTop = findViewById(R.id.imageViewProjectImageTop)
        buttonAddPhotoToGallery = findViewById(R.id.buttonAddGalleryPhoto)
        editTextNotes = findViewById(R.id.editTextNotes)
        buttonAddPhotoToGallery.setOnClickListener {
            dispatchTakePictureIntent()
        }
        recyclerViewProjectDetails = findViewById(R.id.recyclerViewProjectDetails)
        recyclerViewProjectDetails.layoutManager = LinearLayoutManager(this)
        data = ArrayList()
        db = Firebase.firestore
        storage = Firebase.storage
        projectImageAdapter = ProjectImageAdapter(data)
        recyclerViewProjectDetails.adapter = projectImageAdapter

    }

    override fun onResume() {
        super.onResume()
        val sp = getSharedPreferences("preferences.xml", Context.MODE_PRIVATE)
        val num = sp.getString("preference_font_size", "18")
        editTextNotes.textSize = num?.toFloat()!!

        getProject()
    }

    override fun onPause() {
        super.onPause()
        uploadNotes()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemLibrary -> {
                val intent = Intent(this@ProjectDetailsActivity, LibraryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemSettings -> {
                val intent = Intent(this@ProjectDetailsActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemAbout -> {
                val intent = Intent(this@ProjectDetailsActivity, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemLogOut -> {
                Firebase.auth.signOut()
                val intent = Intent(this@ProjectDetailsActivity, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getProject(){
        data.clear()
        db.collection("FinalModels")
            .whereEqualTo("name", intent.getStringExtra("name"))
            .whereEqualTo("description", intent.getStringExtra("description"))
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    textViewProjectNameHeader.text = document.get("name") as String
                    textViewProjectNameDescription.text = document.get("description") as String
                    editTextNotes.text = Editable.Factory.getInstance().newEditable(document.get("notes") as String)
                    Glide.with(imageViewProjectImageTop)
                        .load(document.get("image") as String)
                        .override(300, 300)
                        .centerInside()
                        .into(imageViewProjectImageTop)

                    // Get all of the project's images and render them
                    val listRef = storage.reference.child(document.get("imageDirectory") as String)
                    Log.d("MYDEBUG","Attempting to listAll from " + listRef.path)
                    listRef.listAll()
                        .addOnSuccessListener { (items, prefixes) ->
                            Log.d("MYDEBUG", "In onSuccess ListAll")
                            if (items.isEmpty()){
                                Log.d("MYDEBUG", "ITEMS EMPTY")
                            }
                            if (prefixes.isEmpty()){
                                Log.d("MYDEBUG", "PREFIXES EMPTY")
                            }
                            prefixes.forEach { prefix ->
                                Log.d("MYDEBUG", "In prefixes.forEach")
                                Log.d("MYDEBUG", prefix.path)
                                // All the prefixes under listRef.
                                // You may call listAll() recursively on them.
                            }

                            items.forEach { item ->
                                Log.d("MYDEBUG", "In items.forEach")
                                Log.d("MYDEBUG", item.path)

                                var path = item.path.substring(1)
                                // URL encode path string
                                path = path.replace("/","%2F")
                                var url =
                                    "https://firebasestorage.googleapis.com/v0/b/labs-91cdc.appspot.com/o/$path?alt=media"
                                url = url.replace(" ", "%20")

                                Log.d("MYDEBUG", url)

                                data.add(ProjectImage(url))
                            }
                            projectImageAdapter = ProjectImageAdapter(data)
                            recyclerViewProjectDetails.adapter = projectImageAdapter
                            projectImageAdapter.setOnItemClickListener(object: ProjectImageAdapter.ProjectImageAdapterListener {
                                override fun onClick(position: Int) {

                                }
                            })
                        }
                        .addOnFailureListener {
                            Log.d("MYDEBUG", "FAILED")
                            Toast.makeText(this@ProjectDetailsActivity,"Error getting images",Toast.LENGTH_SHORT).show()
                        }

                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
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
                    Toast.makeText(this@ProjectDetailsActivity,"Error creating file", Toast.LENGTH_SHORT).show()
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

    private fun uploadNotes() {
        intent.getStringExtra("name")
            ?.let { db.collection("FinalModels").document(it).update("notes", editTextNotes.text.toString()) }
    }
}


