mkdir projects
cd projects
cordova create simplefile01 io.hankers.simplefile01 SimpleFile01
cd simplefile01
cordova platform add android ios
cordova plugin add cordova-plugin-device
cordova plugin add cordova-plugin-file
cordova plugin add cordova-plugin-camera
cordova plugin add cordova-plugin-ionic-webview
cp -f ../../index.html ./www/
cp -f ../../index.js ./www/js/
cp -f ../../SystemWebChromeClient.java ./platforms/android/CordovaLib/src/org/apache/cordova/engine/
cordova prepare
