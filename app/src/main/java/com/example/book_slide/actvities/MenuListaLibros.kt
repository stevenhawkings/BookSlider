package com.example.book_slide.actvities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.book_slide.R
import com.example.book_slide.databinding.ActivityMenuListaLibrosBinding
import com.example.book_slide.fragment.HomeFragment
import com.example.book_slide.fragment.SettingsFragment
import com.example.book_slide.fragment.addFragment

class MenuListaLibros : AppCompatActivity() {
    lateinit var binding: ActivityMenuListaLibrosBinding

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_menu_lista_libros)
         // hacer que cada vez que se presione un item se diriga a un fragmento

         binding = ActivityMenuListaLibrosBinding.inflate(layoutInflater)
         setContentView(binding.root)

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
}