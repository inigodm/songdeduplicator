package filesystem

import dataretriever.MediaDataRetriever
import models.SongInfo
import java.io.File

class MP3Finder(factory : (file: File) -> MediaDataRetriever){
    private var t : (file: File) -> MediaDataRetriever = factory
    val MAXIMUM_DEEP = 5;

    fun getAllMP3sFrom(path: String): List<SongInfo>{
        val res = TwoListsMap<SongInfo>()
        var file = File(path)
        if (file.exists()){
            addFilesFromDirectoryWithExtension(file, "mp3", res)
        }
        return res.values
    }

    private fun addFilesFromDirectoryWithExtension(file: File, extension: String, res: TwoListsMap<SongInfo>, deep: Int = 0){
        if (!file.canRead() || MAXIMUM_DEEP == deep){
            return
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

    private fun addFileOrMarkItAsDuplicated(file: File, res: TwoListsMap<SongInfo>){
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

    private fun isDuplicated(si: SongInfo, res: TwoListsMap<SongInfo>): Boolean{
        return res.keys.parallelStream().anyMatch({
            value -> if (value.length > si.hash.length) value.contains(si.hash) else si.hash.contains(value) })
    }
}

class TwoListsMap<T>{
    val keys: MutableList<String> = mutableListOf()
    val values: MutableList<T> = mutableListOf()

    fun put(key: String, value: T){
        keys.add(0,key)
        values.add(0,value)
    }
}

