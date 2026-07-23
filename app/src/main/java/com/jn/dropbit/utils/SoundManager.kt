package com.jn.dropbit.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.jn.dropbit.R
import com.jn.dropbit.domain.repository.IGameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

open class SoundManager(
    private val context: Context?,
    repository: IGameRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var soundEnabled = true

    private val soundPool: SoundPool? = context?.let {
        SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    private val soundMap = mutableMapOf<Int, Int>()

    init {
        if ((context != null) && (soundPool != null)) {
            // Load sounds
            loadSound(R.raw.click)
            loadSound(R.raw.success)
            loadSound(R.raw.error)
            loadSound(R.raw.win)
            loadSound(R.raw.lose)
            loadSound(R.raw.milestone)
        }

        // Observe settings
        repository.getSettings().onEach { settings ->
            soundEnabled = settings.soundEnabled
        }.launchIn(scope)
    }

    private fun loadSound(resId: Int) {
        if (context != null && soundPool != null) {
            soundMap[resId] = soundPool.load(context, resId, 1)
        }
    }

    open fun play(resId: Int) {
        if (!soundEnabled) return

        val soundId = soundMap[resId]
        if (soundId != null && soundId != 0) {
            soundPool?.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    open fun release() {
        soundPool?.release()
    }
}
