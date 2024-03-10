import java.io.FileOutputStream
import scala.util.Using

val currentPath: String =
  "/Users/kotoji/develop/gitlab-repos/workbook-scala/scribble/"

trait ByteEncoder[A] {
  def encode(a: A): Array[Byte]
}
object ByteEncoder {
  implicit val stringByteEncoder: ByteEncoder[String] =
    instance[String](_.getBytes)

  def instance[A](f: A => Array[Byte]): ByteEncoder[A] = new ByteEncoder[A] {
    override def encode(a: A): Array[Byte] = f(a)
  }
}

trait Channel {
  def write[A](obj: A)(implicit enc: ByteEncoder[A]): Unit
}
object FileChannel extends Channel {
  override def write[A](obj: A)(implicit enc: ByteEncoder[A]): Unit = {
    val bytes: Array[Byte] = enc.encode(obj)
    Using(new FileOutputStream(currentPath + "/fp-test")) { os =>
      os.write(bytes)
      os.flush()
    }
  }
}

FileChannel.write("hello")
