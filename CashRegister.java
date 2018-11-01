import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class CashRegister {
    private Map<String, Item> items;
    private Map<String, Discount> discounts;
    private Map<String, Integer> purchase;
    private String line;
    private BarcodeFileImporter barcodeFileImporter;
    private double totalDiscount;
    private double subTotal;

    public CashRegister(String priceFilename, String discountsFilename) {
        barcodeFileImporter = new BarcodeFileImporter();
        items = barcodeFileImporter.priceFileImporter(priceFilename);
        discounts = barcodeFileImporter.discountFileImporter(discountsFilename);
    }

    public void importPurchase(String barcodeFilename) {
        purchase = barcodeFileImporter.purchaseFileImporter(barcodeFilename);
    }

    public List<String> extractCategories() {
        List<String> categories = new ArrayList<>();
        for (String barcode : purchase.keySet()) {
            if (!categories.contains(items.get(barcode).getCategory())) {
                categories.add(items.get(barcode).getCategory());
            }
        }
        return categories;
    }

    private List<String> sortListAlphabetically(List<String> listToBeSorted) {
        Collections.sort(listToBeSorted);
        return listToBeSorted;
    }

    public boolean discountActive(String barcode) {
        return (discounts.get(barcode) != null) && (discounts.get(barcode).getLimit() <= purchase.get(barcode));
    }

    public void printCategory(String category) {
        String categoryString = "* " + category + " *";
        int spacing = (18 + categoryString.length() / 2);
        System.out.println();
        System.out.println(String.format("%" + spacing + "s", categoryString));
    }

    public void printItem(Item item, int quantity) {
        double totalPrice = (item.getPrice() * quantity);
        if (quantity == 1) {
            System.out.print(String.format("%-29s", item.getName()));
            System.out.println(String.format("%8s", printablePrice(totalPrice)));
        } else {
            System.out.println(item.getName());
            System.out.print(String.format("%-18s", "  " + quantity + " x " + printablePrice(item.getPrice())));
            System.out.println(String.format("%19s", printablePrice(totalPrice)));
        }
    }

    public void updateSubTotal(double addedPrice) {
        subTotal += addedPrice;
    }

    public void updateTotalDiscount(double addedDiscount) {
        totalDiscount += addedDiscount;
    }

    public double totalDiscount(Item item, Discount discount, int quantity) {
        return quantity * (item.getPrice() - discount.getPrice());
    }

    public double printDiscount(String barcode, int quantity) {
        Item item = items.get(barcode);
        Discount discount = discounts.get(barcode);
        double totDiscount = totalDiscount(item, discount, quantity);

        System.out.print(String.format("%-19s", "RABAT"));
        System.out.print(String.format("%20s", printablePrice(totDiscount) + "-\n"));

        return totDiscount;
    }

    public String printablePrice(double price) {
        String printablePrice;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        return new DecimalFormat("##.00", otherSymbols).format(price);
    }

    public void resetTotalDiscount() {
        totalDiscount = 0.00;
    }

    public void resetSubTotal() {
        subTotal = 0.00;
    }

    public void printSummary(double subTotal, double totalDiscount) {
        System.out.print("\n-------------------------------------\n\n");
        System.out.print(String.format("%-18s", "SUBTOT"));
        System.out.print(String.format("%19s", printablePrice(subTotal)) + "\n\n");
        if (totalDiscount > 0.00) {
            System.out.print(String.format("%-18s", "RABAT"));
            System.out.print(String.format("%19s", printablePrice(totalDiscount)) + "\n\n");
        }
        System.out.print(String.format("%-18s", "TOTAL"));
        System.out.print(String.format("%19s", printablePrice(subTotal - totalDiscount)) + "\n\n");
        System.out.print(String.format("%-18s", "KØBET HAR UDLØST " + maerkeCount(subTotal, totalDiscount) + " MÆRKER\n\n"));
        System.out.print(String.format("%-18s", "MOMS UDGØR"));
        System.out.print(String.format("%19s", printablePrice((subTotal - totalDiscount) * 0.2)) + "\n\n");
    }

    public int maerkeCount(double subTotal, double totalDiscount) {
        return (int) (subTotal - totalDiscount) / 50;
    }

    public List<Item> getItemsInCategory(String category) {
        List<Item> itemsInCategory = new ArrayList<>();
        for (String barcode : purchase.keySet()) {
            Item item = items.get(barcode);
            if (item.getCategory().equals(category)) {
                itemsInCategory.add(item);
            }
        }
        return itemsInCategory;
    }

    public void printReceipt(String barcodeFilename) {
        List<String> sortedCategories;
        List<String> sortedItemsInCategory;
        importPurchase(barcodeFilename);
        sortedCategories = sortListAlphabetically(extractCategories());
        double subTotal = 0;
        double totalDiscount = 0;
        for (String category : sortedCategories) {
            List<Item> currentItems = getItemsInCategory(category);
            Collections.sort(currentItems, (Item i1, Item i2) -> i1.getName().compareTo(i2.getName()));
            printCategory(category);
            for (Item item : currentItems) {
                String barcode = item.getBarcode();
                int quantity = purchase.get(barcode);
                printItem(item, quantity);
                subTotal += item.getPrice() * quantity;
                if (discountActive(barcode)) {
                    Discount discount = discounts.get(barcode);
                    printDiscount(barcode, quantity);
                    totalDiscount += totalDiscount(item, discount, quantity);
                }

            }
        }

        printSummary(subTotal, totalDiscount);
        resetSubTotal();
        resetTotalDiscount();
    }
}
