import java.text.DecimalFormat;

public class Discount {
    private String barcode;
    private int limit;
    private double price;

    public Discount(String barcode, int limit, double price) {
        this.barcode = barcode;
        this.limit = limit;
        this.price = price;
    }
    public void printDiscount(int quantity, double itemPrice, DecimalFormat doubleFormatter) {
        double totDiscount = totalDiscount(quantity, itemPrice);
        System.out.print(String.format("%-19s", "RABAT"));
        System.out.print(String.format("%20s", doubleFormatter.format(totDiscount) + "-\n"));
    }
    public double totalDiscount(int quantity, double itemPrice) {
        return quantity * (itemPrice - price);
    }

    public int getLimit() {
        return limit;
    }
}
