package apriori4s

case class AnalysisResult(
  frequentItemSets: Map[Integer, Set[FrequentItemSet]],
  associationRules: Set[AssociationRule])