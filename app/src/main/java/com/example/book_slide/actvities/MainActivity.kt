package com.example.book_slide.actvities
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.book_slide.R
import com.example.book_slide.databinding.ActivityMenuListaLibrosBinding
import com.example.book_slide.fragment.HomeFragment
import com.example.book_slide.fragment.LogInFragment
import com.example.book_slide.fragment.SettingsFragment
import com.example.book_slide.fragment.addFragment
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuListaLibrosBinding

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_menu_lista_libros)

         // hacer que cada vez que se presione un item se diriga a un fragmento
         binding = ActivityMenuListaLibrosBinding.inflate(layoutInflater)
         setContentView(binding.root)
         setSupportActionBar(binding.toolbarMenu)
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, HomeFragment()).commit()
         binding.bottomNavigation.setOnItemSelectedListener {
             when (it.itemId){
                 R.id.add_circle_menu -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                     addFragment()).commit()
                 R.id.settings -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                     SettingsFragment()).commit()
                 R.id.home -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,
                     HomeFragment()).commit()
             }
             true
         }
     }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.iniciar_sesion -> {
                Toast.makeText(this, "Iniciar sesion pronto", Toast.LENGTH_LONG).show()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContainer,
                    LogInFragment()
                ).commit()
                true
            }
            R.id.settings-> {
                Toast.makeText(this, "Configuracion pronto", Toast.LENGTH_LONG).show()
                // Agregar fragmento de configuracion
                true
            }

            R.id.perfil -> {
                Toast.makeText(this, "Perfil pronto", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}