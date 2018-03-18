package dataretriever

import com.mpatric.mp3agic.ID3v1
import com.mpatric.mp3agic.ID3v22Tag
import com.mpatric.mp3agic.Mp3File
import models.SongInfo
import java.io.File

class DesktopMediaRetriever(file: File): MediaDataRetriever {
    val VOID = "N/A"
    var mp3 : Mp3File

    var pth: String
    var id : ID3v1

    init{
        mp3 = Mp3File(file)
        pth = file.path
        mp3 = Mp3File(pth)
        if (mp3.hasId3v1Tag()){
            id = mp3.getId3v1Tag()
        }else if(mp3.hasId3v2Tag()){
            id = mp3.getId3v2Tag()
        }else{
            id = ID3v22Tag()
        }
        if (id.title == null || id.title.equals("")){
            id.title = file.name.substring(0, file.name.length - 4)
        }
    }

    override fun getName(): String {
        return id.title ?: VOID
    }

    override fun getAlbum(): String {
        return id.album ?: VOID
    }

    override fun getArtist(): String {
        return id.artist ?: VOID
    }

    override fun getDuration(): Long {
        return mp3.lengthInSeconds ?: 0
    }

    override fun getPath(): String {
        return pth
    }
    override fun buildSongInfo(): SongInfo {
        return SongInfo(getName(),
                getArtist(),
                getAlbum(),
                getDuration(),
                getPath())
    }

}