package jonathanespinal.com.androidfinalproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemLibrary -> {
                val intent = Intent(this@AboutActivity, LibraryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemSettings -> {
                val intent = Intent(this@AboutActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemLogOut -> {
                Firebase.auth.signOut()
                val intent = Intent(this@AboutActivity, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}