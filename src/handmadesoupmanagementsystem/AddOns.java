package handmadesoupmanagementsystem;

/**
 *
 * @author Chin Jia Xiong
 */
public class AddOns {

    private final String name;
    private final double finalAmount;
    private final double price;
    private final double originalAmount;
    
    public AddOns(String name, double finalAmount, double price, double originalAmount) {
        this.name = name;
        this.finalAmount = finalAmount;
        this.price = price;
        this.originalAmount = originalAmount;
    }

    public String getName() {
        return name;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public double getPrice() {
        return price;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }
    
    
}
