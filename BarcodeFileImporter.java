import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BarcodeFileImporter {
    public BarcodeFileImporter(){}

    public Map<String,Item> getItems(String priceFilename, String discountFilename){
        Map<String,Item> items = priceFileImporter(priceFilename);
        Map<String,Discount> discounts = discountFileImporter(discountFilename);

        for (Item item:items.values()) {
            if (discounts.containsKey(item.getBarcode())){
                item.setDiscount(discounts.get(item.getBarcode()));
            }
        }
        return items;
        }

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
