import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * The {@code CashRegister} class represents a cash register, scanning a item and discount file to establish
 * a foundation to print receipts for purchase files.
 *
 */
public class CashRegister {
    private Map<String, Item> items;
    private Map<String, Integer> purchase;
    private BarcodeFileImporter barcodeFileImporter;
    private DecimalFormat doubleFormatter;

    public CashRegister(String priceFilename, String discountsFilename) {
        //Import files.
        barcodeFileImporter = new BarcodeFileImporter();
        items = barcodeFileImporter.getItems(priceFilename, discountsFilename);
        //Set number formatting according to receipt specifications.
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        doubleFormatter = new DecimalFormat("##.00", otherSymbols);
    }

    /**
     * Import a list of purchased items and quantities given a filename containing barcodes.
     * @param barcodeFilename Name of barcode file.
     */
    private void importPurchase(String barcodeFilename) {
        purchase = barcodeFileImporter.purchaseFileImporter(barcodeFilename);
    }

    /**
     * Extracts a list of unique item categories contained in a list of barcodes.
     * @return A list of unique item categories.
     */
    private List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (String barcode : purchase.keySet()) {
            if (!categories.contains(items.get(barcode).getCategory())) {
                categories.add(items.get(barcode).getCategory());
            }
        }
        return categories;
    }

    /**
     * Sort a list alphabetically given a list of strings.
     * @param listToBeSorted List of strings.
     * @return Sorted list of strings.
     */
    private List<String> sortListAlphabetically(List<String> listToBeSorted) {
        Collections.sort(listToBeSorted);
        return listToBeSorted;
    }

    /**
     * Prints out a category header with correct indentation, given a category.
     * @param category Category to be printed.
     */
    private void printCategory(String category) {
        String categoryString = "* " + category + " *";
        int spacing = (18 + categoryString.length() / 2);
        System.out.println();
        System.out.println(String.format("%" + spacing + "s", categoryString));
    }

    /**
     * Converts a price to a printable string, with two digits and a comma separator.
     * @param price Price to be printed.
     * @return String to be printed.
     */
    private String printablePrice(double price) {
        return doubleFormatter.format(price);
    }

    /**
     * Prints out a receipt summary, containing, subtotal, total discount, total and how many coupons gained from the purchase, given a subtotal and a total discount.
     * @param subTotal Subtotal of all purchased items, excl. discounts.
     * @param totalDiscount Total discount obtained from purchase.
     */
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

    /**
     * Calculates the number of coupons gained given a subtotal and discount of the purchase.
     * @param subTotal Subtotal of all purchased items, excl. discounts.
     * @param totalDiscount Total discount obtained from purchase.
     * @return Number of coupons gained from purchase.
     */
    private int maerkeCount(double subTotal, double totalDiscount) {
        return (int) (subTotal - totalDiscount) / 50;
    }

    /**
     * Extracts items from a purchase within a given category.
     * @param category Category to check items within.
     * @return List of items within given category.
     */
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

    /**
     * Prints out a receipt of all items purchased and their discounts given a filename containing barcodes.
     * @param barcodeFilename Name of barcode file.
     */
    public void printReceipt(String barcodeFilename) {
        //Import barcode file.
        importPurchase(barcodeFilename);
        List<String> sortedCategories;
        sortedCategories = sortListAlphabetically(getCategories());
        double subTotal = 0;
        double totalDiscount = 0;
        //Print out receipt by category, alphabetically.
        for (String category : sortedCategories) {
            List<Item> currentItems = getItemsInCategory(category);
            Collections.sort(currentItems, (Item i1, Item i2) -> i1.getName().compareTo(i2.getName()));
            printCategory(category);
            for (Item item : currentItems) {
                int quantity = purchase.get(item.getBarcode());
                item.printItem(quantity, doubleFormatter);
                //Update subtotal.
                subTotal += item.getPrice() * quantity;
                //Update discount if relevant.
                if (item.activeDiscount(quantity)) {
                    totalDiscount += item.getDiscount().totalDiscount(quantity,item.getPrice());
                }
            }
        }
        printSummary(subTotal, totalDiscount);
    }
}
