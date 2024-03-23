package hearthstoneMini
package model.fileIOComponent.xmlIOImpl

import com.google.inject.Guice
import com.google.inject.name.Names
import model.fieldComponent.FieldInterface
import net.codingwell.scalaguice.InjectorExtensions.*

import model.fileIOComponent.FileIOInterface
import model.fieldComponent.fieldImpl.{Field, FieldObject}

import java.io.{File, PrintWriter}
import scala.xml.XML.loadFile
import scala.xml.{NodeSeq, PrettyPrinter}

class FileIO extends FileIOInterface {
  override def load: Field = {
    val field = loadFile( "field.xml" )
    FieldObject.fromXML( field )
  }

  override def save(field: FieldInterface): Unit = {
    val pw = new PrintWriter( new File( "field.xml" ) )
    val prettyPrinter = new PrettyPrinter( 120, 4 )
    val xml = prettyPrinter.format( field.toXML )
    pw.write( xml )
    pw.close()
  }
}
