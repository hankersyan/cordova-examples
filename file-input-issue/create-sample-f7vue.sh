mkdir projects
cd projects
cordova create fileIssue io.hankers.fileissue FileIssue --template cordova-template-framework7-vue-webpack
cd fileIssue
cordova platform add android ios
cordova plugin add cordova-plugin-device
cordova plugin add cordova-plugin-file
cordova plugin add cordova-plugin-camera
# wkwebview has a keyboard issue
#cordova plugin add cordova-plugin-wkwebview-engine
cordova plugin add cordova-plugin-ionic-webview
cp -f ../../form.vue ./src/assets/vue/pages/
cp -f ../../SystemWebChromeClient.java ./platforms/android/CordovaLib/src/org/apache/cordova/engine/
cordova prepare
