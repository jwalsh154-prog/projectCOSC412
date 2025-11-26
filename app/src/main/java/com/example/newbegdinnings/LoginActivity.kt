package com.example.newbegdinnings

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogle: LinearLayout
    private lateinit var tvSignUp: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogle = findViewById(R.id.btnGoogle)
        tvSignUp = findViewById(R.id.tvSignUp)

        auth = FirebaseAuth.getInstance()

        // Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (validateInput(email, password)) loginWithEmail(email, password)
        }

        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Toast.makeText(this, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loginWithEmail(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val user = auth.currentUser

                    // Check if user exists
                    if (user != null) {
                        // User exists → login successful → go to welcome screen
                        goToWelcomeScreen()
                    } else {
                        // Rare case, but still safe
                        Toast.makeText(this, "User not found!", Toast.LENGTH_LONG).show()
                    }

                } else {
                    // Firebase checks email + password exist or not
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToWelcomeScreen()
                } else {
                    Toast.makeText(this, "Firebase Auth Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToWelcomeScreen() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) { etEmail.error = "Email required"; return false }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.error = "Enter valid email"; return false }
        if (password.isEmpty()) { etPassword.error = "Password required"; return false }
        if (password.length < 6) { etPassword.error = "Password must be at least 6 characters"; return false }
        return true
    }
}