package YiZhan.Wong.doineedit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    // Instantiation the widgets
    private TextView registerTitle;
    private EditText firstNameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private RadioGroup radioGroupGender;
    private Button registerSuccessfully;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Link the widgets by findViewById
        registerTitle = findViewById(R.id.textViewTitleRegister);
        firstNameEditText = findViewById(R.id.editTextFirstName);
        surnameEditText = findViewById(R.id.editTextSurname);
        emailEditText = findViewById(R.id.editTextEmailRegister);
        passwordEditText = findViewById(R.id.editTextPasswordRegister);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        registerSuccessfully = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.create_acct_progress);

        // set onClickListener for button and text view
        registerTitle.setOnClickListener(this);
        registerSuccessfully.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(this, HomePage.class);

            startActivity(intent);
        }
        else
        {
            Log.d("currentUserCheck","not logged in");
        }
    }

    // Create intent that link to the HomePage activity by using OnClickListener
    @Override
    public void onClick(View view) {
        // Get text from edit texts and radio button
        String fName = firstNameEditText.getText().toString();
        String sName = surnameEditText.getText().toString();
        int genderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton radioGender = findViewById(genderId);
        String gender = radioGender.getText().toString();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        int id = view.getId(); // start action based on ID

        // if title is clicked return to MainActivity
        if (id == R.id.textViewTitleRegister){
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
        }
        // if register button is clicked run the functions
        else if (id == R.id.buttonRegister)
        {
            userInfo(fName, sName, email, gender);
            newUser(email, password);
        }
    }

    // Create new user
    private void newUser(String email, String password){
        progressBar.setVisibility(View.VISIBLE); // Display when start to add new user


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d("TAG", "createUserWithEmail:success"); // Log display success message

                    // Proceed to HomePage Activity
                    Intent intent = new Intent(Register.this, HomePage.class);
                    startActivity(intent);

                    Toast.makeText(getBaseContext(),"Register successfully", Toast.LENGTH_SHORT).show(); // Display success message
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.getException()); // Log display fail message
                    Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_LONG).show(); // Dispaly fail message
                }
            }
        });

        progressBar.setVisibility(View.INVISIBLE); // Hide when finish the process
    }

    // function to add user info
    private void userInfo(String fName, String sName, String email, String gender){
        // Instantiate FireStore
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new map for user information
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstName", fName);
        userInfo.put("surname", sName);
        userInfo.put("email", email);
        userInfo.put("gender", gender);

        // add the userInfo into the FireStore
        db.collection("userInfo").add(userInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("FSLog", "DocumentSnapshot added with ID" + documentReference.getId()); // Log display success message
                Toast.makeText(Register.this, "Added Successfully", Toast.LENGTH_SHORT).show(); // Display success message

                // Proceed to HomePage activity
                Intent intent = new Intent(Register.this, HomePage.class);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("FSLog", "Error adding document", e); // Log display fail message
                Toast.makeText(Register.this, "Failed to add", Toast.LENGTH_SHORT).show(); // Display fail message
            }
        });
    }
}