package com.ssafy.yoganavi.ui.homeUI.schedule.live.webRtc.audio

typealias AudioDeviceChangeListener = (
    audioDevices: List<AudioDevice>,
    selectedAudioDevice: AudioDevice?
) -> Unit
