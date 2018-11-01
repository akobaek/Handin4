public class Discount {
    private String barcode;
    private int limit;
    private double price;

    public Discount(String barcode, int limit, double price) {
        this.barcode = barcode;
        this.limit = limit;
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getLimit() {
        return limit;

    }

    public double getPrice() {
        return price;

    }
}
