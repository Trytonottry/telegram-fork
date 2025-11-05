package org.telegram.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import org.telegram.R
import org.telegram.messenger.FileLoader
import org.telegram.messenger.MessagesController
import org.telegram.messenger.UserConfig
import org.telegram.tgnet.TLRPC.*

class MusicFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MusicAdapter
    private lateinit var player: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MusicAdapter { audioUrl ->
            playAudio(audioUrl)
        }
        recyclerView.adapter = adapter
        player = ExoPlayer.Builder(requireContext()).build()
        loadAudioFiles()
        return view
    }

    private fun loadAudioFiles() {
        val audioList = mutableListOf<AudioItem>()

        val dialogs = MessagesController.getInstance(UserConfig.selectedAccount).dialogs
        for (dialog in dialogs) {
            val messages = MessagesController.getInstance(UserConfig.selectedAccount)
                .getHistory(dialog.id, 0, 0, 100) // последние 100 сообщений
            for (msg in messages) {
                val media = msg.media
                when (media) {
                    is TL_messageMediaDocument -> {
                        val doc = media.document
                        if (doc.mime_type.startsWith("audio/")) {
                            val audioUrl = FileLoader.getAttachFileName(doc)
                            audioList.add(AudioItem(doc.file_name, audioUrl))
                        }
                    }
                    is TL_messageMediaAudio -> {
                        val audioUrl = FileLoader.getAttachFileName(media.audio)
                        audioList.add(AudioItem(media.audio.title ?: "Голосовое сообщение", audioUrl))
                    }
                }
            }
        }

        adapter.submitList(audioList)
    }

    private fun playAudio(url: String) {
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}

data class AudioItem(val title: String, val url: String)
