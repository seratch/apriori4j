package apriori4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ItemSet extends HashSet<String> {

    public static ItemSet create(Collection<String> items) {
        ItemSet itemSet = new ItemSet();
        for (String item : items) {
            itemSet.add(item);
        }
        return itemSet;
    }

    public static ItemSet create(String... items) {
        ItemSet itemSet = new ItemSet();
        for (String item : items) {
            itemSet.add(item);
        }
        return itemSet;
    }

    public boolean isSubSetOf(Transaction transaction) {
        return transaction.getItems().containsAll(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            return this.toString().equals(o.toString());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String[] values = this.toArray(new String[0]);
        Arrays.sort(values);
        StringBuilder sb = new StringBuilder();
        sb.append("ItemSet(");
        for (String value : values) {
            sb.append(value).append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
