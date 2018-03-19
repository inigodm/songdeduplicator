package dataretreiver

import dataretriever.DesktopMediaRetriever
import filesystem.MP3Finder
import models.SongInfo
import org.junit.Assert
import org.junit.Test
import java.io.File

class DataRetreeiveTest{
    @Test fun testData() {
        var mr = DesktopMediaRetriever(File("/home/inigo/wam_music.mp3"))
        Assert.assertEquals("WAM_music", mr.getName())
        Assert.assertEquals(31, mr.getDuration())
    }

    @Test fun testSongInfo(){
        var mr = DesktopMediaRetriever(File("/home/inigo/wam_music.mp3"))
        var si = SongInfo(mr.getName(),
                mr.getArtist(),
                mr.getAlbum(),
                mr.getDuration(),
                mr.getPath())
        mr.buildSongInfo()
    }

    @Test fun testGetAllSongsFromFile(){
        var fs = MP3Finder(::DesktopMediaRetriever)
        var files = fs.getAllMP3sFrom("/home/inigo")
        println("${files.size} songs found")
        println(files)
    }

}