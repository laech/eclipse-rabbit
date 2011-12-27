package rabbit.ui.internal.viewers

final class ReversedOrdering(private val ordering: Ordering[Any])
  extends Ordering[Any] {

  override def compare(a: Any, b: Any) = ordering.compare(a, b) * -1
}