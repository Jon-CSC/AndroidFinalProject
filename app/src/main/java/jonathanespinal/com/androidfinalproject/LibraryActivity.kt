package jonathanespinal.com.androidfinalproject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LibraryActivity : AppCompatActivity() {
    private lateinit var recyclerViewModel : RecyclerView
    private lateinit var data : ArrayList<Model>
    private lateinit var modelAdapter : ModelAdapter
    private lateinit var buttonAddProject : Button
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        recyclerViewModel = findViewById(R.id.recyclerViewLibrary)
        recyclerViewModel.layoutManager = LinearLayoutManager(this)
        data = ArrayList()
        db = Firebase.firestore
        modelAdapter = ModelAdapter(data)
        recyclerViewModel.adapter = modelAdapter
        loadModels()
        buttonAddProject = findViewById(R.id.buttonAddProject)
        buttonAddProject.setOnClickListener {
            val intent = Intent(this@LibraryActivity, AddProjectActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemLibrary -> {
                val intent = Intent(this@LibraryActivity, LibraryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemSettings -> {
                val intent = Intent(this@LibraryActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemAbout -> {
                val intent = Intent(this@LibraryActivity, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemLogOut -> {
                Firebase.auth.signOut()
                val intent = Intent(this@LibraryActivity, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadModels(){
        data.clear()
        db.collection("FinalModels")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name: String = document.get("name") as String
                    val description : String = document.get("description") as String
                    val image = document.get("image") as String
                    data.add(Model(name, description, image))
                }
                modelAdapter = ModelAdapter(data)
                recyclerViewModel.adapter = modelAdapter
                // set listener for clicking on project items
                modelAdapter.setOnItemClickListener(object: ModelAdapter.ModelAdapterListener {
                    override fun onClick(position: Int) {
                        // open project details of selected item
                        val model = data[position]
                        val i = Intent(this@LibraryActivity,ProjectDetailsActivity::class.java)
                        i.putExtra("name", model.name)
                        i.putExtra("description",model.description)
                        i.putExtra("image",model.imageURL)
                        startActivity(i)
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

    }
}
