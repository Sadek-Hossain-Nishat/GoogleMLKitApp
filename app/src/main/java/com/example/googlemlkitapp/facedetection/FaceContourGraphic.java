package com.example.googlemlkitapp.facedetection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraCharacteristics;

import com.google.mlkit.vision.face.Face;

/** Graphic instance for rendering face contours graphic overlay view. */
public class FaceContourGraphic extends GraphicOverlay.Graphic {

  private static final float FACE_POSITION_RADIUS = 10.0f;
  private static final float ID_TEXT_SIZE = 40.0f;
  private static final float ID_Y_OFFSET = 50.0f;
  private static final float ID_X_OFFSET = -50.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;

  private static final int[] COLOR_CHOICES = {
          Color.BLUE , Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW
  };
  private static int currentColorIndex = 0;

  private int facing;

  private final Paint facePositionPaint;
  private final Paint idPaint;
  private final Paint boxPaint;

  private volatile Face firebaseVisionFace;
  private RectF rectF;

  public FaceContourGraphic(GraphicOverlay overlay) {
    super(overlay);

    currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
    final int selectedColor = COLOR_CHOICES[currentColorIndex];

    facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);

    boxPaint = new Paint();
    boxPaint.setColor(selectedColor);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    rectF=new RectF();
  }

  /**
   * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
   * portions of the overlay to trigger a redraw.
   */
  public void updateFace(Face face) {
    firebaseVisionFace = face;

    rectF=adjustBoundingRect(face.getBoundingBox());

    postInvalidate();
  }

    private RectF adjustBoundingRect(Rect rect){
        return new RectF(translateX((float) rect.left),
                translateY((float) rect.top), translateX((float) rect.right),
                translateY((float) rect.bottom));
    }



  /** Draws the face annotations for position on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
 Face face = firebaseVisionFace;
    if (face == null) {
      return;
    }

    // Draws a circle at the position of the detected face, with the face's track id below.
    float x = translateX(face.getBoundingBox().centerX());
    float y = translateY(face.getBoundingBox().centerY());
    canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);
    canvas.drawText("id: " + face.getTrackingId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);
    canvas.drawText(
            "happiness: " + String.format("%.2f", face.getSmilingProbability()),
            x + ID_X_OFFSET * 3,
            y - ID_Y_OFFSET,
            idPaint);
    if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
      canvas.drawText(
              "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
              x - ID_X_OFFSET,
              y,
              idPaint);
      canvas.drawText(
              "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
              x + ID_X_OFFSET * 6,
              y,
              idPaint);
    } else {
      canvas.drawText(
              "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
              x - ID_X_OFFSET,
              y,
              idPaint);
      canvas.drawText(
              "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
              x + ID_X_OFFSET * 6,
              y,
              idPaint);
    }

    // Draws a bounding box around the face.
    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    float left = x - xOffset;
    float top = y - yOffset;
    float right = x + xOffset;
    float bottom = y + yOffset;

      canvas.drawRoundRect(rectF,10,10,boxPaint);
  }
}
