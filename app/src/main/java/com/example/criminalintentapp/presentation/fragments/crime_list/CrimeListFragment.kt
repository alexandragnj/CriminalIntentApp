package com.example.criminalintentapp.presentation.fragments.crime_list

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintentapp.R
import com.example.criminalintentapp.data.database.Crime
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class CrimeListFragment : Fragment(R.layout.fragment_crime_list) {

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var emptyListTextView: TextView
    private var adapter: CrimeAdapter = CrimeAdapter(emptyList())
    private var bundle = Bundle()

    private val crimeListViewModel: CrimeListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView(view)

        crimeListViewModel.crimesListLiveData.observe(
            viewLifecycleOwner
        ) { crimes ->
            emptyListTextView.isVisible = crimes.isEmpty()
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
                val crime = Crime()
                bundle.putInt("crime_id", crime.id)
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
                true
            }

            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_crimeListFragment_to_loginFragment)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun bindView(view: View) {
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        emptyListTextView = view.findViewById(R.id.empty_list_text_view) as TextView
    }

    private fun setupUI(crimes: List<Crime>) {
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
        adapter.setOnClickListener(object : CrimeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                bundle.putInt("crime_id", crimes[position].id)
                NavHostFragment.findNavController(this@CrimeListFragment)
                    .navigate(R.id.action_crimeListFragment_to_crimeFragment, bundle)
            }
        })
    }

    companion object {
        private const val TAG = "CrimeListFragment"
    }
}
