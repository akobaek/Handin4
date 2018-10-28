public class Item {
    private String barcode, category, name;
    private double price;

    public Item(String barcode, String category, String name, double price) {
        this.barcode = barcode;
        this.category = category;
        this.name = name;
        this.price = price;

    }
    public String getCategory() {
        return category;
    }

    public String getBarcode() {return barcode;}

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
