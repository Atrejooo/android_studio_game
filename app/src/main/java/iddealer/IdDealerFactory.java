package iddealer;

public class IdDealerFactory {
    public static IdDealer createIdDealer(){
        return new IdDealerImpl();
    }
}
