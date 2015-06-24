package apriori4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;

/**
 * Apriori Algorithm.
 */
public class AprioriAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(AprioriAlgorithm.class);

    private double minSupport = 0.15D;
    private double minConfidence = 0.80D;
    private int maxItemSetSize = 5;
    private boolean isQuickRun = true;
    private int maxJoinedSetsSizeWhenQuickRun = 1000;
    private int timeoutMillis = 60000; // 1 minute

    public AprioriAlgorithm(Double minSupport, Double minConfidence) {
        setMinSupport(minSupport);
        setMinConfidence(minConfidence);
    }

    private static long currentExecutionTime(long startedAt) {
        return System.currentTimeMillis() - startedAt;
    }

    private void failIfTimeout(long startedAt) throws AprioriTimeoutException {
        if (currentExecutionTime(startedAt) > getTimeoutMillis()) {
            throw new AprioriTimeoutException(getTimeoutMillis());
        }
    }

    public AnalysisResult analyze(List<Transaction> transactions) throws AprioriTimeoutException {
        long startedAt = System.currentTimeMillis();

        Map<ItemSet, Integer> frequencies = new HashMap<ItemSet, Integer>();
        Map<Integer, Set<FrequentItemSet>> frequentItemSets = new HashMap<Integer, Set<FrequentItemSet>>();

        Set<ItemSet> oneElementItemSets = toOneElementItemSets(transactions);
        Set<FrequentItemSet> oneCItemSets = findItemSetsMinSupportSatisfied(oneElementItemSets, transactions, frequencies);
        failIfTimeout(startedAt);

        Integer itemSetSize = 1;
        Set<FrequentItemSet> currentLItemSets = oneCItemSets;
        while (currentLItemSets.size() != 0) {
            frequentItemSets.put(itemSetSize, currentLItemSets);
            Set<ItemSet> itemSets = toItemSets(currentLItemSets);
            Set<ItemSet> joinedItemSets = toFixedSizeJoinedSets(itemSets, itemSetSize + 1);
            failIfTimeout(startedAt);
            if (isQuickRun && joinedItemSets.size() >= maxJoinedSetsSizeWhenQuickRun) {
                final Set<ItemSet> reducedItemSets = new HashSet<ItemSet>();
                SecureRandom random = new SecureRandom();
                for (ItemSet itemSet : joinedItemSets) {
                    if (reducedItemSets.size() >= maxJoinedSetsSizeWhenQuickRun) {
                        break;
                    }
                    if (random.nextInt(2) == 0) {
                        reducedItemSets.add(itemSet);
                    }
                }
                joinedItemSets = reducedItemSets;
            }
            currentLItemSets = findItemSetsMinSupportSatisfied(joinedItemSets, transactions, frequencies);
            failIfTimeout(startedAt);

            if (logger.isDebugEnabled()) {
                logger.debug("Calculating currentLItemSets " +
                        "(itemSetSize: " + itemSetSize +
                        ", joinedItemSets: " + joinedItemSets.size() +
                        ", currentLItemSets: " + currentLItemSets.size() +
                        ")");
            }
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

    private Set<ItemSet> toFixedSizeJoinedSets(Set<ItemSet> itemSets, Integer length) {
        Set<ItemSet> resultItemSets = new HashSet<ItemSet>();
        Set<ItemSet> flattenItemSets = new HashSet<ItemSet>();
        for (ItemSet itemSet : itemSets) {
            if (itemSet.size() >= maxItemSetSize) {
                for (ItemSet newItemSet : itemSet.split(maxItemSetSize)) {
                    flattenItemSets.add(newItemSet);
                }
            } else {
                flattenItemSets.add(itemSet);
            }
        }
        for (ItemSet itemSetA : flattenItemSets) {
            for (ItemSet itemSetB : flattenItemSets) {
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

    private static double calculateSupport(
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

    public double getMinSupport() {
        return minSupport;
    }

    public void setMinSupport(double minSupport) {
        this.minSupport = minSupport;
    }

    public double getMinConfidence() {
        return minConfidence;
    }

    public void setMinConfidence(double minConfidence) {
        this.minConfidence = minConfidence;
    }

    public int getMaxItemSetSize() {
        return maxItemSetSize;
    }

    public void setMaxItemSetSize(int maxItemSetSize) {
        this.maxItemSetSize = maxItemSetSize;
    }

    public boolean isQuickRun() {
        return isQuickRun;
    }

    public void setIsQuickRun(boolean isQuickRun) {
        this.isQuickRun = isQuickRun;
    }

    public int getMaxJoinedSetsSizeWhenQuickRun() {
        return maxJoinedSetsSizeWhenQuickRun;
    }

    public void setMaxJoinedSetsSizeWhenQuickRun(int maxJoinedSetsSizeWhenQuickRun) {
        this.maxJoinedSetsSizeWhenQuickRun = maxJoinedSetsSizeWhenQuickRun;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

}
