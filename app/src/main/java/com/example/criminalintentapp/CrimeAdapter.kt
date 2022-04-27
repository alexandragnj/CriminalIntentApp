package com.example.criminalintentapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CrimeAdapter(var crimes: List<Crime>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val CRIME_SIMPLE = 1
    private val CRIME_POLICE = 2

    class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
        }

        override fun onClick(v: View?) {
            if (v != null) {
                Toast.makeText(v.context, "${crime.title} pressed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class CrimeHolderPolice(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title_police)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date_police)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
        }

        override fun onClick(v: View?) {
            if (v != null) {
                Toast.makeText(v.context, "${crime.title} pressed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val v: View =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(v)
        } else {
            val v: View =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_crime_police, parent, false)
            return CrimeHolderPolice(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val crime = crimes[position]

        if (holder.javaClass == CrimeHolder::class.java) {
            val viewHolder = holder as CrimeHolder
            viewHolder.bind(crime)
        } else {
            val viewHolder = holder as CrimeHolderPolice
            viewHolder.bind(crime)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val crime = crimes[position]

        if (crime.requiresPolice) {
            return CRIME_POLICE
        } else {
            return CRIME_SIMPLE
        }
    }

    override fun getItemCount(): Int {
        return crimes.size
    }
}
