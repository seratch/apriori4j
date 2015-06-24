package apriori4j;

public class FrequentItemSet {

    private final ItemSet itemSet;
    private final Double support;

    public FrequentItemSet(ItemSet itemSet, Double support) {
        this.itemSet = itemSet;
        this.support = support;
    }

    public ItemSet getItemSet() {
        return itemSet;
    }

    public Double getSupport() {
        return support;
    }

    public String toString() {
        return "FrequentItemSet(itemSet: " + itemSet + ", support: " + support + ")";
    }

}
