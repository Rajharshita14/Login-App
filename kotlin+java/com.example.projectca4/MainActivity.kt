package com.example.projectca4

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var logout: ImageButton
    private lateinit var uname:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        logout=findViewById(R.id.imgLogout)
        uname=findViewById(R.id.textViewName)
        updateUI()

        val inboxBtn: ImageButton = findViewById(R.id.Inbox)
        val calendarBtn: ImageButton = findViewById(R.id.Calendar)
        val mapBtn: ImageButton = findViewById(R.id.Map)
        val settingsBtn: ImageButton = findViewById(R.id.Settings)
        val cameraBtn: ImageButton = findViewById(R.id.VideoCall)
        val contactsBtn: ImageButton = findViewById(R.id.VoiceCall)
        val whatsappBtn: ImageButton = findViewById(R.id.Chat)

        inboxBtn.setOnClickListener {
            openGmailInbox()
        }
        calendarBtn.setOnClickListener {
            openCalendar()
        }

        mapBtn.setOnClickListener {
            openGoogleMaps()
        }

        settingsBtn.setOnClickListener {
            openSettings()
        }
//        cameraBtn.setOnClickListener {
//            openCamera()
//        }

        contactsBtn.setOnClickListener {
            openContacts()
        }
        whatsappBtn.setOnClickListener {
            openWhatsApp()
        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val email = intent.getStringExtra("email")
        if (email != null ) {
            setText(email)
            with(sharedPref.edit()) {
                putString("Email", email)
                apply()
            }
        }else {
            val storedName = sharedPref.getString("Email", null)
            if (storedName != null) {
                setText(storedName)
            } else {
                Toast.makeText(this, "No email found.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
        }

        logout.setOnClickListener {
            sharedPref.edit().remove("Email").apply()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun openWhatsApp() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=9501199497")
        startActivity(intent)
    }

    private fun openContacts() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(ContactsContract.Contacts.CONTENT_URI, ContactsContract.Contacts.CONTENT_TYPE)
        startActivity(intent)
    }

//    private fun openCamera() {
//        val intent = packageManager.getLaunchIntentForPackage("com.google.android.apps.tachyon")
//        if (intent != null) {
//            startActivity(intent)
//        } else {
//            val playStoreIntent = Intent(Intent.ACTION_VIEW)
//            playStoreIntent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.tachyon")
//            startActivity(playStoreIntent)
//        }
//    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    private fun openGoogleMaps() {
        val intent = packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")
        if (intent != null) {
            startActivity(intent)
        } else {
            val playStoreIntent = Intent(Intent.ACTION_VIEW)
            playStoreIntent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
            startActivity(playStoreIntent)
        }
    }

    private fun openCalendar() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
        startActivity(intent)
    }

    private fun openGmailInbox() {
        val intent = packageManager.getLaunchIntentForPackage("com.google.android.gm")
        if (intent != null) {
            startActivity(intent)
        } else {
            val playStoreIntent = Intent(Intent.ACTION_VIEW)
            playStoreIntent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm")
            startActivity(playStoreIntent)
        }
    }


    private fun setText(email: String) {
        db=FirebaseFirestore.getInstance()
        db.collection("USERS").document(email).get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()){
                    val nameValue = documentSnapshot.getString("Name")
                    val phoneValue = documentSnapshot.getString("Phone")
                    if(nameValue!=null && phoneValue != null) {
                        uname.text = nameValue
                    }else {
                        Toast.makeText(this, "User data is incomplete.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun updateUI() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val username = currentUser.displayName
            if (!username.isNullOrBlank()) {
                uname.text = username
            } else {
                uname.text = "USER" // Default text if username is not set
            }
        } ?: run {
            uname.text = "Not logged in" // Default text if user is not logged in
        }
    }
}
