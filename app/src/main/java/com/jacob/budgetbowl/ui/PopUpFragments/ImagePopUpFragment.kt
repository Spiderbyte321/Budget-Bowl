package com.jacob.budgetbowl.ui.PopUpFragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.jacob.budgetbowl.R


class ImagePopUpFragment(val imageToShow: Bitmap) : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_pop_up, container, false)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setView(view)

        return dialogBuilder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageView: ImageView = requireView().findViewById(R.id.imageToShow)

        imageView.setImageBitmap(imageToShow)
    }

}