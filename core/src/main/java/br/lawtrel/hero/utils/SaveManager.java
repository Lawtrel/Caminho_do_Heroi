package br.lawtrel.hero.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class SaveManager {

    private static final String PREFERENCES_NAME = "CaminhoDoHeroi_SaveState";
    //private static final String SAVE_FILE = "savegame.json";
    private static final Json json = new Json();
    //Salva o estado do jogo em um arquivo JSON.
    public static void saveGame(PlayerState state) {
        try {
            Preferences prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
            String saveData = json.toJson(state); // Converte o objeto para uma string JSON

            prefs.putString("gameState", saveData); // Salva a string JSON com a chave "gameState"
            prefs.flush(); // Grava as alterações no disco/LocalStorage

            Gdx.app.log("SaveManager", "Jogo salvo com sucesso nas Preferences.");
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Erro ao salvar o jogo.", e);
        }
    }
    //Carrega o estado do jogo a partir de um arquivo JSON.
    public static PlayerState loadGame() {
        try {
            Preferences prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
            // Pega a string salva com a chave "gameState". Se não existir, retorna null.
            String saveData = prefs.getString("gameState", null);

            if (saveData != null && !saveData.isEmpty()) {
                PlayerState state = json.fromJson(PlayerState.class, saveData);
                Gdx.app.log("SaveManager", "Jogo carregado com sucesso das Preferences.");
                return state;
            } else {
                Gdx.app.log("SaveManager", "Nenhum save encontrado nas Preferences.");
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Erro ao carregar o jogo.", e);
        }
        return null;
    }
    //verificar se existe algum save
    public static boolean doesSaveExist() {
        Preferences prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
        return prefs.contains("gameState");
    }

}
