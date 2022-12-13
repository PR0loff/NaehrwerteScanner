package com.example.naehrwertescanner;

import static androidx.core.content.FileProvider.getUriForFile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.naehrwertescanner.backend.GraphicOverlay;
import com.example.naehrwertescanner.backend.TextGraphic;
import com.example.naehrwertescanner.backend.TextRecocgnition;
import com.google.mlkit.vision.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    private String currentPhotoPath;
    private static final int CAMERA_REQUEST = 1;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private TextRecocgnition TR = new TextRecocgnition();
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String fileName = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                Log.println(Log.DEBUG,"TESTER", "currentPhotoPath");
                try {
                    File imageFile =  File.createTempFile(fileName, ".jpg", storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();
                    Log.println(Log.DEBUG,"TESTER", currentPhotoPath);


                    //Uri imageUri = getUriForFile(MainActivity.this, "com.example.naehrwertescanner.fileprovider", imageFile);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            //Bitmap photo = BitmapFactory.decodeFile(currentPhotoPath);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //Log.println(Log.DEBUG,"TESTER", currentPhotoPath);

            imageView.setImageBitmap(photo);

            //List<Text.TextBlock> blocks =  TR.runTextRecognition(photo);
            TR.runTextRecognition(photo);

            if(false) {
                Text text = null;
                int b = 0;
                while (text == null) {
                    text = TR.TextBlocks[0];
                    if (b % 1000000 == 0)
                        Log.println(Log.DEBUG, "TESTER", "incompleted");
                    b++;
                }

                List<Text.TextBlock> blocks = text.getTextBlocks();

                for (int i = 0; i < blocks.size(); i++) {
                    List<Text.Line> lines = blocks.get(i).getLines();
                    for (int j = 0; j < lines.size(); j++) {
                        List<Text.Element> elements = lines.get(j).getElements();
                        for (int k = 0; k < elements.size(); k++) {
                            Log.println(Log.DEBUG, "Debug", elements.get(k).getText());
                        }
                    }
                }
            }
        }
    }
}