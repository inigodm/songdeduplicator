package filesystem

import dataretriever.MediaDataRetriever
import models.SongInfo
import java.io.File
import java.util.stream.Collectors

class MP3Finder{
    var t : (file: File) -> MediaDataRetriever//KClass<MediaDataRetriever>
    val MAXIMUM_DEEP = 5;
    val delMee = mutableListOf<String>()

    constructor(file: File, factory : (file: File) -> MediaDataRetriever){
        t = factory
    }

    fun getAllMP3sFrom(path: String): List<SongInfo>{
        val res = mutableMapOf<String, SongInfo>()
        var file = File(path)
        if (file.exists()){
            addFilesFromDirectoryWithExtension(file, "mp3", res)
        }
        return res.entries.stream().map { entry -> entry.value }.collect(Collectors.toList());
    }

    fun addFilesFromDirectoryWithExtension(file: File, extension: String, res: MutableMap<String, SongInfo>, deep: Int = 0){
        if (!file.canRead() || MAXIMUM_DEEP == deep){
            return;
        }
        if (file.isDirectory){
            for (subfil in file.listFiles()){
                addFilesFromDirectoryWithExtension(subfil, extension, res, deep+1)
            }
        }else{
            if (file.name.toLowerCase().endsWith("mp3")){
                addFileOrMarkItAsDuplicated(file, res)
            }
        }
    }

    fun addFileOrMarkItAsDuplicated(file: File, res: MutableMap<String, SongInfo>){
        try {
            var si = t(file).buildSongInfo()
            if (isDuplicated(si, res)){
                println("Song duplicated: ${file} hash ${si.hash}")
            }else{
                res.put(si.hash, si)
            }

        } catch (e: Exception) {
            println("Not able to retreive info from ${file.absoluteFile}")
        }
    }

    fun isDuplicated(si: SongInfo, res: MutableMap<String, SongInfo>): Boolean{
        return res.map{entry ->  entry.key}.parallelStream().anyMatch({
            value -> if (value.length > si.hash.length)value.contains(si.hash) else si.hash.contains(value) })
        //return res.containsKey(si.hash)111
    }
}

