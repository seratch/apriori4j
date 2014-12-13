package apriori4j;

import java.util.*;

/**
 * Apriori Algorithm.
 */
public class AprioriAlgorithm {

    private final Double minSupport;
    private final Double minConfidence;

    public AprioriAlgorithm(Double minSupport, Double minConfidence) {
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
    }

    public AnalysisResult analyze(List<Transaction> transactions) {

        Map<ItemSet, Integer> frequencies = new HashMap<ItemSet, Integer>();
        Map<Integer, Set<FrequentItemSet>> frequentItemSets = new HashMap<Integer, Set<FrequentItemSet>>();

        Set<ItemSet> oneElementItemSets = toOneElementItemSets(transactions);
        Set<FrequentItemSet> oneCItemSets = findItemSetsMinSupportSatisfied(oneElementItemSets, transactions, frequencies);

        Integer itemSetSize = 1;
        Set<FrequentItemSet> currentLItemSets = oneCItemSets;
        while (currentLItemSets.size() != 0) {
            frequentItemSets.put(itemSetSize, currentLItemSets);
            Set<ItemSet> joinedItemSets = toFixedSizeJoinedSets(toItemSets(currentLItemSets), itemSetSize + 1);
            currentLItemSets = findItemSetsMinSupportSatisfied(joinedItemSets, transactions, frequencies);
            itemSetSize++;
        }

        Set<ItemSet> foundSubSets = new HashSet<ItemSet>();
        Set<AssociationRule> associationRules = new HashSet<AssociationRule>();
        for (Map.Entry<Integer, Set<FrequentItemSet>> each : frequentItemSets.entrySet()) {
            Set<ItemSet> itemSets = toItemSets(each.getValue());
            if (itemSets.size() == 0 || itemSets.iterator().next().size() <= 1) {
                continue;
            }
            for (ItemSet itemSet : itemSets) {
                for (ItemSet subset : toAllSubSets(itemSet)) {
                    ItemSet diff = new ItemSet();
                    for (String item : itemSet) {
                        if (!subset.contains(item)) {
                            diff.add(item);
                        }
                    }
                    if (diff.size() > 0) {
                        Double itemSupport = calculateSupport(itemSet, frequencies, transactions);
                        Double subsetSupport = calculateSupport(subset, frequencies, transactions);
                        Double confidence = itemSupport / subsetSupport;
                        if (!confidence.isNaN()
                                && !confidence.isInfinite()
                                && !foundSubSets.contains(subset)
                                && confidence >= this.getMinConfidence()) {
                            foundSubSets.add(subset);
                            associationRules.add(new AssociationRule(subset, diff, confidence));
                        }
                    }
                }
            }
        }
        return new AnalysisResult(frequentItemSets, associationRules);
    }

    public Double getMinSupport() {
        return minSupport;
    }

    public Double getMinConfidence() {
        return minConfidence;
    }

    private static Set<ItemSet> toOneElementItemSets(List<Transaction> transactions) {

        Set<ItemSet> results = new HashSet<ItemSet>();
        for (Transaction transaction : transactions) {
            for (String item : transaction.getItems()) {
                results.add(ItemSet.create(item));
            }
        }
        return results;
    }

    private Set<FrequentItemSet> findItemSetsMinSupportSatisfied(
            Set<ItemSet> itemSets,
            List<Transaction> transactions,
            Map<ItemSet, Integer> frequencies) {

        Set<FrequentItemSet> filteredItemSets = new HashSet<FrequentItemSet>();
        Map<ItemSet, Integer> localFrequencies = new HashMap<ItemSet, Integer>();

        for (ItemSet itemSet : itemSets) {
            for (Transaction transaction : transactions) {
                if (itemSet.isSubSetOf(transaction)) {
                    frequencies.put(itemSet, frequencies.get(itemSet) == null ? 1 : frequencies.get(itemSet) + 1);
                    localFrequencies.put(itemSet, localFrequencies.get(itemSet) == null ? 1 : localFrequencies.get(itemSet) + 1);
                }
            }
        }
        for (Map.Entry<ItemSet, Integer> each : localFrequencies.entrySet()) {
            ItemSet itemSet = each.getKey();
            Integer localCount = each.getValue();
            Double support = localCount.doubleValue() / transactions.size();
            if (support >= this.getMinSupport()) {
                boolean alreadyAdded = false;
                for (FrequentItemSet fis : filteredItemSets) {
                    if (alreadyAdded) break;
                    else alreadyAdded = fis.getItemSet().equals(itemSet);
                }
                if (!alreadyAdded) {
                    filteredItemSets.add(new FrequentItemSet(itemSet, support));
                }
            }
        }
        return filteredItemSets;
    }

    private static Set<ItemSet> toItemSets(Set<FrequentItemSet> frequentItemSets) {

        Set<ItemSet> itemSets = new HashSet<ItemSet>();
        for (FrequentItemSet fis : frequentItemSets) {
            itemSets.add(fis.getItemSet());
        }
        return itemSets;
    }

    private static Set<ItemSet> toFixedSizeJoinedSets(Set<ItemSet> itemSets, Integer length) {
        Set<ItemSet> resultItemSets = new HashSet<ItemSet>();
        for (ItemSet itemSetA : itemSets) {
            for (ItemSet itemSetB : itemSets) {
                if (!itemSetB.containsAll(itemSetA)) {
                    ItemSet mergedItemSet = ItemSet.create(itemSetA);
                    mergedItemSet.addAll(itemSetB);
                    if (mergedItemSet.size() == length) {
                        resultItemSets.add(mergedItemSet);
                    }
                }
            }
        }
        return resultItemSets;
    }

    private static Double calculateSupport(
            ItemSet itemSet,
            Map<ItemSet, Integer> frequencies,
            List<Transaction> transactions) {

        Integer frequency = frequencies.get(itemSet);
        if (frequency != null) {
            return frequencies.get(itemSet).doubleValue() / transactions.size();
        } else {
            return 0D;
        }
    }

    private Set<ItemSet> toAllSubSets(ItemSet itemSet) {
        Set<ItemSet> sets = new HashSet<ItemSet>();
        if (itemSet.isEmpty()) {
            sets.add(new ItemSet());
            return sets;
        }
        List<String> list = new ArrayList<String>(itemSet);
        for (ItemSet set : toAllSubSets(ItemSet.create(list.subList(1, list.size())))) {
            sets.add(set);
            ItemSet newSet = ItemSet.create(list.get(0));
            newSet.addAll(set);
            sets.add(newSet);
        }
        return sets;
    }

}
