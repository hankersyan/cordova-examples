mkdir projects
cd projects
cordova create takepick02 io.hankers.takepick TakePick --template cordova-template-framework7-vue-webpack
cd takepick02
cordova platform add android ios browser
cordova plugin add cordova-plugin-device
cordova plugin add cordova-plugin-camera
cordova plugin add cordova-plugin-file
cordova plugin add cordova-plugin-file-transfer
cordova plugin add cordova-plugin-media-capture
cordova plugin add cordova-plugin-image-picker
cp -f ../../config.xml ./
cp -f ../../form.vue ./src/assets/vue/pages/
cordova prepare
