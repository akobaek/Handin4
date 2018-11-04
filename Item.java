import java.text.DecimalFormat;

/**
 * The {@code Item} class represents a purchasable item containing all relevant information about such an item.
 */
public class Item {
    private String barcode, category, name;
    private double price;
    private Discount discount;

    /**
     * Creates an item object.
     * @param barcode The barcode of the item.
     * @param category The category of the item.
     * @param name The name of the item.
     * @param price The price of the item.
     */
    public Item(String barcode, String category, String name, double price) {
        this.barcode = barcode;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    /**
     * Checks whether or not the item has an active discount.
     * @param quantity Quantity purchased of the item.
     * @return Whether or not the discount is active.
     */
    public boolean activeDiscount(int quantity){
        return (discount != null) && (discount.getLimit() <= quantity);
    }
    public Discount getDiscount() {
        return discount;
    }
    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
    public String getCategory() {
        return category;
    }
    public String getBarcode() {
        return barcode;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }

    /**
     * Prints out the item on in the correct receipt format given the purchased quantity and number formatter.
     * @param quantity Purchased quantity.
     * @param doubleFormatter Number formatter.
     */
    public void printItem(int quantity, DecimalFormat doubleFormatter) {
        double totalPrice = (price * quantity);
        //Separate formats if single or multiple instances of the item was purchased.
        if (quantity == 1) {
            System.out.print(String.format("%-29s", name));
            System.out.println(String.format("%8s", doubleFormatter.format(totalPrice)));
        } else {
            System.out.println(name);
            System.out.print(String.format("%-18s", "  " + quantity + " x " + doubleFormatter.format(price)));
            System.out.println(String.format("%19s", doubleFormatter.format(totalPrice)));
        }
        //Checks if the item has an active discount and prints it if so.
        if (activeDiscount(quantity)){
            discount.printDiscount(quantity, price, doubleFormatter);
        }
    }
}