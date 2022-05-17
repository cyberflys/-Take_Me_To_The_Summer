import os,io,sys
import subprocess
print("cyfly DALAS Setup tool")
adb install spotify.apk
adb install dash.apk
adb uninstall com.android.launcher3
adb shell cmd package set-home-activity "com.example.dasher/.MainActivity"



