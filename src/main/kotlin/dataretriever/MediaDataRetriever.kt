package dataretriever

import models.SongInfo

interface MediaDataRetriever{
    fun getName():String
    fun getAlbum():String
    fun getArtist():String
    fun getDuration():Long
    fun getPath(): String
    fun buildSongInfo(): SongInfo
}