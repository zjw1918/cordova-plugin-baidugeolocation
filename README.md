# cordova-plugin-baidugeolocation

### Android端调用百度Android 定位sdk

### 安装

cordova plugin add cordova-plugin-baidumaplocation --variable ANDROID_KEY=YOUR_BAIDU_ANDROID_APIKEY

### 用法
```js
// watch
BaiduGeolocation.startWatch(function(p) {
    console.log(p);
}, function (err) {
    console.error(err);
})

setTimeout(function() {
    BaiduGeolocation.stopWatch();
}, 20 * 1000);

// getCurrent
BaiduGeolocation.getCurrentPosition(function (res) {
    console.log(res);
}, function (err) {
    console.log(err);
});

```

### 说明
1. ios定位可以直接用cordova-plugin-geolocation, 没有必要再造轮子。返回wgs的经纬度。
2. 这个插件调用的百度定位sdk。返回bd09ll的经纬度。

### 参考：

https://github.com/aruis/cordova-plugin-baidumaplocation

https://github.com/ionic-team/cordova-plugin-template

https://github.com/apache/cordova-plugin-geolocation