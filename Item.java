public class Item {
    private String barcode, category, name;
    private int kr, oere;

    public Item(String barcode, String category, String name, int kr, int oere) {
        this.barcode = barcode;
        this.category = category;
        this.name = name;
        this.kr = kr;
        this.oere = oere;
    }
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public int getKr() {
        return kr;
    }

    public int getOere() {
        return oere;
    }
}
