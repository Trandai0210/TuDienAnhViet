package com.example.tudienanhviet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText edtInput;
    ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    TextView txtIn;
    TextView txtOut;
    ImageButton btnChange;
    TextView txtKetQua;
    Button btnDich;
    Boolean isEnglishToVietnamese = true;
    Translator englishVietnameseTranslator;
    Translator vietnameseEnglishTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        addAction();
    }

    public void init(){
        edtInput = findViewById(R.id.edtInput);
        btnSpeak = findViewById(R.id.btnSpeak);
        txtIn = findViewById(R.id.txtIn);
        txtOut = findViewById(R.id.txtOut);
        btnChange = findViewById(R.id.btnChange);
        txtKetQua = findViewById(R.id.txtKetQua);
        btnDich = findViewById(R.id.btnDich);

        englishVietnameseTranslator = Translation.getClient(new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                .build());
        getLifecycle().addObserver(englishVietnameseTranslator);
        DownloadConditions conditions1 = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishVietnameseTranslator.downloadModelIfNeeded(conditions1)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("AAA","Download englishVietnameseTranslator success!");
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                                Log.d("AAA","Download englishVietnameseTranslator false!");
                            }
                        });
        vietnameseEnglishTranslator = Translation.getClient(new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build());
        getLifecycle().addObserver(vietnameseEnglishTranslator);
        DownloadConditions conditions2 = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        vietnameseEnglishTranslator.downloadModelIfNeeded(conditions2)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("AAA","Download vietnameseEnglishTranslator success!");
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                                Log.d("AAA","Download vietnameseEnglishTranslator false!");
                            }
                        });
    }

    public void addAction(){
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEnglishToVietnamese){
                    txtIn.setText("Tiếng Việt");
                    txtOut.setText("English");
                    isEnglishToVietnamese = false;
                }else {
                    txtIn.setText("English");
                    txtOut.setText("Tiếng Việt");
                    isEnglishToVietnamese = true;
                }
            }
        });

        btnDich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = edtInput.getText().toString();
                if(isEnglishToVietnamese){
                    englishVietnameseTranslator.translate(value)
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@NonNull String s) {
                                    txtKetQua.setText(s);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    txtKetQua.setText(e.toString());
                                }
                            });
                }else{
                    vietnameseEnglishTranslator.translate(value)
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@NonNull String s) {
                                    txtKetQua.setText(s);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    txtKetQua.setText(e.toString());
                                }
                            });
                }
            }
        });
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edtInput.setText(result.get(0));
                }
                break;
            }
        }
    }
}