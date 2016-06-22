package com.epifania.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Locale;
/**
 * Created by juan on 5/29/16.
 */
public class Settings {

    public static final Settings instance = new Settings();

    private ObjectMap<String,String> languajes;
    private Preferences preferences;
    public boolean setted = false;
    public boolean controls = false; //False = Buttons ; true = touch
    public String languaje = Locale.getDefault().getLanguage();
    public float sfxVolume =0.8f;
    public float musicVolume = 1;

    private Settings(){
        languajes = new ObjectMap<String, String>();
        languajes.put("en","ENGLISH");
        languajes.put("es","ESPAÑOL");
        Gdx.app.debug("Settings","Locale languaje = "+Locale.getDefault().getLanguage());
    }
    public void loadSettings(){
        preferences = Gdx.app.getPreferences("settings");

        setted = preferences.getBoolean("setted");
        sfxVolume = preferences.getFloat("sfxVolume");
        musicVolume = preferences.getFloat("musicVolume");
        controls = preferences.getBoolean("controls");
        languaje = preferences.getString("languaje");
        if(setted == false) {
            sfxVolume = 0.8f;
            musicVolume = 1f;
            languaje = Locale.getDefault().getLanguage();
        }

        Gdx.app.debug("Settings","sfxVolume = "+sfxVolume);
        Gdx.app.debug("Settings","musicVolume = "+musicVolume);
        Gdx.app.debug("Settings","languaje = "+languaje);
        Gdx.app.debug("Settings","setted = "+setted);
        Gdx.app.debug("Settings","controls = "+controls);
        Locale.setDefault(new Locale(languaje));
    }
    public void saveSettings(){
        preferences.putFloat("sfxVolume",sfxVolume);
        preferences.putFloat("musicVolume",musicVolume);
        preferences.putBoolean("setted",setted);
        preferences.putBoolean("controls",controls);
        preferences.putString("languaje",languaje);
        preferences.flush();
    }
}
