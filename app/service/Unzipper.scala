package service

import java.io._

/**
  * Created by quentin on 07/06/17.
  */
trait Unzipper {


  import java.io.{BufferedInputStream, BufferedOutputStream, FileInputStream, FileOutputStream, IOException}

  import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}
  import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

  //TODO copy past from stackoverflow, to be reviewed
  @throws[IOException]
  def untarTo(source: File, destination: String): Unit = {
    //if (!(FilenameUtils.getExtension(source.getName.toLowerCase) == "gz")) throw new IllegalArgumentException("Expecting tar.gz file: " + source.getAbsolutePath)
    if (!new File(destination).isDirectory) throw new IllegalArgumentException("Destination should be a folder: " + destination)
    /** create a TarArchiveInputStream object. **/
    val fin = new FileInputStream(source)
    val in = new BufferedInputStream(fin)
    val gzIn = new GzipCompressorInputStream(in)
    val tarIn = new TarArchiveInputStream(gzIn)
    try {
      var entry = null
      /** Read the tar entries using the getNextEntry method **/
      val BUFFER = 2048

      def iterateEntry(tarIn: TarArchiveInputStream, entry: TarArchiveEntry): Unit = {
        if (entry != null) {
          /** If the entry is a directory, create the directory. **/
          if (entry.isDirectory) {
            val f = new File(destination, entry.getName)
            f.mkdirs
          }
          else {
            var count = 0
            val data = new Array[Byte](BUFFER)
            val fos = new FileOutputStream(new File(destination, entry.getName))
            val dest = new BufferedOutputStream(fos, BUFFER)
            try {
              def read(tarIn: TarArchiveInputStream): Unit = {
                val count = tarIn.read(data, 0, BUFFER)
                if (count != -1) {
                  dest.write(data, 0, count)
                  read(tarIn)
                }
              }
              read(tarIn)
            }
            finally {
              dest.close()
            }
          }
          iterateEntry(tarIn, tarIn.getNextEntry.asInstanceOf[TarArchiveEntry])
        }
      }

      iterateEntry(tarIn, tarIn.getNextEntry.asInstanceOf[TarArchiveEntry])
    }

    finally {
      tarIn.close()
    }
  }
//
//  //TODO copy past from stackoverflow, to be reviewed
//  @throws[IOException]
//  def unzipTo(sourceZipFile: File, destinationDirectory: String): Unit = {
//    val BUFFER = 2048
//    val zipFiles = ListBuffer[String]()
//    val unzipDestinationDirectory = new File(destinationDirectory)
//    unzipDestinationDirectory.mkdir
//    // Open Zip file for reading
//    val zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ)
//    try {
//      // Create an enumeration of the entries in the zip file
//      val zipFileEntries = zipFile.entries
//      // Process each entry
//      while ( {
//        zipFileEntries.hasMoreElements
//      }) {
//        // grab a zip file entry
//        val entry = zipFileEntries.nextElement.asInstanceOf[ZipEntry]
//        val currentEntry = entry.getName
//        var destFile = new File(unzipDestinationDirectory, currentEntry)
//        destFile = new File(unzipDestinationDirectory, destFile.getName)
//        if (currentEntry.endsWith(".zip")) {
//          zipFiles += destFile.getAbsolutePath
//        }
//        // grab file's parent directory structure
//        val destinationParent = destFile.getParentFile
//        // create the parent directory structure if needed
//        destinationParent.mkdirs
//        try // extract file if not a directory
//        if (!entry.isDirectory) {
//          val is = new BufferedInputStream(zipFile.getInputStream(entry))
//          var currentByte = 0
//          // establish buffer for writing file
//          val data = new Array[Byte](BUFFER)
//          // write the current file to disk
//          val fos = new FileOutputStream(destFile)
//          val dest = new BufferedOutputStream(fos, BUFFER)
//          // read and write until last byte is encountered
//          while ( {
//            (currentByte = is.read(data, 0, BUFFER)) != -1
//          }) {
//            dest.write(data, 0, currentByte)
//          }
//          dest.flush()
//          dest.close()
//          is.close()
//        }
//        catch {
//          case ioe: IOException =>
//            ioe.printStackTrace()
//        }
//      }
//    }
//    finally {
//      zipFile.close()
//    }
//
//    //unzip the other zips if any.
//    zipFiles.toList.foreach(zipName => unzipTo(new File(zipName), destinationDirectory + File.separatorChar + zipName.substring(0, zipName.lastIndexOf(".zip"))))
//  }
}
