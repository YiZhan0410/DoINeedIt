package YiZhan.Wong.doineedit;

import com.google.firebase.firestore.Exclude;

// Class for Product Item
public class ProductItem {
    // Instantiate variable for productID
    @Exclude private String id;

    // Instantiate variable for productTitle, productDescription and price
    private String title;
    private String description;
    private double price;

    // Empty constructor
    public ProductItem(){

    }

    // Constructor for ProductItem
    public ProductItem(String Title, String Description, double Price)
    {
        // Declare parameter variables to the class
        title = Title;
        description = Description;
        price = Price;
    }

    // Get and set method for ID
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    // Method to get Product Title
    public String getTitle() { return title; }

    // Method to get Product Description
    public String getDescription() { return description; }

    // Method to get Price
    public double getPrice() { return price; }
}
