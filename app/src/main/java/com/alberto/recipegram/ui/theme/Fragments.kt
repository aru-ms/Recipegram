package com.alberto.recipegram.ui.theme


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alberto.recipegram.R

class Fragments {

    class HomeFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_home, container, false)

            // Find the TextView in the layout
            val textView = view.findViewById<TextView>(R.id.homeTextView)

            // Set the text for the TextView
            textView.text = "Welcome to the Home Fragment!"

            return view
        }
    }

    class ProfileFragment : Fragment() {
        // Add necessary code for the Profile fragment
    }

    class SearchFragment : Fragment() {
        // Add necessary code for the Search fragment
    }

    class UploadFragment : Fragment() {
        // Add necessary code for the Upload fragment
    }
}