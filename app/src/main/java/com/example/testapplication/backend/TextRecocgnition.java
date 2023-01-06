package com.example.testapplication.backend;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.TextBlock;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.List;
import java.util.concurrent.Callable;


public class TextRecocgnition {

    public Text TextBlocks;
    public boolean isReady = false;

    public void runTextRecognition(Bitmap BmImage, Callable<Void> callback) {
        InputImage image = InputImage.fromBitmap(BmImage, 0);

        TextRecognizer recognizer = TextRecognition.getClient();

        Task tr = recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                TextBlocks = texts;
                                Log.println(Log.DEBUG, "TESTER","SUCCESS");
                                processTextRecognitionResult(texts);
                                try {
                                    callback.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Log.println(Log.DEBUG, "TESTER","asdadsdfadsffdasasdffad");
                            }
                        });
    }

    private List<TextBlock> processTextRecognitionResult(Text texts) {
        List<TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {

            return null;
        }
        //mGraphicOverlay.clear();
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    //GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    //mGraphicOverlay.add(textGraphic);
                    //Log.println(Log.DEBUG, "TEXTERKENNUNG", elements.get(k).getText());

                }
            }
        }
        isReady = true;
        return blocks;
    }

}
