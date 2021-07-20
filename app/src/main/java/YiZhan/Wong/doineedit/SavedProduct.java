package YiZhan.Wong.doineedit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SavedProduct extends AppCompatActivity implements View.OnClickListener{

    // Instantiation variables
    private TextView savedProductTitle;
    private RecyclerView recyclerViewSavedProduct;
    private RecyclerView.LayoutManager layoutManagerSavedProduct;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference markRef = db.collection("mark");
    private SavedProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_product);

        // Link the widgets by findViewById
        savedProductTitle = findViewById(R.id.textViewTitleSavedProduct);
        savedProductTitle.setOnClickListener(this);

        // set up Recycler View to read the document from FireStore
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query= markRef.whereEqualTo("email", mAuth.getCurrentUser().getEmail()); // Query read document based on email

        FirestoreRecyclerOptions<ProductItem> options = new FirestoreRecyclerOptions.
                Builder<ProductItem>().setQuery(query, ProductItem.class).build(); // Read and categorise the field and ProductItem class

        adapter = new SavedProductAdapter(options); // Apply recyclerview options into the adapter

        recyclerViewSavedProduct = findViewById(R.id.recyclerViewSavedProduct); // Variable to recyclerView
        recyclerViewSavedProduct.setHasFixedSize(true); // Set size for the recycler view
        layoutManagerSavedProduct = new LinearLayoutManager(this); // Create layout manager
        recyclerViewSavedProduct.setAdapter(adapter);// Set adapter for recycler view
        recyclerViewSavedProduct.setLayoutManager(layoutManagerSavedProduct); // Set layout manager for recycler view

        // Swipe left and right gesture
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteProduct(viewHolder.getAdapterPosition()); // delete the item based on the position
            }
        }).attachToRecyclerView(recyclerViewSavedProduct);

        // set onItemClickListener to the adapter
        adapter.setOnItemClickListener(new SavedProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ProductItem productItem = documentSnapshot.toObject(ProductItem.class); // link between document in FireStore and Product Item class
                String id = documentSnapshot.getId(); // Get document ID

                // Pass the information to ProductDetails Activity
                Intent intent = new Intent(getApplicationContext(), ProductDetails.class);
                intent.putExtra("id", id);
                intent.putExtra("title", productItem.getTitle());
                intent.putExtra("description", productItem.getDescription());
                intent.putExtra("price", Double.parseDouble(String.valueOf(productItem.getPrice())));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId(); // start action based on ID

        // if title is clicked back to home page
        if (id == R.id.textViewTitleSavedProduct){
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        // start listen to the update of the FireStore
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        // stop listen to the update of the FireStore
        super.onStop();
        adapter.startListening();
    }
}