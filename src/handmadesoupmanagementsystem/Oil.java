package handmadesoupmanagementsystem;

/**
 *
 * @author Chin Jia Xiong
 */
public class Oil implements Comparable<Oil>{
    
    private final int id;
    private final String nameChi;
    private final String nameEng;
    private final double NaOH;
    private final double KOH;
    private final int INSsolid;
    private final int INSliquid;
    private final double price;
    
    public Oil(int id, String nameChi, String nameEng, double NaOH, double KOH, int INSsolid, int INSliquid, double price){
        this.id = id;
        this.nameChi = nameChi;
        this.nameEng = nameEng;
        this.NaOH = NaOH;
        this.KOH = KOH;
        this.INSsolid = INSsolid;
        this.INSliquid = INSliquid;
        this.price = price;
    }
    
    public int getId(){
        return id;
    }

    public String getNameChi() {
        return nameChi;
    }

    public String getNameEng() {
        return nameEng;
    }

    public double getNaOH() {
        return NaOH;
    }

    public double getKOH() {
        return KOH;
    }

    public int getINSsolid() {
        return INSsolid;
    }
    
    public int getINSliquid() {
        return INSliquid;
    }
    
    public double getPrice() {
        return price;
    }

    @Override
    public int compareTo(Oil t) {
        return this.INSsolid - t.INSsolid;
    }
    
}
