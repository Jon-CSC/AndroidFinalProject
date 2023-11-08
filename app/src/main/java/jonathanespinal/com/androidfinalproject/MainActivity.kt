package jonathanespinal.com.androidfinalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Notes:
 *
 * This app is intended to be a tool for keeping track of painting projects with photos and notes.
 *
 * All activities, except for login and addproject, have a menu to navigate the root activities of
 * the app.
 *
 * Library page has a RecyclerView list of projects.
 *      At the top is an add project button which allows for the addition of more projects by the
 *      user. Each project requires a title, short descriptor, and photo. The project is added to
 *      the library immediately after the photo is taken. Tapping on a created project in the
 *      library recyclerview opens that project's details for viewing and editing.
 *
 * Project Details is accessed by tapping a project in the library.
 *      The Notes field contains the user's notes on a project, which is stored in the database. Any
 *      modifications are automatically saved to the database when leaving the details activity.
 *      The button launches the camera to add more photos to the project's details. These images
 *      appear in the scrolling gallery in the bottom half of the view.
 *
 * Settings page contains the preferences.
 *      Font size affects the size of text in the project details "Notes" field.
 *      Dark/light mode toggles those app themes.
 *
 * About menu item leads to the About activity, which is self explanatory.
 *
 * Logout menu item logs the user out of firebase and returns the user to the entrance of the app
 * (the login page)
 *
 */
class MainActivity : AppCompatActivity() {
    private lateinit var buttonLogin: Button
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp = getSharedPreferences("preferences.xml", Context.MODE_PRIVATE)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        buttonLogin = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val email = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            if (email != "" && password != "") {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        this
                    ) { task ->
                        // Was the sign in successful?
                        if (task.isSuccessful) {
                            val intent = Intent(this@MainActivity, LibraryActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.d("MY DEBUG", "Log In error")
                            Toast.makeText(
                                this@MainActivity,
                                "Login was not successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetching the stored data from the SharedPreference
        if (sp.getBoolean("dark_mode_preference", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}