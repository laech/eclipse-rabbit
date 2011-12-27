package rabbit.ui.internal.viewers

abstract class ForwardingTest {

  trait Forwarding {

    /** The real instance wrapped by this forwarding object.
      *
      * This instance is created with `Mockito.mock`, which can be used with
      * `Mockito.verify` to verify that this forwarding object has forwarded
      * the correct calls to the mocked instance.
      */
    val delegate: Any
  }

  /** Creates an instance for testing. */
  protected def create: Forwarding
}