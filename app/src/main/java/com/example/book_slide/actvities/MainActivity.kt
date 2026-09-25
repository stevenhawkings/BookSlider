package com.example.book_slide.actvities

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.book_slide.R
import com.example.book_slide.databinding.ActivityMenuListaLibrosBinding
import com.example.book_slide.fragment.HomeFragment
import com.example.book_slide.fragment.LogInFragment
import com.example.book_slide.fragment.ProfileFragment
import com.example.book_slide.fragment.SettingsFragment
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuListaLibrosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuListaLibrosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMenu)

        val currentMode = ColorBlindnessManager.init(this)
        applyColorBlindnessUI(currentMode)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, HomeFragment()).commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContainer,
                    SettingsFragment()
                ).commit()

                R.id.home -> supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContainer,
                    HomeFragment()
                ).commit()
            }
            true
        }
    }

    fun applyColorBlindnessUI(mode: ColorBlindnessMode) {
        val colorStateList = ColorStateList.valueOf(mode.primaryColorHex)
        binding.toolbarMenu.setTitleTextColor(mode.primaryColorHex)
        binding.bottomNavigation.itemIconTintList = colorStateList
        binding.bottomNavigation.itemTextColor = colorStateList

        val indicatorAlphaColor = (mode.primaryColorHex and 0x00FFFFFF) or 0x33000000
        binding.bottomNavigation.itemActiveIndicatorColor = ColorStateList.valueOf(indicatorAlphaColor)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.iniciar_sesion -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContainer,
                    LogInFragment()
                ).commit()
                true
            }

            R.id.perfil -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContainer,
                    ProfileFragment()
                ).commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}