import java.text.DecimalFormat;
import java.util.*;


public class CashRegister {
    private Map<String, Item> items;
    private Map<String, Discount> discounts;
    private Map<String, Integer> purchase;
    private String line;
    private Map<String, String> activeItemDictionary;
    private BarcodeFileImporter barcodeFileImporter;
    private double totalDiscount = 0.00;
    private double subTotal = 0.00;

    public CashRegister(String priceFilename, String discountsFilename){
        items = new HashMap<>();
        discounts = new HashMap<>();
        purchase = new HashMap<>();
        activeItemDictionary = new HashMap<>();
        barcodeFileImporter = new BarcodeFileImporter();

        items = barcodeFileImporter.priceFileImporter(priceFilename);
        discounts = barcodeFileImporter.discountFileImporter(discountsFilename);
    }
    public void importPurchase(String barcodeFilename){
        purchase = barcodeFileImporter.purchaseFileImporter(barcodeFilename);
    }

    public List<String> extractCategories(){
        List<String> categories = new ArrayList<>();
        for (String barcode:purchase.keySet()){
            if (!categories.contains(items.get(barcode).getCategory())) {
                categories.add(items.get(barcode).getCategory());
            }
        }
        return categories;
    }
    public List<String> sortListAlphabetically(List<String> listToBeSorted){
        Collections.sort(listToBeSorted);
        return listToBeSorted;
    }
    public boolean discountActive(String barcode){
        if (discounts.get(barcode) != null) {
            if (discounts.get(barcode).getLimit() <= purchase.get(barcode)) {
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }
    public void printCategory(String category){
        int spacing = (18+category.length()/2);
        System.out.print("\n");
        System.out.print(String.format("%"+spacing+"s", "* " + category)+" *"+"\n");
    }

    public void printSingleItem(String barcode){
        Item item = items.get(barcode);
        System.out.print(String.format("%-29s",item.getName()));
        System.out.print(String.format("%9s",printablePrice(item.getPrice())+"\n"));
        if (discountActive(barcode)){printDiscount(barcode);}
        updateSubTotal(item.getPrice());
    }
    public void printMultipleItem(String barcode){
        Item item = items.get(barcode);
        int quantity = purchase.get(barcode);
        double totalPrice = (item.getPrice()*quantity);
        System.out.print(item.getName()+"\n");
        System.out.print(String.format("%-19s","  "+quantity+" x "+ printablePrice(item.getPrice())));
        System.out.print(String.format("%19s", printablePrice(totalPrice) + "\n"));
        if (discountActive(barcode)){printDiscount(barcode);}
        updateSubTotal(totalPrice);
    }

    public void updateSubTotal(double addedPrice){
        subTotal += addedPrice;
    }
    public void updateTotalDiscount(double addedDiscount){
        totalDiscount += addedDiscount;
    }

    public void printDiscount(String barcode){
        Item item = items.get(barcode);
        Discount discount = discounts.get(barcode);
        int quantity = purchase.get(barcode);
        double totDiscount = totalDiscount(item,discount,quantity);

        System.out.print(String.format("%-19s","RABAT"));
        System.out.print(String.format("%20s", printablePrice(totDiscount) +"-\n"));

        updateTotalDiscount(totDiscount);
    }

    public String printablePrice(double price){
        return (new DecimalFormat("##.00").format(price));
    }

    public double totalDiscount(Item item, Discount discount, int quantity){
        return quantity * (item.getPrice()-discount.getPrice());
    }
    public void resetTotalDiscount(){
        totalDiscount = 0.00;
    }
    public void resetSubTotal(){
        subTotal = 0.00;
    }
    public void printSummary(){
        System.out.print("\n-------------------------------------\n\n");
        System.out.print(String.format("%-18s","SUBTOT"));
        System.out.print(String.format("%19s",printablePrice(subTotal))+"\n\n");
        if (totalDiscount>0.00) {
            System.out.print(String.format("%-18s", "RABAT"));
            System.out.print(String.format("%19s", printablePrice(totalDiscount)) + "\n\n");
        }
        System.out.print(String.format("%-18s","TOTAL"));
        System.out.print(String.format("%19s",printablePrice(subTotal-totalDiscount))+"\n\n");
        System.out.print(String.format("%-18s","KØBET HAR UDLØST "+ maerkeCount()+" MÆRKER\n\n"));
        System.out.print(String.format("%-18s","MOMS UDGØR"));
        System.out.print(String.format("%19s",printablePrice((subTotal-totalDiscount)*0.2))+"\n\n");
    }
    public int maerkeCount(){
        return (int) (subTotal-totalDiscount)/50;
    }
    public List<String> getItemsInCategory(String category){
        List<String> itemsInCategory = new ArrayList<>();
        for (String barcode:purchase.keySet()){
            if (items.get(barcode).getCategory().equals(category)) {
                itemsInCategory.add(barcode);
            }
        }
        return itemsInCategory;
    }
    public List<String> translateFromBarcodeToItemName(List<String> itemsInCategory){
        List<String> listToBeReturned = new ArrayList<>();
        for (String barcode:itemsInCategory){
            listToBeReturned.add(items.get(barcode).getName());
            activeItemDictionary.put(items.get(barcode).getName(),barcode);
        }
        return listToBeReturned;
    }
    public List<String> translateFromItemNameToBarcode(List<String> itemsInCategory){
        List<String> listToBeReturned = new ArrayList<>();

        for (String itemName:itemsInCategory){
            listToBeReturned.add(activeItemDictionary.get(itemName));
        }
        return listToBeReturned;
    }

    public void printReceipt(String barcodeFilename){
        List<String> sortedCategories;
        List<String> sortedItemsInCategory;
        importPurchase(barcodeFilename);
        sortedCategories = sortListAlphabetically(extractCategories());
        for (String category:sortedCategories){
            sortedItemsInCategory = translateFromItemNameToBarcode(sortListAlphabetically(translateFromBarcodeToItemName(getItemsInCategory(category))));
            printCategory(category);
            for (String barcode:sortedItemsInCategory){
                if (purchase.get(barcode)>1){
                    printMultipleItem(barcode);
                }
                else{
                    printSingleItem(barcode);
                }
            }
        }
        printSummary();
        resetSubTotal();
        resetTotalDiscount();
    }

    public static void main(String[] args) {
        CashRegister cr = new CashRegister("prices.txt","discounts.txt");
        cr.printReceipt("bar4.txt");
    }
}
