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
import com.example.criminalintentapp.data.database.FirestoreClass
import com.example.criminalintentapp.databinding.FragmentCrimeListBinding
import com.example.criminalintentapp.presentation.dialogs.ProgressDialog
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrimeListFragment : Fragment(R.layout.fragment_crime_list) {

    private lateinit var binding: FragmentCrimeListBinding
    private lateinit var adapter: CrimeAdapter
    private var bundle = Bundle()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var crimeList: ArrayList<Crime>

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

        //FirestoreClass().getCrimes()

        val syncWithCloud: Boolean?= arguments?.getBoolean(ARG_SYNC_CLOUD)
        Log.d(TAG,"SYNC: $syncWithCloud")
        if(syncWithCloud == true){
            progressDialog.show()
            Log.d(TAG,"syncWithCloud")
            crimeListViewModel.syncWithCloud()
            progressDialog.hide()
        }

        crimeListViewModel.crimesListLiveData.observe(
            viewLifecycleOwner
        ) { crimes ->
            binding.emptyListTextView.isVisible = crimes.isEmpty()
            Log.i(TAG, "Got crimes ${crimes.size}")
            setupUI(crimes as ArrayList<Crime>)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                progressDialog.show()
                val crime = Crime()
                bundle.putLong(ARG_CRIME_ID, crime.id)
                progressDialog.hide()
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
                true
            }

            R.id.logout -> {
                progressDialog.show()
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                progressDialog.hide()
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_loginFragment)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupUI(crimes: ArrayList<Crime>) {
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CrimeAdapter(crimes)
        binding.crimeRecyclerView.adapter = adapter
        adapter.setOnClickListener(object : CrimeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                progressDialog.show()
                bundle.putLong(ARG_CRIME_ID, crimes[position].id)
                progressDialog.hide()
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
            }
        })
    }

    companion object {
        private const val TAG = "CrimeListFragment"
        private const val ARG_CRIME_ID = "crime_id"
        private const val ARG_SYNC_CLOUD = "sync_cloud"
    }
}
