package com.example.a41396969.burguersgame;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.cocos2d.opengl.CCGLSurfaceView;

public class ActividadPrincipal extends Activity {

    CCGLSurfaceView VistaPrincipal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        VistaPrincipal = new CCGLSurfaceView(this);
        setContentView(VistaPrincipal);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("onStart","Declaro e instancio la clase juego");
        clase_Juego miJuego;
        miJuego = new clase_Juego(VistaPrincipal);
        miJuego.ComenzarJuego();
    }
}
