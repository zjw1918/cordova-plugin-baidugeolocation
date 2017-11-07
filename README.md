# cordova-plugin-baidugeolocation

### Android端调用百度Android 定位sdk

### 用法
```js
BaiduGeolocation.startWatch(function(p) {
    console.log(p);
}, function (err) {
    console.error(err);
})

setTimeout(function() {
    BaiduGeolocation.stopWatch();
}, 20 * 1000);
```


### 参考：

https://github.com/aruis/cordova-plugin-baidumaplocation

https://github.com/ionic-team/cordova-plugin-template