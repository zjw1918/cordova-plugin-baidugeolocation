/**
 */
package com.zjw;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class BaiduGeolocation extends CordovaPlugin {
    private static final String TAG = "BaiduGeolocation";

    public static final String START_WATCH = "startWatch";
    public static final String STOP_WATCH = "stopWatch";

    /**
     * JS回调接口对象
     */
    public static CallbackContext cbCtx = null;

    /**
     * 百度定位客户端
     */
    public LocationClient mLocationClient = null;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing BaiduGeolocation");
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cbCtx = callbackContext;
        if (action.equals("echo")) {
            String phrase = args.getString(0);
            // Echo back the first argument
            Log.d(TAG, phrase);
        } else if (action.equals("getDate")) {
            // An example of returning data back to the web layer
            final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
            callbackContext.sendPluginResult(result);
        } else if ("getCurrentPosition".equalsIgnoreCase(action)) {
            if (!needsToAlertForRuntimePermission()) {
                performGetLocation();
            } else {
                requestPermission();
                // 会在onRequestPermissionResult时performGetLocation
            }
        } else if (START_WATCH.equalsIgnoreCase(action)) {
            if (!needsToAlertForRuntimePermission()) {
                startWatchLocation();
            } else {
                requestPermission();
            }
        } else if (STOP_WATCH.equalsIgnoreCase(action)) {
            stopWatchLocation();
        }
        return true;
    }

    public BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                JSONObject json = new JSONObject();

                json.put("time", location.getTime());
                json.put("locType", location.getLocType());
                json.put("locTypeDescription", location.getLocTypeDescription());
                json.put("latitude", location.getLatitude());
                json.put("longitude", location.getLongitude());
                json.put("radius", location.getRadius());
                json.put("speed", location.getSpeed());
                json.put("accuracy", location.getGpsAccuracyStatus());

                json.put("countryCode", location.getCountryCode());
                json.put("country", location.getCountry());
                json.put("citycode", location.getCityCode());
                json.put("city", location.getCity());
                json.put("district", location.getDistrict());
                json.put("street", location.getStreet());
                json.put("addr", location.getAddrStr());
                json.put("province", location.getProvince());

                json.put("userIndoorState", location.getUserIndoorState());
                json.put("direction", location.getDirection());
                json.put("locationDescribe", location.getLocationDescribe());

                PluginResult pluginResult;
                if (location.getLocType() == BDLocation.TypeServerError
                        || location.getLocType() == BDLocation.TypeNetWorkException
                        || location.getLocType() == BDLocation.TypeCriteriaException) {

                    json.put("describe", "定位失败");
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, json);
                } else {
                    pluginResult = new PluginResult(PluginResult.Status.OK, json);
                }

                cbCtx.sendPluginResult(pluginResult);
            } catch (JSONException e) {
                String errMsg = e.getMessage();
                Log.e(TAG, errMsg, e);

                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, errMsg);
                cbCtx.sendPluginResult(pluginResult);
            } finally {
                mLocationClient.stop();
            }
        }
    };

    /**
     * listenner of gps watch
     */
    public BDLocationListener bdLocationWatchListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                JSONObject json = new JSONObject();

                json.put("time", location.getTime());
                json.put("locType", location.getLocType());
                json.put("locTypeDescription", location.getLocTypeDescription());
                json.put("latitude", location.getLatitude());
                json.put("longitude", location.getLongitude());
                json.put("radius", location.getRadius());
                json.put("speed", location.getSpeed());
                json.put("accuracy", location.getGpsAccuracyStatus());

                json.put("countryCode", location.getCountryCode());
                json.put("country", location.getCountry());
                json.put("citycode", location.getCityCode());
                json.put("city", location.getCity());
                json.put("district", location.getDistrict());
                json.put("street", location.getStreet());
                json.put("addr", location.getAddrStr());
                json.put("province", location.getProvince());


                json.put("userIndoorState", location.getUserIndoorState());
                json.put("direction", location.getDirection());
                json.put("locationDescribe", location.getLocationDescribe());

                PluginResult pluginResult;
                if (location.getLocType() == BDLocation.TypeServerError
                        || location.getLocType() == BDLocation.TypeNetWorkException
                        || location.getLocType() == BDLocation.TypeCriteriaException) {

                    json.put("describe", "定位失败");
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, json);
                } else {
                    pluginResult = new PluginResult(PluginResult.Status.OK, json);
                }
                pluginResult.setKeepCallback(true);
                cbCtx.sendPluginResult(pluginResult);
            } catch (JSONException e) {
                String errMsg = e.getMessage();
                Log.e(TAG, errMsg, e);

                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, errMsg);
                pluginResult.setKeepCallback(true);
                cbCtx.sendPluginResult(pluginResult);
            }
        }
    };

    /**
     * 安卓6以上动态权限相关
     */

    private static final int REQUEST_CODE = 100001;

    private boolean needsToAlertForRuntimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || !cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ArrayList<String> permissionsToRequire = new ArrayList<String>();

        if (!cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsToRequire.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (!cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsToRequire.add(Manifest.permission.ACCESS_FINE_LOCATION);

        String[] _permissionsToRequire = new String[permissionsToRequire.size()];
        _permissionsToRequire = permissionsToRequire.toArray(_permissionsToRequire);
        cordova.requestPermissions(this, REQUEST_CODE, _permissionsToRequire);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (cbCtx == null || requestCode != REQUEST_CODE)
            return;
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                JSONObject json = new JSONObject();
                json.put("describe", "定位失败");
                Log.e(TAG, "权限请求被拒绝");
                cbCtx.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, json));
                return;
            }
        }

        performGetLocation();
    }

    private LocationClientOption getDefaultLocationClientOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true); // 可选，默认false,设置是否使用gps
        option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集

        option.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        return option;
    }

    private LocationClientOption getWatchLocationClientOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true); // 可选，默认false,设置是否使用gps
        option.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        return option;
    }

    /**
     * 权限获得完毕后进行定位
     */
    private void performGetLocation() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this.webView.getContext());
            mLocationClient.registerLocationListener(bdLocationListener);
            mLocationClient.setLocOption(getDefaultLocationClientOption());
        }

        mLocationClient.start();
    }

    /**
     * 监测gps变化
     */
    public void startWatchLocation() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this.webView.getContext());
            mLocationClient.registerLocationListener(bdLocationWatchListener);
            mLocationClient.setLocOption(getWatchLocationClientOption());
        }

        mLocationClient.start();
    }

    private void stopWatchLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

}
