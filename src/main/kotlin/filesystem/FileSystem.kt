package filesystem

import dataretriever.MediaDataRetriever
import models.SongInfo
import java.io.File
import java.nio.file.*
import java.util.stream.Collectors

class MP3Finder(factory : (file: File) -> MediaDataRetriever, path: String){
    private var t : (file: File) -> MediaDataRetriever = factory
    private val MAXIMUM_DEEP = 5;
    var path: String = path
    var allSongs: SongAndDuplicates = SongAndDuplicates()

    init {
        Files.createDirectories(Paths.get("${path}aux"))
        var file = File(path)
        if (file.exists()){
            addFilesFromDirectoryWithExtension(file, "mp3")
        }
    }

    /*fun moveDuplicates(from: String, toSubDir: String){
        dups.parallelStream().map{it -> it.path}.forEach({
            println("moving ${it} to ${path}${toSubDir}\\${it.substring(it.lastIndexOf("\\") + 1)}")
            Files.move(Paths.get(it), Paths.get("${path}${toSubDir}\\${
                it.substring(it.lastIndexOf("\\")+1)}" ), StandardCopyOption.REPLACE_EXISTING)})
    }*/

    private fun addFilesFromDirectoryWithExtension(file: File, extension: String, deep: Int = 0){
        if (!file.canRead() || MAXIMUM_DEEP == deep){
            return
        }
        if (file.isDirectory){
            for (subfil in file.listFiles()){
                addFilesFromDirectoryWithExtension(subfil, extension, deep+1)
            }
        }else{
            if (file.name.toLowerCase().endsWith("mp3")){
                addFileOrMarkItAsDuplicated(file)
            }
        }
    }

    private fun addFileOrMarkItAsDuplicated(file: File){
        try {
            var si = t(file).buildSongInfo()
            var dupHashes = getDuplicatedHashes(si)
            if (dupHashes.isNotEmpty()){
                println("${file} duplicated from ${dupHashes}")
                dupHashes!!.forEach({it -> allSongs.put(it, si)})
            }else{
                allSongs.put(si.hash, si)
            }

        } catch (e: Exception) {
            println("Not able to retreive info from ${file.absoluteFile}")
            e.printStackTrace()
        }
    }

    private fun getDuplicatedHashes(si: SongInfo): List<String> {
        return allSongs.keys.parallelStream().filter{
            value: String -> if (value.length > si.hash.length) value.contains(si.hash) else si.hash.contains(value) }
                .collect(Collectors.toList())
    }
}


class SongAndDuplicates{
    val keys: MutableList<String> = mutableListOf()
    val values: MutableList<SongInfo> = mutableListOf()
    val duplicates: MutableList<MutableList<SongInfo>> = mutableListOf()

    fun put(key: String, value: SongInfo){
        if (keys.indexOf(key) == -1){
            keys.add(0,key)
            values.add(0,value)
            duplicates.add(0, mutableListOf())
        }else{
            duplicates.elementAt(keys.indexOf(key)).add(value)
            println("added ${value} for key ${key} as duplicate which was in ${values.elementAt(keys.indexOf(key))}")
        }
    }

    fun writeToOutput(){
        for (i in 0..keys.size-1){
            println("hash: ${keys.elementAt(i)} song: ${values.elementAt(i)} ")
            if (duplicates.elementAt(i).isNotEmpty()) {
                println("*********BEGIN DUPS*******************")
                duplicates.elementAt(i).forEach { println("${it}") }
                println("*********END DUPS*******************")
            }
        }
    }
}


