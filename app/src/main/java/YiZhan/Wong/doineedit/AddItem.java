package YiZhan.Wong.doineedit;

// Import class
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    // Instantiate the widgets
    private TextView addPage;
    private EditText editTextProductTitle;
    private EditText editTextProductDescription;
    private EditText editTextPrice;
    private Button buttonAddProduct;
    private ProgressBar progressBarLoading;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productNew = db.collection("productNew");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Link the widgets by findViewById
        addPage = findViewById(R.id.textViewAddPageTitle);
        editTextProductTitle = findViewById(R.id.editTextProductTitle);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        // Set OnClickListener to the buttons
        addPage.setOnClickListener(this);
        buttonAddProduct.setOnClickListener(this);

        // Get intent from other applications
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("text/")) {
                handleSendText(intent); // Handle text being sent
            }
            else
            {
                // Handle other intents, such as being started from the home screen
                editTextProductTitle.setText("");
                editTextProductDescription.setText("");
                editTextPrice.setText("");
            }
        }
    }

    // Function to receive the text or URL link from other applications
    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            editTextProductDescription = findViewById(R.id.editTextProductDescription);
            editTextProductDescription.setText(sharedText);
        }
    }

    // Function for OnClick listener
    @Override
    public void onClick(View view) {
        int id = view.getId(); // Start action based on ID

        // if title is clicked back to home page
        if (id == R.id.textViewAddPageTitle){
            Intent intent = new Intent(this, SavedLater.class); // Proceed to SavedLater page
            startActivity(intent); // Start activity
        }

        // if add button is clicked data will pass into the FireStore
        else if(id == R.id.buttonAddProduct){
            // Get input data from text views
            String title = editTextProductTitle.getText().toString().trim();
            String description = editTextProductDescription.getText().toString().trim();
            double price = Double.parseDouble(editTextPrice.getText().toString().trim());
            progressBarLoading.setVisibility(View.VISIBLE); // Display when it starts to add product

            addProduct(title, description, price); // Add the input data to FireStore

            progressBarLoading.setVisibility(View.INVISIBLE); // Hide when it finish to add product
        }
    }

    // Function to add the product
    private void addProduct(String title, String description, Double price){

        // Create a new map to add product into the FireStore
        Map<String, Object> product = new HashMap<>();
        product.put("title", title);
        product.put("description", description);
        product.put("price", price);
        product.put("email", user.getEmail());

        // Add the product into the FireStore
       productNew.add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("FSLog", "DocumentSnapshot added with ID" + documentReference.getId());
                Toast.makeText(AddItem.this, "Added Successfully", Toast.LENGTH_SHORT).show(); // Display success message
                Intent intent = new Intent(AddItem.this, SavedLater.class); // Back to the SavedLater class
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("FSLog", "Error adding document", e);
                Toast.makeText(AddItem.this, "Failed to add", Toast.LENGTH_SHORT).show(); // Display fail message
            }
        });
    }

}