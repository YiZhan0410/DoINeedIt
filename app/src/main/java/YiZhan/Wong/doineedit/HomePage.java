package YiZhan.Wong.doineedit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    // Instantiate the widgets
    private TextView savedProduct;
    private TextView saveLater;
    private TextView myAccount;
    private Button logout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Link the widgets by findViewById
        setContentView(R.layout.activity_home_page);
        savedProduct = findViewById(R.id.textViewSavedProduct);
        saveLater = findViewById(R.id.textViewSavedLater);
        myAccount = findViewById(R.id.textViewMyAccount);
        logout = findViewById(R.id.buttonLogout);

        // Set OnClickListener to the button and text views
        savedProduct.setOnClickListener(this);
        saveLater.setOnClickListener(this);
        myAccount.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId(); // start action based on ID

        // if mark as purchased is clicked proceed to mark as purchased
        if (id == R.id.textViewSavedProduct){

            Intent intent = new Intent(this, SavedProduct.class);
            startActivity(intent);

        }
        // if saved product is clicked proceed to saved product
        else if (id == R.id.textViewSavedLater){

            Intent intent = new Intent(this, SavedLater.class);
            startActivity(intent);

        }
        // if my account is clicked proceed to my account
        else if (id == R.id.textViewMyAccount){

            Intent intent = new Intent(this, Account.class);
            startActivity(intent);

        }
        // if logout button is clicked logout the user and return to MainActivity
        else if (id == R.id.buttonLogout){

        mAuth.signOut();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        }
    }
}