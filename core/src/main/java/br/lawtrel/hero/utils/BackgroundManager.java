package br.lawtrel.hero.utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;

public class BackgroundManager {
    private final HashMap<String, Texture> backgrounds;
    private Texture currentBackground;

    public BackgroundManager() {
        backgrounds = new HashMap<>();
        loadDefaultBackgrounds();
    }
    private void loadDefaultBackgrounds() {
        // Pré-carrega os backgrounds básicos
        addBackground("floresta", "bg/bg_00.png");
        addBackground("caverna", "bg/bg_01.png");
        addBackground("castelo", "bg/bg_01.png");

        currentBackground = createSolidBackground(Color.BLACK);
    }

    public void addBackground(String areaName, String path) {
        try {
            Texture bg = new Texture(Gdx.files.internal(path));
            backgrounds.put(areaName, bg);
        } catch (Exception e) {
            Gdx.app.error("BackgroundManager", "Erro ao carregar: " + path);
        }
    }
    public void setCurrentArea(String areaName) {
        Texture newBg = backgrounds.get(areaName);
        if (newBg != null) {
            if (currentBackground != null && currentBackground != createSolidBackground(Color.DARK_GRAY)) {
                currentBackground.dispose();
            }
            currentBackground = newBg;
        }
    }
    public Texture getCurrentBackground() {
        return currentBackground;
    }

    private Texture createSolidBackground(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void dispose() {
        for (Texture texture : backgrounds.values()) {
            texture.dispose();
        }
        if (currentBackground != null) {
            currentBackground.dispose();
        }
    }
}
