package com.example.criminalintentapp.presentation.fragments.crime_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.R
import java.util.UUID

class CrimeListFragment : Fragment(R.layout.fragment_crime_list) {

    interface Callbacks {
        fun onCrimeSelected(crimeId: Int?)
    }

    var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var emptyListTextView: TextView
    private var adapter: CrimeAdapter = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

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
            if (crimes.isEmpty()) {
                emptyListTextView.isVisible = true
                emptyListTextView.setText(R.string.empty_list)
            }
            Log.i(TAG, "Got crimes ${crimes.size}")
            setupUI(crimes)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
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
                callbacks?.onCrimeSelected(crimes[position].id)
            }
        })
    }

    companion object {
        private const val TAG = "CrimeListFragment"

        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}
