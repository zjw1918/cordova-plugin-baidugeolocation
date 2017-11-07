
var exec = require('cordova/exec');

var PLUGIN_NAME = 'BaiduGeolocation';

var BaiduGeolocation = {
  echo: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
  },
  getDate: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'getDate', []);
  },
  getCurrentPosition: function (successCallback, errorCallback, options) {

      // Timer var that will fire an error callback if no position is retrieved from native
      // before the "timeout" param provided expires
      var timeoutTimer = { timer: null };

      var win = function (p) {
          clearTimeout(timeoutTimer.timer);
          if (!(timeoutTimer.timer)) {
              // Timeout already happened, or native fired error callback for
              // this geo request.
              // Don't continue with success callback.
              return;
          }
          successCallback(p);
      };
      var fail = function (e) {
          clearTimeout(timeoutTimer.timer);
          timeoutTimer.timer = null;
          if (errorCallback) {
              errorCallback(e);
          }
      };

      if (options && options.timeout !== Infinity) {
          // If the timeout value was not set to Infinity (default), then
          // set up a timeout function that will fire the error callback
          // if no successful position was retrieved before timeout expired.
          timeoutTimer.timer = createTimeout(fail, options.timeout);
      } else {
          // This is here so the check in the win function doesn't mess stuff up
          // may seem weird but this guarantees timeoutTimer is
          // always truthy before we call into native
          timeoutTimer.timer = true;
      }
      exec(win, fail, PLUGIN_NAME, 'getCurrentPosition', [options]);
      return timeoutTimer
  },
  startWatch: function(successCallback, errorCallback) {
    var win = function(p) {
      if (successCallback) {
        successCallback(p);        
      }
    }

    var fail = function(e) {
      if (errorCallback) {
        errorCallback(e);
      }
    }

    exec(win, fail, PLUGIN_NAME, 'startWatch', []);
  },
  stopWatch: function (cb) {
    exec(cb, null, PLUGIN_NAME, 'stopWatch', []);
  }

};

// Returns a timeout failure, closed over a specified timeout value and error callback.
function createTimeout(errorCallback, timeout) {
  var t = setTimeout(function () {
      clearTimeout(t);
      t = null;
      errorCallback({
          code: -1,
          message: "Position retrieval timed out."
      });
  }, timeout);
  return t;
}

module.exports = BaiduGeolocation;
