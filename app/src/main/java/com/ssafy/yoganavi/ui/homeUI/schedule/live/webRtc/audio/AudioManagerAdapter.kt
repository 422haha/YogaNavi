package com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.audio

internal interface AudioManagerAdapter {

  fun hasEarpiece(): Boolean

  fun hasSpeakerphone(): Boolean

  fun setAudioFocus()

  fun enableBluetoothSco(enable: Boolean)

  fun enableSpeakerphone(enable: Boolean)

  fun mute(mute: Boolean)

  fun cacheAudioState()

  fun restoreAudioState()
}
