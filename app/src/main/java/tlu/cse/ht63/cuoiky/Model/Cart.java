package tlu.cse.ht63.cuoiky.Model;

public class Cart {
    private String productId;
    private int quantity;

    public Cart() {
        // Empty constructor needed for Firestore serialization
    }

    public Cart(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
