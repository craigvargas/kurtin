package com.travelguide.scanner;

import com.wikitude.common.rendering.RenderExtension;
import com.wikitude.common.tracking.RecognizedTarget;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRendererFaceDetectionPlugin extends GLRenderer {

    StrokedRectangle _faceDetectionRectangle;
    private RecognizedTarget _currentlyRecognizedFace;

    public GLRendererFaceDetectionPlugin(RenderExtension wikitudeRenderExtension_) {
        super(wikitudeRenderExtension_);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        _faceDetectionRectangle = new StrokedRectangle(StrokedRectangle.Type.FACE);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        super.onDrawFrame(unused);
        if (_currentlyRecognizedFace != null) {
            _faceDetectionRectangle.onDrawFrame(_currentlyRecognizedFace);
        }
    }

    public void setCurrentlyRecognizedFace(RecognizedTarget face) {
        _currentlyRecognizedFace = face;
    }

}
