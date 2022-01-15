package com.example.workmanagerdemo1

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class FilteringWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {


    override fun doWork(): Result {
        return try {

            for (a in 0..3000) {
                Log.d("MYTAG", "Filtering $a")
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}