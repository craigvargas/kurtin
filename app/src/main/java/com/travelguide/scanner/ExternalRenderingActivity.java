package com.travelguide.scanner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.wikitude.WikitudeSDK;
import com.wikitude.WikitudeSDKStartupConfiguration;
import com.wikitude.common.camera.CameraSettings;
import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.common.tracking.RecognizedTarget;
import com.wikitude.rendering.ExternalRendering;
import com.wikitude.tracker.ClientTracker;
import com.wikitude.tracker.ClientTrackerEventListener;
import com.wikitude.tracker.Tracker;

public class ExternalRenderingActivity extends Activity implements ClientTrackerEventListener, ExternalRendering {

    private static final String TAG = "ExternalRendering";


    private WikitudeSDK _wikitudeSDK;
    private CustomSurfaceView _view;
    private Driver _driver;
    private GLRenderer _glRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _wikitudeSDK = new WikitudeSDK(this);
        WikitudeSDKStartupConfiguration startupConfiguration = new WikitudeSDKStartupConfiguration(WikitudeSDKConstants.WIKITUDE_SDK_KEY, CameraSettings.CameraPosition.BACK, CameraSettings.CameraFocusMode.CONTINUOUS);
        _wikitudeSDK.onCreate(getApplicationContext(), startupConfiguration);
        ClientTracker tracker = _wikitudeSDK.getTrackerManager().create2dClientTracker("file:///android_asset/magazine.wtc");
        tracker.registerTrackerEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _wikitudeSDK.onResume();
        _view.onResume();
        _driver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _wikitudeSDK.onPause();
        _view.onPause();
        _driver.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _wikitudeSDK.onDestroy();
    }

    @Override
    public void onRenderExtensionCreated(final RenderExtension renderExtension_) {
        _glRenderer = new GLRenderer(renderExtension_);
        _view = new CustomSurfaceView(getApplicationContext(), _glRenderer);
        _driver = new Driver(_view, 30);
        setContentView(_view);
    }

    @Override
    public void onErrorLoading(final ClientTracker clientTracker_, final String errorMessage_) {
        Log.v(TAG, "onErrorLoading: " + errorMessage_);
    }

    @Override
    public void onTrackerFinishedLoading(final ClientTracker clientTracker_, final String trackerFilePath_) {

    }

    @Override
    public void onTargetRecognized(final Tracker tracker_, final String targetName_) {

    }

    @Override
    public void onTracking(final Tracker tracker_, final RecognizedTarget recognizedTarget_) {
        _glRenderer.setCurrentlyRecognizedTarget(recognizedTarget_);
    }

    @Override
    public void onTargetLost(final Tracker tracker_, final String targetName_) {
        _glRenderer.setCurrentlyRecognizedTarget(null);
    }

    @Override
    public void onExtendedTrackingQualityUpdate(final Tracker tracker_, final String targetName_, final int oldTrackingQuality_, final int newTrackingQuality_) {

    }

}