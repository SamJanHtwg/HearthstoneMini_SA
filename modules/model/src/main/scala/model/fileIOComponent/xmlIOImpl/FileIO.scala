package model
package fileIOComponent.xmlIOImpl

import com.google.inject.Guice
import com.google.inject.name.Names
import fieldComponent.FieldInterface
import net.codingwell.scalaguice.InjectorExtensions.*

import fileIOComponent.FileIOInterface
import fieldComponent.fieldImpl.{Field}

import java.io.{File, PrintWriter}
import scala.xml.XML.loadFile
import scala.xml.{NodeSeq, PrettyPrinter}
import scala.util.Try

class FileIO extends FileIOInterface {
  override def load: Try[Field] =
    Try {
      val field = loadFile("field.xml")
      Field.fromXml(field)
    }

  override def save(field: FieldInterface): Unit = {
    val pw = new PrintWriter(new File("field.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(field.toXML)
    pw.write(xml)
    pw.close()
  }
}
