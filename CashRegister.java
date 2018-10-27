import java.io.*;
import java.util.*;


public class CashRegister {
    private Map<String, Item> items;
    private Map<String, Discount> discounts;
    private Map<String,Integer> purchase;
    private String line;

    public CashRegister(String priceFilename, String discountsFilename){
        items = new HashMap<>();
        discounts = new HashMap<>();
        purchase = new HashMap<>();
        try {
            FileReader priceFileReader = new FileReader(priceFilename);
            BufferedReader priceBufferedReader = new BufferedReader(priceFileReader);

            while((line = priceBufferedReader.readLine()) != null) {
                String[] itemInput = line.split(",");
                Item itemToAdd = new Item(itemInput[0], itemInput[1], itemInput[2], Integer.parseInt(itemInput[3]), Integer.parseInt(itemInput[4]));
                items.put(itemInput[0],itemToAdd);
            }
            priceBufferedReader.close();

            FileReader discountFileReader = new FileReader(discountsFilename);
            BufferedReader discountBufferedReader = new BufferedReader(discountFileReader);

            while((line = discountBufferedReader.readLine()) != null) {
                String[] discountInput = line.split(",");
                Discount discountToAdd = new Discount(discountInput[0], Integer.parseInt(discountInput[1]), Integer.parseInt(discountInput[2]), Integer.parseInt(discountInput[3]));
                discounts.put(discountInput[0],discountToAdd);
            }
            discountBufferedReader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Unable to find at least one of the files named "+discountsFilename+" or "+priceFilename);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
    public void importPurchase(String barcodeFilename){
        try{
            FileReader barcodeFileReader = new FileReader(barcodeFilename);
            BufferedReader barcodeBufferedReader = new BufferedReader(barcodeFileReader);

            while((line = barcodeBufferedReader.readLine()) != null) {
                if (purchase.get(line) != null){
                    purchase.put(line,purchase.get(line)+1);
                }
                else{
                    purchase.put(line,1);
                }
            }
            barcodeBufferedReader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Unable to find the file named "+barcodeFilename);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<String> extractCategories(){
        List<String> categories = new ArrayList<>();
        for (String barcode:purchase.keySet()){
                categories.add(items.get(barcode).getCategory());
        }
        return categories;
    }
    public List<String> sortListAlphabetically(List<String> listToBeSorted){
        Collections.sort(listToBeSorted);
        return listToBeSorted;
    }
    public Set<String> checkDiscounts(){
        Set<String> discountedPurchases = new HashSet<>();
        for (String barcode:purchase.keySet()){
            if (discounts.get(barcode) != null){
                discountedPurchases.add(barcode);
            }
        }
        return discountedPurchases;
    }

    public void printCategory(String category){
        int spacing = (18+category.length()/2);
        System.out.print(String.format("%"+spacing+"s", "* " + category)+" *"+"\n");
    }

    public void printRegularItem(Item item){
        System.out.print(String.format("%-18",item.getName()));
        System.out.print(String.format("%19s",item.getKr() + ","+ item.getOere() + "\n"));
    }
    public void printDiscountedItem(Item item, Discount discount, int quantity){
        int totalPriceKr = quantity*discount.getKr()+(quantity*discount.getOere())/100;
        System.out.print(String.format("%-18",item.getName())+"\n");
        System.out.print(String.format("%18","  "+quantity+" x "+discount.getKr()+","+discount.getOere()));
        System.out.print(String.format("%19s",item.getKr() + ","+ item.getOere() + "\n"));
    }


    public void printReceipt(String barcodeFilename){
        List<String> sortedCategories;
        Set<String> activeDiscounts;
        importPurchase(barcodeFilename);

        sortedCategories = sortListAlphabetically(extractCategories());




        System.out.print(String.format("%-18s",items.get("0 580524 463272").getName()));
        System.out.print(String.format("%19s",items.get("0 580524 463272").getKr()+","+items.get("1 173648 738266").getOere())+"\n");
        System.out.print(String.format("%-18s",items.get("1 173648 738266").getName()));
        System.out.print(String.format("%19s",items.get("1 173648 738266").getKr()+","+items.get("1 173648 738266").getOere())+"-");

        // sortedCategories = sortListAlphabetically(extractCategories());

    }

    public static void main(String[] args) {
        CashRegister cr = new CashRegister("prices.txt","discounts.txt");
        cr.printReceipt("bar0.txt");
    }
}
