package jonathanespinal.com.androidfinalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity(),SharedPreferences.OnSharedPreferenceChangeListener  {
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sp = getSharedPreferences("preferences.xml", Context.MODE_PRIVATE)
        sp.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemLibrary -> {
                val intent = Intent(this@SettingsActivity, LibraryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemAbout -> {
                val intent = Intent(this@SettingsActivity, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemLogOut -> {
                Firebase.auth.signOut()
                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
        if (sp.getBoolean("dark_mode_preference",false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val num = sp.getString("preference_font_size", "18")
        if (num != null) {
            if (!num.matches("\\d*".toRegex()) || num == "" ){
                sp.edit().putString("preference_font_size","18").apply()

            }
        }
    }
}