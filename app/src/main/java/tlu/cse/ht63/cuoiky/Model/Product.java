package tlu.cse.ht63.cuoiky.Model;
import com.google.firebase.firestore.DocumentSnapshot;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private String image;
    private float rating;

    public Product() {
        // Empty constructor needed for Firestore
    }

    public Product(String id, String name, String description, double price, String image, float rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public float getRating() {
        return rating;
    }

    // Convert Firestore DocumentSnapshot to Product object
    public static Product fromDocumentSnapshot(DocumentSnapshot document) {
        Product product = new Product();
        product.id = document.getId();
        product.name = document.getString("name");
        product.description = document.getString("description");
        product.price = document.getDouble("price");
        product.image = document.getString("image");
        product.rating = document.getDouble("rating").floatValue(); // Assuming "rating" is stored as a double in Firestore
        return product;
    }
}
