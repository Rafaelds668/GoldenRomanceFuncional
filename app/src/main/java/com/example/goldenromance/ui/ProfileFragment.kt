package com.example.goldenromance.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.goldenromance.R
import com.example.goldenromance.databinding.FragmentProfileBinding
import com.example.goldenromance.model.UserModel
import com.example.goldenromance.utils.Config
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

       Config.showDialog(requireContext())

        binding = FragmentProfileBinding.inflate(layoutInflater)

        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid!!).get()
            .addOnSuccessListener {
                if( it.exists()){
                    val data = it.getValue(UserModel::class.java)

                    binding.name.setText(data!!.name.toString())
                    binding.city.setText(data!!.city.toString())
                    binding.email.setText(data!!.email.toString())
                    binding.number.setText(data!!.number.toString())

                    Glide.with(requireContext()).load(data.image).placeholder(R.drawable.profile).into(binding.userImage)

                    Config.hideDialog()
                }

            }

        return binding.root
    }

}