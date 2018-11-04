import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code BarcideFileImporter} imports different types of files containing barcodes in comma separated files.
 */
public class BarcodeFileImporter {
    public BarcodeFileImporter(){}

    /**
     * Returns a map of barcodes to items containing discounts, given a file names of price- and discount files.
     * @param priceFilename Name of comma separated barcode file containing barcode, category, name, price.
     * @param discountFilename Name of comma separated barcode file containing barcode, limit, price.
     * @return Map of barcodes to items containing discounts available.
     */
    public Map<String,Item> getItems(String priceFilename, String discountFilename){
        //Imports files.
        Map<String,Item> items = priceFileImporter(priceFilename);
        Map<String,Discount> discounts = discountFileImporter(discountFilename);
        //Adds the imported discounts to the imported items.
        for (Item item:items.values()) {
            if (discounts.containsKey(item.getBarcode())){
                item.setDiscount(discounts.get(item.getBarcode()));
            }
        }
        return items;
        }

    /**
     * Imports a map of barcodes to items given a file name of a price file.
     * @param priceFilename Name of comma separated barcode file containing barcode, category, name, price.
     * @return Map of barcodes to items available.
     */
    private Map<String, Item> priceFileImporter(String priceFilename) {
        String line;
        Map<String, Item> items = new HashMap<>();
        try {
            FileReader priceFileReader = new FileReader(priceFilename);
            BufferedReader priceBufferedReader = new BufferedReader(priceFileReader);

            while ((line = priceBufferedReader.readLine()) != null) {
                String[] itemInput = line.split(",");
                String stringToBeParsed = itemInput[3] + "." + itemInput[4];
                double doubleToBeParsed = Double.parseDouble(stringToBeParsed);
                Item itemToAdd = new Item(itemInput[0], itemInput[1], itemInput[2], doubleToBeParsed);
                items.put(itemInput[0], itemToAdd);
            }
            priceBufferedReader.close();

        } catch (
                FileNotFoundException e) {
            System.out.println("Unable to find at the file named " + priceFilename);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Imports a map of barcodes to discounts given a file name of a discount file.
     * @param discountsFilename Name of comma separated barcode file containing barcode, limit, price.
     * @return Map of barcodes to discounts available.
     */
    private Map<String, Discount> discountFileImporter(String discountsFilename) {
        String line;
        Map<String, Discount> discounts = new HashMap<>();
        try {

            FileReader discountFileReader = new FileReader(discountsFilename);
            BufferedReader discountBufferedReader = new BufferedReader(discountFileReader);

            while ((line = discountBufferedReader.readLine()) != null) {
                String[] discountInput = line.split(",");
                String discountStringToBeParsed = discountInput[2] + "." + discountInput[3];
                double discountDoubleToBeParsed = Double.parseDouble(discountStringToBeParsed);

                Discount discountToAdd = new Discount(discountInput[0], Integer.parseInt(discountInput[1]), discountDoubleToBeParsed);
                discounts.put(discountInput[0], discountToAdd);
            }
            discountBufferedReader.close();
        } catch (
                FileNotFoundException e) {
            System.out.println("Unable to find at the file named " + discountsFilename);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    /**
     * Imports a map of barcodes to quantities given a file name of a purchase file.
     * @param barcodeFilename A file containing barcodes.
     * @return Map of barcodes to quantities.
     */
    public Map<String,Integer> purchaseFileImporter(String barcodeFilename) {
        String line;
        Map<String,Integer> purchase = new HashMap<>();
        try {
            FileReader barcodeFileReader = new FileReader(barcodeFilename);
            BufferedReader barcodeBufferedReader = new BufferedReader(barcodeFileReader);

            while ((line = barcodeBufferedReader.readLine()) != null) {
                if (purchase.get(line) != null) {
                    purchase.put(line, purchase.get(line) + 1);
                } else {
                    purchase.put(line, 1);
                }
            }
            barcodeBufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to find the file named " + barcodeFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return purchase;
    }
}
