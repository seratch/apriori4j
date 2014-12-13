package apriori4s

case class AssociationRule(
  leftHandSide: ItemSet,
  rightHandSet: ItemSet,
  confidence: Double)