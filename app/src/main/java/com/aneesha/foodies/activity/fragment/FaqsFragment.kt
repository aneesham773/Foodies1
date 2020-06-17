package com.aneesha.foodies.activity.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.aneesha.foodies.R

/**
 * A simple [Fragment] subclass.
 * Use the [FaqsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FaqsFragment(val contextParam:Context) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_faqs, container, false)
        return view
    }


}
