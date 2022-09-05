package utils

import java.util.function.{BiFunction, Function => JFunction}

import scala.language.implicitConversions

object ScalaHelper {

  implicit def toJavaFunction[U, V](f: U => V): JFunction[U, V] =
    new JFunction[U, V] {
      override def apply(t: U): V = f(t)

      override def compose[T](
          before: JFunction[_ >: T, _ <: U]
      ): JFunction[T, V] = toJavaFunction(f.compose(x => before.apply(x)))

      override def andThen[W](
          after: JFunction[_ >: V, _ <: W]
      ): JFunction[U, W] = toJavaFunction(f.andThen(x => after.apply(x)))
    }

  implicit def fromJavaFunction[U, V](f: JFunction[U, V]): U => V = f.apply

  implicit def toJavaBiFunction[T, U, R](f: (T, U) => R): BiFunction[T, U, R] =
    (t: T, u: U) => f(t, u)

//	implicit def fromJavaBiFunction[T, U, R](f: BiFunction[T,U]): (T,U) => R = f.apply

}
