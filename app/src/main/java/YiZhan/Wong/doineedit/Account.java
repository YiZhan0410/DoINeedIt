package YiZhan.Wong.doineedit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.core.Query;

import java.util.HashMap;
import java.util.Map;

public class Account extends AppCompatActivity implements View.OnClickListener{

    // Instantiate the widgets
    private TextView accountTitle;
    private EditText aFirstName;
    private EditText aSurname;
    private EditText aGender;
    private Button aUpdate;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference accInfo = db.collection("userInfo");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Link the widgets by findViewById
        accountTitle = findViewById(R.id.textViewAccountTitle);
        aFirstName = findViewById(R.id.editTextAccountFirstName);
        aSurname = findViewById(R.id.editTextAccountSurname);
        aGender = findViewById(R.id.editTextAccountGender);
        aUpdate = findViewById(R.id.buttonAccountUpdate);

        // set onClickListener to text view and button
        accountTitle.setOnClickListener(this);
        aUpdate.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // display account info from FireStore
        loadAcc();
    }

    // function to display account info
    private void loadAcc() {

        // Get account information based on email
        accInfo.whereEqualTo("email", user.getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            if(documentSnapshot.exists()){
                                Log.d("AccInfo", "Retrieve Successfully"); // Log display success message

                                // Get account information by using get method
                                AccountInfo accountInfo = documentSnapshot.toObject(AccountInfo.class);
                                String fName = accountInfo.getFirstName();
                                String sName = accountInfo.getSurname();
                                String gender = accountInfo.getGender();

                                // Display the information into each eidt text
                                aFirstName.setText(fName);
                                aSurname.setText(sName);
                                aGender.setText(gender);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("AccInfo", "Fail to retrieve.."); // Log display fail message
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId(); // start action based on ID

        // if title is clicked back to home page
        if (id == R.id.textViewAccountTitle){
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
        // if update button is clicked update the account information
        else if (id == R.id.buttonAccountUpdate){
            updateAcc();
        }
    }

    // function to update account information
    private void updateAcc() {
        // Get the account information based on email
        accInfo.whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document: task.getResult()){
                        // Get text from the edit texts
                        String fName = aFirstName.getText().toString().trim();
                        String sName = aSurname.getText().toString().trim();
                        String gender = aGender.getText().toString().trim();

                        // Create a new map to update account information
                        Map<String, Object> uAcc = new HashMap<>();
                        uAcc.put("firstName", fName);
                        uAcc.put("surname", sName);
                        uAcc.put("gender", gender);

                        accInfo.document(document.getId()).set(uAcc, SetOptions.merge()); // update the document based on ID

                        Toast.makeText(Account.this, "Update Successfully", Toast.LENGTH_SHORT).show(); // Display success message
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Account.this, "Fail to update", Toast.LENGTH_SHORT).show(); // Display fail message
            }
        });
    }
}