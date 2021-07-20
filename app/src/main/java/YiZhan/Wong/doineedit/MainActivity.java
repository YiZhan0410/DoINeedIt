package YiZhan.Wong.doineedit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Instantiate the widgets
    private Button linkToRegister;
    private Button loginButton;
    private EditText emailLogin;
    private EditText passwordLogin;
    private ProgressBar progressBarLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the widgets by findViewById
        linkToRegister = findViewById(R.id.buttonRegisterPage);
        loginButton = findViewById(R.id.buttonLoginPage);
        emailLogin = findViewById(R.id.editTextEmailLogin);
        passwordLogin = findViewById(R.id.editTextPasswordLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener to the buttons
        linkToRegister.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get current user else sign out automatically
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent intent = new Intent(this, AddItem.class);
            startActivity(intent);
        }else{
            mAuth.signOut();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId(); // start action based on ID

        // if register button is clicked proceed to Register Activity
        if(id == R.id.buttonRegisterPage){
            Log.i("Register", "we've clicked register");
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        }
        // if login button is clicked run the login function
        else if (id == R.id.buttonLoginPage){
            Log.i("signIn", "we've clicked log in");
            String loginEmail = emailLogin.getText().toString().trim();
            String loginPassword = passwordLogin.getText().toString().trim();
            login(loginEmail, loginPassword);
        }
    }

    // function to login
    private void login(String loginEmail, String loginPassword){
        progressBarLogin.setVisibility(View.VISIBLE); // Display when start to login

        // Run authentication process
        mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("LoginAuth", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    startActivity(intent);
                } else {
                    Log.w("LoginAuth", "signInWithEmail:Failure");
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                }
            }
        });

        progressBarLogin.setVisibility(View.INVISIBLE); // Hide when login finish
    }
}