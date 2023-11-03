package io.samagra.oce_sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.samagra.odk.collect.extension.utilities.ODKProvider

class InBuiltScreensActivity : AppCompatActivity() {

    private val odkInteractor by lazy {
        ODKProvider.getODKActivityInteractor()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_built_screens)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<Button>(R.id.drafts_list_btn).setOnClickListener {
//            odkInteractor.openDraftFormList(mutableListOf("sms_meetings", "sms_holiday"), this)
            odkInteractor.openDraftFormList(null, this)
        }
        findViewById<Button>(R.id.unsent_list_btn).setOnClickListener {
            odkInteractor.openUnSubmittedFormList(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}