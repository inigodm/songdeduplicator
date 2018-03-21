package dataretreiver

import dataretriever.DesktopMediaRetriever
import filesystem.MP3Finder
import models.SongInfo
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.io.File

class DataRetreeiveTest{
    @Test @Ignore fun testData() {
        var mr = DesktopMediaRetriever(File("/home/inigo/wam_music.mp3"))
        Assert.assertEquals("WAM_music", mr.getName())
        Assert.assertEquals(31, mr.getDuration())
    }

    @Test @Ignore fun testSongInfo(){
        var mr = DesktopMediaRetriever(File("/home/inigo/wam_music.mp3"))
        var si = SongInfo(mr.getName(),
                mr.getArtist(),
                mr.getAlbum(),
                mr.getDuration(),
                mr.getPath())
        mr.buildSongInfo()
    }

    @Test fun testGetAllSongsFromFile(){
        var fs = MP3Finder(::DesktopMediaRetriever, "D:\\DATOS\\idelgado\\Music")
        var files = fs.allSongs
        println("${files.keys.size} songs found")
        files.writeToOutput()
    }

}