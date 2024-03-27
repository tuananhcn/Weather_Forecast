package com.example.weatherforecast.ui.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weatherforecast.R
import com.example.weatherforecast.utils.collectFlow
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.model.SupportedLanguage
import com.example.weatherforecast.model.Units
import com.example.weatherforecast.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::class.java)

    val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SettingSpinnerAdapter(
            requireContext(),
            SupportedLanguage.getLanguageSpinnerItem()
        ).also { adapter ->
            binding.langSpinner.adapter = adapter
        }

        SettingSpinnerAdapter(requireContext(), Units.getUnitsSpinnerItem()).also { adapter ->
            binding.unitsSpinner.adapter = adapter
        }

        binding.langSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val language = SupportedLanguage.getLanguageValue(pos)
                if (viewModel.state.value.selectedLanguage != language) {
                    viewModel.setLanguage(language)
//                    if(language != "vi") {
                        changeLanguage(requireContext(), language)
//                    }
//                    else if(language == "zh") {
//                        changeLanguage(requireContext(), language)
//                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.unitsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val units = Units.getUnitsValue(pos)
                if (viewModel.state.value.selectedUnits != units) {
                    viewModel.setUnits(units)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        observeSettingsState()
    }

    private fun observeSettingsState() = with(binding) {
        collectFlow(viewModel.state) { state ->
            langSpinner.setSelection(SupportedLanguage.getIndex(state.selectedLanguage))
            unitsSpinner.setSelection(Units.getIndex(state.selectedUnits))
            versionTextView.text = state.versionInfo
        }
    }
    private fun changeLanguage(context: Context, languageCode: String) {
        var locale = Locale(languageCode);
        if(languageCode != "en"){
            locale = Locale(languageCode)
            Locale.setDefault(locale)
        }
        else{
            locale = Locale.getDefault()
            Locale.setDefault(locale)
        }

        // Get the resources object from the application context
        val resources = context.resources

        // Create a new configuration and set the locale
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        // Update the configuration
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Restart the activity to apply the language change
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}