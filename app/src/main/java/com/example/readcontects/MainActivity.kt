package com.example.readcontects

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            requestContacts()
        } else {
            Log.d("MainActivity_denied", "Permission denied 1")
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_RC
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_CONTACTS_RC && grantResults.isNotEmpty()) {
            Log.d("MainActivity_first", grantResults[0].toString())
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED



            if (permissionGranted) {
                requestContacts()
            } else {
                Log.d("MainActivity_denied", "Permission denied 2")

                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                    Log.d("MainActivity_denied", "USer press 'don't ask again'")
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestContacts() {
        thread {
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null,
            )

            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)) ?: ""

                val contact = Contact(id = id, name = name)

                Log.d("MainActivity_contacts", contact.toString())
            }

            cursor?.close()
        }
    }

    companion object {
        private const val READ_CONTACTS_RC = 100
    }
}