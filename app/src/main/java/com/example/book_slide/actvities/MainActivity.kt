package com.example.book_slide.actvities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.book_slide.R
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // esto es algo para comenzar.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        btnActualizar.setOnClickListener {
            val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val saludo = when (hora) {
                in 0..11 -> "¡Buenos días!, Listo para leer"
                in 12..18 -> "¡Buenas tardes!, Listo para leer?"
                else -> "¡Buenas noches!, Listo para leer?"
            }
            tvSaludo.text = saludo
        }

        // Direccion a pantalla Home

        val btnInicio: Button = findViewById(R.id.btnPantallaInicio)
        btnInicio.setOnClickListener {
            val intent: Intent = Intent(this, MenuListaLibros::class.java)
            startActivity(intent)
        }
    }
}