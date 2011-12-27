package rabbit.ui.internal.viewers

import scala.math.Ordering

trait Sortable {

  var ordering: Ordering[Any]
}