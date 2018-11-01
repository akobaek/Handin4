import java.text.DecimalFormat;

public class Item {
    private String barcode, category, name;
    private double price;
    private Discount discount;

    public Item(String barcode, String category, String name, double price) {
        this.barcode = barcode;
        this.category = category;
        this.name = name;
        this.price = price;
    }
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
    public void printItem(int quantity, DecimalFormat doubleFormatter) {
        double totalPrice = (price * quantity);
        if (quantity == 1) {
            System.out.print(String.format("%-29s", name));
            System.out.println(String.format("%8s", doubleFormatter.format(totalPrice)));
        } else {
            System.out.println(name);
            System.out.print(String.format("%-18s", "  " + quantity + " x " + doubleFormatter.format(price)));
            System.out.println(String.format("%19s", doubleFormatter.format(totalPrice)));
        }
        if (activeDiscount(quantity)){
            discount.printDiscount(quantity, price, doubleFormatter);
        }
    }


}
