# Skowa Reader

This is an implementation of a desktop skowa (Swiss-Knife over Wi-Fi and audio) reader in Kotlin.

# Build

Build the `../swiss-knife/` library.

Then, create a distribution with `gradle distTar`. It is necessary in order to allow microphone access on macOS.

# Usage

Untar `build/distributions/skowa-reader.tar` and launch `skowa-reader/bin/skowa-reader`.

A `ServerSocket` is opened on port 9999. You can then use any compatible skowa tag and the distance-bounding protocol will start.

Sound and microphone are required on both devices for the fast phase. In the `android/` folder of this repo a skowa tag is implemented as an Android app.

# Results

The verification is working, but the signal processing is too slow. When the tag is next to the reader it gets verified as a ~100m far tag.
