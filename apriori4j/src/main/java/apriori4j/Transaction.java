package apriori4j;

import java.util.Set;

public class Transaction {

    private final Set<String> items;

    public Transaction(Set<String> items) {
        this.items = items;
    }

    public Set<String> getItems() {
        return items;
    }

    public String toString() {
        return items.toString();
    }

}
