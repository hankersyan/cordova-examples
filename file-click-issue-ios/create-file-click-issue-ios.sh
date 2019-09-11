mkdir projects
cd projects
cordova create fileClickIssueIOS io.hankers.fileclickissueios FileClickIssueIOS --template cordova-template-framework7-vue-webpack
cd fileClickIssueIOS
cordova platform add android ios browser
cordova plugin add cordova-plugin-wkwebview-engine
cp -f ../../form.vue ./src/assets/vue/pages/
cordova prepare
