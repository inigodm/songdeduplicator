package models

import java.text.Normalizer

public data class SongInfo(val title: String,
                           val album: String,
                           val artist: String,
                           val duration: Long,
                           val path: String,
                           val hash: String = Normalizer.normalize(title.toLowerCase(), Normalizer.Form.NFD).replace("[^a-z0-9]".toRegex(), "")
)
