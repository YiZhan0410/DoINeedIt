package YiZhan.Wong.doineedit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SavedProductAdapter extends FirestoreRecyclerAdapter<ProductItem, SavedProductAdapter.SavedProductHolder> {
    // Instantiate the widget
    private OnItemClickListener listener;

    // Constructor for savedProduct
    public SavedProductAdapter(@NonNull FirestoreRecyclerOptions<ProductItem> options) {
        super(options);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedProductHolder savedProductHolder, int i, @NonNull ProductItem productItem) {

        // set the format for text views
        savedProductHolder.productTitle.setText(productItem.getTitle());
        savedProductHolder.productDescription.setText(productItem.getDescription());
        savedProductHolder.price.setText(String.format("£ "+ productItem.getPrice()));
    }

    @NonNull
    @Override
    public SavedProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get card view design to apply it to the recycler view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent,false);
        return new SavedProductHolder(v);
    }

    // Delete product based on position
    public void deleteProduct(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class SavedProductHolder extends RecyclerView.ViewHolder{
        // Instantiate the widgets
        public TextView productTitle;
        public TextView productDescription;
        public TextView price;

        public SavedProductHolder(@NonNull View itemView){
            super(itemView);
            // Link the widgets by findViewById
            productTitle = itemView.findViewById(R.id.textViewProductTitle);
            productDescription = itemView.findViewById(R.id.textViewProductDescription);
            price = itemView.findViewById(R.id.textViewPrice);

            // set onClickListener to the itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    // interface for onItemClick Listener
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    // function for onItemClick Listener
    public void setOnItemClickListener(SavedProductAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
