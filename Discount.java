public class Discount {
    private String barcode;
    private int limit, kr, oere;

    public Discount(String barcode, int limit, int kr, int oere) {
        this.barcode = barcode;
        this.limit = limit;
        this.kr = kr;
        this.oere = oere;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getLimit() {
        return limit;
    }

    public int getKr() {
        return kr;
    }

    public int getOere() {
        return oere;
    }
}
