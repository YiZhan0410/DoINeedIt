package YiZhan.Wong.doineedit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.core.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProductDetails extends AppCompatActivity implements View.OnClickListener{

    // Instantiation variables
    private TextView productDetails;
    private TextView productID;
    private EditText dProductTitle;
    private EditText dProductDescription;
    private EditText dPrice;
    private Button edit;
    private Button mark;
    private Button share;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference product = db.collection("productNew");
    private CollectionReference markFS = db.collection("mark");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Link the widgets by findViewById
        productDetails = findViewById(R.id.textViewProductDetail);
        productID = findViewById(R.id.textViewTitleProductID);
        dProductTitle = findViewById(R.id.editTextProductTitleDetail);
        dProductDescription = findViewById(R.id.editTextProductDescriptionDetail);
        dPrice = findViewById(R.id.editTextPriceDetail);
        edit = findViewById(R.id.buttonEdit);
        mark = findViewById(R.id.buttonMark);
        share = findViewById(R.id.buttonShare);

        // Get intent from SavedLater/ SavedProduct class in bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String id = bundle.getString("id");
            String title = bundle.getString("title");
            String description = bundle.getString("description");
            double price = Double.parseDouble(String.valueOf(bundle.getDouble("price", 0.00)));

            // Add the data from the bundle into text view and edit texts
            productID.setText(id);
            dProductTitle.setText(title);
            dProductDescription.setText(description);
            dPrice.setText(String.valueOf(price));
        }

        // Another method to get intent
        /**String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        double price = getIntent().getDoubleExtra("price", 0.00);

        dProductTitle.setText(title);
        dProductDescription.setText(description);
        dPrice.setText(String.valueOf(price));**/

        // aet onClickLsstener to text view and edit texts
        productDetails.setOnClickListener(this);
        edit.setOnClickListener(this);
        mark.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId(); // start action based on ID

        // Get text from edit texts
        String title = dProductTitle.getText().toString();
        String description = dProductDescription.getText().toString();
        double price = Double.parseDouble(dPrice.getText().toString());

        // if title is clicked return to home page
        if (id == R.id.textViewProductDetail){
            Intent intent = new Intent(this, SavedLater.class);
            startActivity(intent);
        }
        // if editButton is clicked run the function
        else if (id == R.id.buttonEdit){
            editProduct(title, description, price);
        }
        // if markAsPurchased button is clicked run the function
        else if (id == R.id.buttonMark){
            markProduct(title, description, price);

        }
        // if shareButton is clicked run the function
        else  if (id == R.id.buttonShare){
            shareProduct(title, description, price);
        }
    }

    // function for edit product
    private void editProduct(String Title, String Description, double Price) {

        // Match the document based on email
        product.whereEqualTo("email", user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document: queryDocumentSnapshots){
                    ProductItem productItem = document.toObject(ProductItem.class);
                    String id = document.getId();
                    productItem.setId(id);

                    // if id is matched update the data
                    if (id == productItem.getId()){
                        String title = dProductTitle.getText().toString().trim();
                        String description = dProductDescription.getText().toString().trim();
                        double price = Double.parseDouble(dPrice.getText().toString().trim());

                        Map<String, Object> uProduct = new HashMap<>();
                        uProduct.put("title", title);
                        uProduct.put("description", description);
                        uProduct.put("price", price);

                        product.document(document.getId()).set(uProduct, SetOptions.merge()); // merge the data based on ID

                        Toast.makeText(ProductDetails.this, "Update successful", Toast.LENGTH_SHORT).show(); // Display success message

                        // Back to SavedLater activity to see the updates
                        Intent intent = new Intent(ProductDetails.this, SavedLater.class);
                        startActivity(intent);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetails.this, "Fail to update", Toast.LENGTH_SHORT).show(); // Display fail message
            }
        });
    }

    // function to mark as purchased
    private void markProduct(String Title, String Description, double Price) {
        String title = dProductTitle.getText().toString().trim();
        String description = dProductDescription.getText().toString().trim();
        double price = Double.parseDouble(dPrice.getText().toString().trim());

        // Match the document based on email
        product.whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document:task.getResult()){
                        ProductItem productItem = document.toObject(ProductItem.class);

                        // Get text from edit texts
                        String title = dProductTitle.getText().toString().trim();
                        String description = dProductDescription.getText().toString().trim();
                        double price = Double.parseDouble(dPrice.getText().toString().trim());

                        // Delete the data below
                        Map<String, Object> dProduct = new HashMap<>();
                        dProduct.put("title", title);
                        dProduct.put("description", description);
                        dProduct.put("price", price);

                        product.document(document.getId()).delete(); // delete based on ID

                        Map<String, Object> aProduct = new HashMap<>();
                        aProduct.put("title", title);
                        aProduct.put("description", description);
                        aProduct.put("price", price);
                        aProduct.put("email", mAuth.getCurrentUser().getEmail());

                        // add the mark as purchased product to a new collection
                        markFS.add(aProduct).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(ProductDetails.this, "Mark as purchased", Toast.LENGTH_SHORT).show(); // Display success message
                                Intent intent = new Intent(ProductDetails.this, SavedLater.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProductDetails.this, "Fail to mark as purchased", Toast.LENGTH_SHORT).show(); // Display fail message
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetails.this, "Fail to delete", Toast.LENGTH_SHORT).show(); // Display fail message
            }
        });
    }

    // function to share the product
    private void shareProduct(String title, String description, double price) {
        String message = dProductTitle.getText().toString() + "\n" +dProductDescription.getText().
                toString() + "\n£" + Double.parseDouble(dPrice.getText().toString()); // Prefill the message content
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")); // proceed to SMS
        intent.putExtra("sms_body", message); // pass the prefill message content into SMS
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }
}