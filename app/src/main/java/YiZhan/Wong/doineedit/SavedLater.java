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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SavedLater extends AppCompatActivity implements View.OnClickListener{

    // Instantiation the widgets
    private TextView pageTitle;
    private RecyclerView recyclerViewSaveLater;
    private RecyclerView.LayoutManager layoutManagerSaveLater;
    private FloatingActionButton floatingActionButtonAdd;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection("productNew");
    private SavedLaterAdapter adapter;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_later);

        // Link the widgets by findViewById
        pageTitle = findViewById(R.id.textViewTitleSavedLater);
        floatingActionButtonAdd = findViewById(R.id.floatingActionButtonAdd);

        // Set onClickListener to text view and floating action button
        pageTitle.setOnClickListener(this);
        floatingActionButtonAdd.setOnClickListener(this);

        // set up Recycler View to read the document from FireStore
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = productRef.whereEqualTo("email", mAuth.getCurrentUser().getEmail()); // Query read document based on email

        FirestoreRecyclerOptions<ProductItem> options = new FirestoreRecyclerOptions.
                Builder<ProductItem>().setQuery(query, ProductItem.class).build(); // Read and categorise the field and ProductItem class

        adapter = new SavedLaterAdapter(options); // Apply recyclerview options into the adapter

        recyclerViewSaveLater = findViewById(R.id.recyclerViewSaveLater); // Variable to recyclerView
        recyclerViewSaveLater.setHasFixedSize(true); // Set size for the recycler view
        layoutManagerSaveLater = new LinearLayoutManager(this); // Create layout manager
        recyclerViewSaveLater.setAdapter(adapter);// Set adapter for recycler view
        recyclerViewSaveLater.setLayoutManager(layoutManagerSaveLater); // Set layout manager for recycler view

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
        }).attachToRecyclerView(recyclerViewSaveLater);

        // set onItemClickListener to the adapter
        adapter.setOnItemClickListener(new SavedLaterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ProductItem productItem = documentSnapshot.toObject(ProductItem.class); // link between document in FireStore and Product Item class
                String id = documentSnapshot.getId();// Get document ID

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
        if (id == R.id.textViewTitleSavedLater){
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
        // if add button is clicked proceed to AddProduct Activity
        else if (id == R.id.floatingActionButtonAdd){
            Intent intent = new Intent(this, AddItem.class);
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
        adapter.stopListening();;
    }
}