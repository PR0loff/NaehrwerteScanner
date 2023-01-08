package com.example.testapplication.backend;

import android.graphics.Bitmap;

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

//Klasse in welcher die Texterkennung realisiert wird
//https://developers.google.com/ml-kit/vision/text-recognition
public class TextRecocgnition {

    public static Text TextBlocks;
    public static boolean isReady = false;

    //Methode erhält das Bild als Bitmap und eine Callback Funktion welche bei
    //Abschluss aufgerufen wird
    public static void runTextRecognition(Bitmap BmImage, Callable<Void> callback) {
        InputImage image = InputImage.fromBitmap(BmImage, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        //Asynchrone Texterkennung
        Task tr = recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                TextBlocks = texts;
                                isReady = true;
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
                            }
                        });
    }
}