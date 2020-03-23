# Skowa Reader

This is an implementation of a desktop skowa (swiss-knife over wi-fi and audio) reader in Kotlin.

# Build

Build the `../swiss-knife/` library.

Then, create a distribution with `gradle distTar`. It is necessary in order to allow microphone access on macOS.

Untar `build/distributions/skowa-reader.tar` and launch `skowa-reader/bin/skowa-reader`.