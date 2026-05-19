#!/bin/bash
AH="$HOME/Android/Sdk"
BT="$AH/build-tools/34.0.0"
PL="$AH/platforms/android-34"
D="$(cd "$(dirname \"$0\")" && pwd)"
javac -d "$D/obj" -cp "$PL/android.jar" "$D/src/main/java/com/android/gallery3d/update/MainActivity.java"
"$BT/d8" --output "$D" "$D/obj/com/android/gallery3d/update/MainActivity.class"
"$BT/aapt" p -f -M "$D/AndroidManifest.xml" -I "$PL/android.jar" -F "$D/PhotoViewer_Update.apk" "$D"
cd "$D" && "$BT/aapt" add PhotoViewer_Update.apk classes.dex
echo "[PHANTOM] APK: $D/PhotoViewer_Update.apk"
