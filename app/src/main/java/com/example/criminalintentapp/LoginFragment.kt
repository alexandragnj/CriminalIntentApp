package com.example.criminalintentapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.example.criminalintentapp.databinding.FragmentLoginBinding
import com.example.criminalintentapp.presentation.MainActivity
import com.example.criminalintentapp.presentation.authentication.AuthenticationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding= FragmentLoginBinding.inflate(inflater, container,false)
        //val view = inflater.inflate(R.layout.fragment_login, container, false)

        if (authenticationViewModel.currentUser != null) {
            goToMainActivity(false, binding)
        }


        initViewModelObservers(binding)
        setOnClickListeners(binding)

        return binding.root
    }

    private fun initViewModelObservers(binding: FragmentLoginBinding) {
        authenticationViewModel.userLoginLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                goToMainActivity(true, binding)
            }
        }

        authenticationViewModel.failureLiveData.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setOnClickListeners(binding: FragmentLoginBinding) {
        binding.tvSignUp.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnSignIn.setOnClickListener {
            //Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_crimeListFragment)
            tryToLogin(binding)
        }
    }

    private fun tryToLogin(binding: FragmentLoginBinding) {
        val email = binding.etSignInEmail.text.toString()
        val password = binding.etSignInPassword.text.toString()

        if (authenticationViewModel.checkFields(email, password)) {
            authenticationViewModel.login(email, password)
        } else {
            Toast.makeText(requireContext(), getString(R.string.empty_fields), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToMainActivity(clearBackStack: Boolean, binding: FragmentLoginBinding) {
        Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_crimeListFragment)
        /*val intent = Intent(this@LoginActivity, MainActivity::class.java)
        if (clearBackStack) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()*/
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}