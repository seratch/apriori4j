package apriori4j;

public class AssociationRule {

    private final ItemSet leftHandSide;
    private final ItemSet rightHandSide;
    private final Double confidence;

    public AssociationRule(ItemSet leftHandSide, ItemSet rightHandSide, Double confidence) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.confidence = confidence;
    }

    public ItemSet getLeftHandSide() {
        return leftHandSide;
    }

    public ItemSet getRightHandSide() {
        return rightHandSide;
    }

    public Double getConfidence() {
        return confidence;
    }

    public String toString() {
        return "AssociationRule(" +
                "leftHandSide: " + leftHandSide +
                ",rightHandSide: " + rightHandSide +
                ",confidence: " + confidence +
                ")";
    }

}
