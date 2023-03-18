package io.samagra.oce_sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.odk.collect.forms.Form

class FormListAdapter(private var forms: ArrayList<Form> = ArrayList()): RecyclerView.Adapter<FormListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var formId: TextView
        private lateinit var displayName: TextView
        fun bind(position: Int) {
            formId = itemView.findViewById(R.id.form_id)
            displayName = itemView.findViewById(R.id.form_display_name)

            formId.text = forms[position].formId
            displayName.text = forms[position].displayName
        }
    }

    fun setData(forms: ArrayList<Form>) {
        this.forms = forms
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.form_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return forms.size
    }
}