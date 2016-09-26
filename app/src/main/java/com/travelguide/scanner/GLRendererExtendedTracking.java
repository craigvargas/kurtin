package com.travelguide.scanner;

import com.wikitude.common.rendering.RenderExtension;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRendererExtendedTracking extends GLRenderer {

    StrokedRectangle _extendedTrackingRectangle;

    public GLRendererExtendedTracking(RenderExtension wikitudeRenderExtension_) {
        super(wikitudeRenderExtension_);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        _extendedTrackingRectangle = new StrokedRectangle(StrokedRectangle.Type.EXTENDED);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);
        if (_currentlyRecognizedTarget != null) {
            _extendedTrackingRectangle.onDrawFrame(_currentlyRecognizedTarget);
        }
    }
}
