<template>
  <f7-page>
    <f7-navbar title="Form components" back-link="Back"></f7-navbar>
    <div class="block block-strong">
      <p>With forms storage it is easy to store and parse form data, especially on Ajax loaded pages. All you need to make it work is to add "form-store-data" class to your &lt;form&gt; and Framework7 will store form data with every input change. And the most awesome part is that when you load this page again Framework7 will parse this data and fill all form fields automatically!</p>
      <p>Just try to fill the form below and then go to any other page, or even you may close this site, and when you return here form fields will have kept your data.</p>
    </div>
    <div id="gallery"></div>
    <form class="list form-store-data" id="demo-form">
      <ul>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Name</div>
            <div class="item-input-wrap">
              <input type="button" id="avatarImg" value="Click input-file" />
              <input
                name="avatar"
                id="avatar"
                type="file"
                placeholder="Avatar"
                style="display:none;"
              />
              <span class="input-clear-button"></span>
              <input type="button" id="take" value="TakeByPlugin" />
              <input type="button" id="pick" value="PickByPlugin" />
            </div>
          </div>
        </li>

        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Name</div>
            <div class="item-input-wrap">
              <input name="name" type="text" placeholder="Your name" />
              <span class="input-clear-button"></span>
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Password</div>
            <div class="item-input-wrap">
              <input name="password" type="password" placeholder="Your password" />
              <span class="input-clear-button"></span>
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">E-mail</div>
            <div class="item-input-wrap">
              <input name="email" type="email" placeholder="Your e-mail" />
              <span class="input-clear-button"></span>
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">URL</div>
            <div class="item-input-wrap">
              <input name="url" type="url" placeholder="URL" />
              <span class="input-clear-button"></span>
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Phone</div>
            <div class="item-input-wrap">
              <input name="phone" type="tel" placeholder="Your phone number" />
              <span class="input-clear-button"></span>
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Gender</div>
            <div class="item-input-wrap">
              <select name="gender" placeholder="Please choose...">
                <option value="Male">Male</option>
                <option value="Female">Female</option>
              </select>
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Birthday</div>
            <div class="item-input-wrap">
              <input name="birthday" type="date" value="2014-04-30" placeholder="Please choose..." />
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Date time</div>
            <div class="item-input-wrap">
              <input name="date" type="datetime-local" placeholder="Please choose..." />
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">Range</div>
            <div class="item-input-wrap">
              <div class="range-slider range-slider-init" data-label="true">
                <input name="range" type="range" value="50" min="0" max="100" step="1" />
              </div>
            </div>
          </div>
        </li>
        <li class="item-content item-input">
          <div class="item-inner">
            <div class="item-title item-label">About you</div>
            <div class="item-input-wrap">
              <textarea name="bio" class="resizable" placeholder="Bio"></textarea>
            </div>
          </div>
        </li>
      </ul>
    </form>
  </f7-page>
</template>
<script>
const URL_PREFIX = "http://YOUR/SERVER/";

export default {
  components: {},
  mounted() {
    const self = this;
    console.log("mounted");
    self.$f7ready(() => {
      var btn = document.getElementById("avatarImg");
      btn.addEventListener(
        "click",
        () => {
          console.log("avatarImg::click::1");
          document.getElementById("avatar").click();
          console.log("avatarImg::click::2");
        },
        false
      );

      var take = document.getElementById("take");
      take.addEventListener(
        "click",
        () => {
          self.takePhoto();
        },
        false
      );

      var pick = document.getElementById("pick");
      pick.addEventListener(
        "click",
        () => {
          self.pickPhoto();
        },
        false
      );
    });
  },
  methods: {
    takePhoto: function() {
      const self = this;
      // capture callback
      var captureSuccess = function(mediaFiles) {
        var i, path, len;
        var parentElement = document.getElementById("gallery");
        for (i = 0, len = mediaFiles.length; i < len; i += 1) {
          console.log(mediaFiles[i]);
          path = mediaFiles[i].fullPath;
          // do something interesting with the file
          self.uploadFile(mediaFiles[i].localURL);
        }
        console.log(parentElement.innerHTML);
      };

      // capture error callback
      var captureError = function(error) {
        console.log("Error code: " + error.code, null, "Capture Error");
      };

      // start image capture
      navigator.device.capture.captureImage(captureSuccess, captureError, {
        limit: 1
      });
    },

    appendImg: function(imgUrl) {
      // do something interesting with the file
      var img = document.createElement("IMG");
      img.style.width = "100px";
      img.src = imgUrl;
      document.getElementById("gallery").appendChild(img);
    },

    uploadFile: function(fileURL) {
      const self = this;
      var win = function(r) {
        console.log("Code = " + r.responseCode);
        console.log("Response = " + r.response);
        console.log("Sent = " + r.bytesSent);
        var filename = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        console.log(filename);
        self.appendImg(URL_PREFIX + "/temp/" + filename);
      };

      var fail = function(error) {
        alert("An error has occurred: Code = " + error.code);
        console.log("upload error source " + error.source);
        console.log("upload error target " + error.target);
      };

      var options = new FileUploadOptions();
      options.fileKey = "file";
      options.mimeType = "text/plain";
      options.fileName = fileURL.substr(fileURL.lastIndexOf("/") + 1);

      var params = {};
      params.value1 = "test";
      params.value2 = "param";

      options.params = params;

      var ft = new FileTransfer();
      ft.upload(
        fileURL,
        encodeURI(URL_PREFIX + "saveUpload.aspx"),
        win,
        fail,
        options
      );
    },

    pickPhoto: function(e) {
      const self = this;

      window.imagePicker.getPictures(
        function(results) {
          for (var i = 0; i < results.length; i++) {
            console.log("Image URI: " + results[i]);

            self.uploadFile(results[i]);
          }
        },
        function(error) {
          console.log("Error: " + error);
        },
        {
          maximumImagesCount: 8,
          width: 800
        }
      );
    }
  }
};
</script>
