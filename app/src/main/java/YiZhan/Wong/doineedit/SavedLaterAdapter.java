package YiZhan.Wong.doineedit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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


public class SavedLaterAdapter extends FirestoreRecyclerAdapter<ProductItem, SavedLaterAdapter.SavedLaterHolder> {
    // Instantiate the widget
    private OnItemClickListener listener;

    // Constructor for SavedLater
    public SavedLaterAdapter(@NonNull FirestoreRecyclerOptions<ProductItem> options) {
        super(options);
    }

    // set the format for text views
    @Override
    protected void onBindViewHolder(@NonNull SavedLaterHolder savedLaterHolder, int i, @NonNull ProductItem productItem) {
        savedLaterHolder.productTitle.setText(productItem.getTitle());
        savedLaterHolder.productDescription.setText(productItem.getDescription());
        savedLaterHolder.price.setText(String.format("£ " + productItem.getPrice()));
    }

    @NonNull
    @Override
    public SavedLaterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get card view design to apply it to the recycler view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new SavedLaterHolder(v);
    }

    // Delete product based on position
    public void deleteProduct(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class SavedLaterHolder extends RecyclerView.ViewHolder{
        // Instantiate the widgets
        public TextView productTitle;
        public TextView productDescription;
        public TextView price;

        public SavedLaterHolder(@NonNull View itemView) {
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
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
