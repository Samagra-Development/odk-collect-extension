package io.samagra.oce_sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class JSONViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jsonview)

        val extras = intent.extras
        if (extras != null) {
            val jsonData = extras.getString("jsonData")

            var JsonTV : TextView = findViewById(R.id.tv_json)
            JsonTV.text = jsonData;

        }
    }
}