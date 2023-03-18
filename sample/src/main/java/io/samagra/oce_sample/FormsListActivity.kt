package io.samagra.oce_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.samagra.odk.collect.extension.components.DaggerFormsDatabaseInteractorComponent

class FormsListActivity : AppCompatActivity() {

    private lateinit var adapter: FormListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms_list)

        recyclerView = findViewById(R.id.form_list_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val formsDatabaseInteractor =
            DaggerFormsDatabaseInteractorComponent.factory().create(application)
                .getFormsDatabaseInteractor()
        val forms = formsDatabaseInteractor.getLocalForms() as ArrayList

        adapter = FormListAdapter(forms)
        adapter.setData(forms)
        recyclerView.adapter = adapter
    }
}