package com.example.book_slide.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.book_slide.R
import com.example.book_slide.actvities.MainActivity
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode

class SettingsFragment : Fragment() {

    private lateinit var tvTitle: TextView
    private lateinit var rgDaltonism: RadioGroup
    private lateinit var rbNormal: RadioButton
    private lateinit var rbProtanopia: RadioButton
    private lateinit var rbDeuteranopia: RadioButton
    private lateinit var rbTritanopia: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle = view.findViewById(R.id.tvSettingsTitle)
        rgDaltonism = view.findViewById(R.id.rgDaltonism)
        rbNormal = view.findViewById(R.id.rbNormal)
        rbProtanopia = view.findViewById(R.id.rbProtanopia)
        rbDeuteranopia = view.findViewById(R.id.rbDeuteranopia)
        rbTritanopia = view.findViewById(R.id.rbTritanopia)

        val currentMode = ColorBlindnessManager.getMode(requireContext())
        selectRadioButtonForMode(currentMode)
        applyModeToSettingsUI(currentMode)

        rgDaltonism.setOnCheckedChangeListener { _, checkedId ->
            val newMode = when (checkedId) {
                R.id.rbProtanopia -> ColorBlindnessMode.PROTANOPIA
                R.id.rbDeuteranopia -> ColorBlindnessMode.DEUTERANOPIA
                R.id.rbTritanopia -> ColorBlindnessMode.TRITANOPIA
                else -> ColorBlindnessMode.NORMAL
            }

            ColorBlindnessManager.setMode(requireContext(), newMode)
            applyModeToSettingsUI(newMode)
            (activity as? MainActivity)?.applyColorBlindnessUI(newMode)
        }
    }

    private fun selectRadioButtonForMode(mode: ColorBlindnessMode) {
        when (mode) {
            ColorBlindnessMode.NORMAL -> rbNormal.isChecked = true
            ColorBlindnessMode.PROTANOPIA -> rbProtanopia.isChecked = true
            ColorBlindnessMode.DEUTERANOPIA -> rbDeuteranopia.isChecked = true
            ColorBlindnessMode.TRITANOPIA -> rbTritanopia.isChecked = true
        }
    }

    private fun applyModeToSettingsUI(mode: ColorBlindnessMode) {
        val colorStateList = ColorStateList.valueOf(mode.primaryColorHex)
        tvTitle.setTextColor(mode.primaryColorHex)
        rbNormal.buttonTintList = colorStateList
        rbProtanopia.buttonTintList = colorStateList
        rbDeuteranopia.buttonTintList = colorStateList
        rbTritanopia.buttonTintList = colorStateList
    }
}
