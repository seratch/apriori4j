package apriori4j;

public class AprioriTimeoutException extends Exception {

    public AprioriTimeoutException(long timeoutMillis) {
        super("The analysis didn't finish within " + timeoutMillis + " milliseconds.");
    }

    public AprioriTimeoutException(String message) {
        super(message);
    }

}
