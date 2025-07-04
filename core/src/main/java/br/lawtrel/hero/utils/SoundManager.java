package br.lawtrel.hero.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class SoundManager implements Disposable {

    private final ObjectMap<String, Sound> soundEffects;
    private Music currentMusic;
    private float musicVolume = 0.5f; // Volume da música (0.0 a 1.0)
    private float sfxVolume = 0.8f;   // Volume dos efeitos sonoros (0.0 a 1.0)

    public SoundManager() {
        soundEffects = new ObjectMap<>();
        // Pré-carrega todos os efeitos sonoros aqui
        loadSound("attack_hit", "audio/sfx/attack_hit.wav");
        loadSound("menu_select", "audio/sfx/menu_select.wav");
        loadSound("victory_fanfare", "audio/sfx/victory.mp3");
        // Adicione outros efeitos sonoros aqui...
    }

    private void loadSound(String key, String path) {
        try {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            soundEffects.put(key, sound);
        } catch (Exception e) {
            Gdx.app.error("SoundManager", "Nao foi possivel carregar o som: " + path, e);
        }
    }

    public void playSound(String key) {
        Sound sound = soundEffects.get(key);
        if (sound != null) {
            sound.play(sfxVolume);
        }
    }

    public void playMusic(String path, boolean looping) {
        // Para a música atual se estiver a tocar
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        try {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
            currentMusic.setVolume(musicVolume);
            currentMusic.setLooping(looping);
            currentMusic.play();
        } catch (Exception e) {
            Gdx.app.error("SoundManager", "Nao foi possivel carregar a musica: " + path, e);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(this.musicVolume);
        }
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    @Override
    public void dispose() {
        // Liberta todos os recursos de áudio
        if (currentMusic != null) {
            currentMusic.dispose();
        }
        for (Sound sound : soundEffects.values()) {
            sound.dispose();
        }
        soundEffects.clear();
        Gdx.app.log("SoundManager", "Recursos de audio libertados.");
    }
}
