package com.example.criminalintentapp.presentation.fragments.crime_list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criminalintentapp.R
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.databinding.FragmentCrimeListBinding
import com.example.criminalintentapp.presentation.dialogs.ProgressDialog
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrimeListFragment : Fragment(R.layout.fragment_crime_list) {

    private lateinit var binding: FragmentCrimeListBinding
    private var adapter: CrimeAdapter = CrimeAdapter(emptyList())
    private var bundle = Bundle()
    private lateinit var progressDialog: ProgressDialog

    private val crimeListViewModel: CrimeListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrimeListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        progressDialog = ProgressDialog(requireActivity())

        crimeListViewModel.crimesListLiveData.observe(
            viewLifecycleOwner
        ) { crimes ->
            binding.emptyListTextView.isVisible = crimes.isEmpty()
            Log.i(TAG, "Got crimes ${crimes.size}")
            setupUI(crimes)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                progressDialog.showProgressDialog()
                val crime = Crime()
                bundle.putInt(ARG_CRIME_ID, crime.id)
                progressDialog.hideProgressDialog()
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
                true
            }

            R.id.logout -> {
                progressDialog.showProgressDialog()
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                progressDialog.hideProgressDialog()
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_loginFragment)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupUI(crimes: List<Crime>) {
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CrimeAdapter(crimes)
        binding.crimeRecyclerView.adapter = adapter
        adapter.setOnClickListener(object : CrimeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                progressDialog.showProgressDialog()
                bundle.putInt(ARG_CRIME_ID, crimes[position].id)
                progressDialog.hideProgressDialog()
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
            }
        })
    }

    companion object {
        private const val TAG = "CrimeListFragment"
        private const val ARG_CRIME_ID = "crime_id"
    }
}
