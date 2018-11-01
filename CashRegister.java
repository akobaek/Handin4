import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class CashRegister {
    private Map<String, Item> items;
    private Map<String, Integer> purchase;
    private BarcodeFileImporter barcodeFileImporter;
    private DecimalFormat doubleFormatter;

    public CashRegister(String priceFilename, String discountsFilename) {
        barcodeFileImporter = new BarcodeFileImporter();
        items = barcodeFileImporter.getItems(priceFilename, discountsFilename);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        doubleFormatter = new DecimalFormat("##.00", otherSymbols);

    }

    private void importPurchase(String barcodeFilename) {
        purchase = barcodeFileImporter.purchaseFileImporter(barcodeFilename);
    }

    private List<String> extractCategories() {
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

    private void printCategory(String category) {
        String categoryString = "* " + category + " *";
        int spacing = (18 + categoryString.length() / 2);
        System.out.println();
        System.out.println(String.format("%" + spacing + "s", categoryString));
    }

    private String printablePrice(double price) {
        String printablePrice;
        return doubleFormatter.format(price);
    }
    private void printSummary(double subTotal, double totalDiscount) {
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

    private int maerkeCount(double subTotal, double totalDiscount) {
        return (int) (subTotal - totalDiscount) / 50;
    }

    private List<Item> getItemsInCategory(String category) {
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
                int quantity = purchase.get(item.getBarcode());
                item.printItem(quantity, doubleFormatter);
                subTotal += item.getPrice() * quantity;
                if (item.activeDiscount(quantity)) {
                    totalDiscount += item.getDiscount().totalDiscount(quantity,item.getPrice());
                }
            }
        }
        printSummary(subTotal, totalDiscount);
    }
}
