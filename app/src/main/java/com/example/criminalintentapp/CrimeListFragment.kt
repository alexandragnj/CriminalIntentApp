package com.example.criminalintentapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

class CrimeListFragment : Fragment(R.layout.fragment_crime_list) {

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emptyListTextView = view.findViewById(R.id.empty_list_text_view) as TextView

        crimeListViewModel.crimesListLiveData.observe(
            viewLifecycleOwner
        ) { crimes ->
            if (crimes.isEmpty()) {
                emptyListTextView.isVisible = true
                emptyListTextView.setText(R.string.empty_list)
            }
            Log.i(TAG, "Got crimes ${crimes.size}")
            setupUI(view, crimes)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun setupUI(view: View, crimes: List<Crime>) {
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView

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
