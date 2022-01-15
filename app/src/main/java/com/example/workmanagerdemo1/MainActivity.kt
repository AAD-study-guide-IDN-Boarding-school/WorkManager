package com.example.workmanagerdemo1

import DownloadingWorker
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.workmanagerdemo1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val KEY_COUNT_VALUE = "key_count"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            setOneTimeWorkRequest()
        }

    }

    private fun setOneTimeWorkRequest() {
        val workManager = WorkManager.getInstance(applicationContext)

        // constraint digunakan untuk syarat worker akan berjalan apabila constraint terpenuhi
        val constraints = Constraints.Builder()
            // bisa berjalan jika hp di cas dan internet konek
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // untuk membuat input ke dalam worker UploadWorker.kt
        val data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .build()

        //membuat work request
        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        val filteringWorker = OneTimeWorkRequest.Builder(FilteringWorker::class.java).build()
        val compressWorker = OneTimeWorkRequest.Builder(CompressWorker::class.java).build()
        val downloadingWorker = OneTimeWorkRequest.Builder(DownloadingWorker::class.java).build()

        val parallelWorker = mutableListOf<OneTimeWorkRequest>()
        parallelWorker.add(downloadingWorker)
        parallelWorker.add(filteringWorker)


        // urutan workmanager saat run dari begin - then(uploadRequest)
        workManager
            .beginWith(parallelWorker)
            .then(compressWorker)
            .then(uploadRequest)
            .enqueue()

        // mendapatkan status workmanager
        workManager.getWorkInfoByIdLiveData(uploadRequest.id)
            .observe(this, Observer {
                binding.status.text = it.state.name

                // menerima data dari UploadWorker.kt, akan diterima ketika success
                if (it.state.isFinished) {
                    val data = it.outputData
                    val message = data.getString(UploadWorker.KEY_WORKER)
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}

