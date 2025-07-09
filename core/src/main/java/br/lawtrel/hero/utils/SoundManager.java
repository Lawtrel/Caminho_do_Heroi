package br.lawtrel.hero.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class SoundManager implements Disposable {

    private final ObjectMap<String, Sound> soundEffects;
    private final Array<String> victoryMusicPaths;
    private final Array<String> battleMusicPaths;
    private Music currentMusic;
    private float musicVolume = 0.5f; // Volume da música (0.0 a 1.0)
    private float sfxVolume = 0.8f;   // Volume dos efeitos sonoros (0.0 a 1.0)

    public SoundManager() {
        soundEffects = new ObjectMap<>();
        victoryMusicPaths = new Array<>();
        battleMusicPaths = new Array<>();
        // Pré-carrega todos os efeitos sonoros aqui
        loadSound("attack_hit","audio/sfx/attack_hit.wav");
        loadSound("menu_select", "audio/sfx/menu_select.wav");
        loadSound("menu_confirm", "audio/sfx/menu_confirm.wav");
        loadSound("magic_cast", "audio/sfx/magic_cast.wav");
        loadSound("purchase_sound", "audio/sfx/purchase_sound.wav");

        victoryMusicPaths.add("audio/music/victory.mp3");
        victoryMusicPaths.add("audio/music/victory_fanfare.mp3");
        victoryMusicPaths.add("audio/music/victory_fanfare2.mp3");
        victoryMusicPaths.add("audio/music/victory_fanfare3.mp3");

        battleMusicPaths.add("audio/music/battle_theme.mp3");
        battleMusicPaths.add("audio/music/battle.mp3");
        battleMusicPaths.add("audio/music/battle2.mp3");
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

    public void playVictoryMusic() {
        if (victoryMusicPaths.size == 0) {
            Gdx.app.log("SoundManager", "Nenhuma musica de vitoria para tocar.");
            return;
        }
        // Escolhe uma música aleatoriamente da lista
        String randomVictoryPath = victoryMusicPaths.random();
        // Toca a música escolhida, sem repetir (looping = false)
        playMusic(randomVictoryPath, false);
    }

    public void playBattleMusic() {
        if (battleMusicPaths.size == 0) {
            Gdx.app.log("SoundManager", "Nenhuma musica de vitoria para tocar.");
            return;
        }
        // Escolhe uma música aleatoriamente da lista
        String randomBattlePath = battleMusicPaths.random();
        // Toca a música escolhida, sem repetir (looping = false)
        playMusic(randomBattlePath, false);
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
