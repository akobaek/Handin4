import java.text.DecimalFormat;

/**
 * The {@code Discount} class holds the information about the an items discount.
 * The limit for activating the discount and the discounted price.
 */
public class Discount {
    private String barcode;
    private int limit;
    private double price;

    /**
     * Creates a discount object.
     * @param barcode The barcode of the discount.
     * @param limit The limit for activating the discount.
     * @param price The discounted price.
     */
    public Discount(String barcode, int limit, double price) {
        this.barcode = barcode;
        this.limit = limit;
        this.price = price;
    }

    /**
     * Prints out the discount in receipt format, given a quantity of items purchased,
     * the undiscounted price of the item and a number formatter.
     * @param quantity Purchased quantity.
     * @param undiscountedItemPrice Undiscounted price of the item.
     * @param doubleFormatter Number formatter.
     */
    public void printDiscount(int quantity, double undiscountedItemPrice, DecimalFormat doubleFormatter) {
        double totDiscount = totalDiscount(quantity, undiscountedItemPrice);
        System.out.print(String.format("%-19s", "RABAT"));
        System.out.print(String.format("%20s", doubleFormatter.format(totDiscount) + "-\n"));
    }

    /**
     * Calculates the total discounted obtained given the quantity purchased and the undiscounted item price.
     * @param quantity Purchased quantity.
     * @param undiscountedItemPrice Undiscounted price of the item.
     * @return Total discount obtained.
     */
    public double totalDiscount(int quantity, double undiscountedItemPrice) {
        return quantity * (undiscountedItemPrice - price);
    }

    public int getLimit() {
        return limit;
    }
}
