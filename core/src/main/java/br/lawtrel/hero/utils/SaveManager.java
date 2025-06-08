package br.lawtrel.hero.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class SaveManager {

    private static final String SAVE_FILE = "savegame.json";
    private static final Json json = new Json();
    //Salva o estado do jogo em um arquivo JSON.
    public static void saveGame(PlayerState state) {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE); // Arquivo na pasta local de armazenamento
            json.setOutputType(JsonWriter.OutputType.json); // Formato JSON legível
            String saveData = json.prettyPrint(state); // Converte o objeto para uma string JSON formatada
            file.writeString(saveData, false); // 'false' para sobrescrever o arquivo
            Gdx.app.log("SaveManager", "Jogo salvo com sucesso em: " + file.path());
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Erro ao salvar o jogo.", e);
        }
    }
    //Carrega o estado do jogo a partir de um arquivo JSON.
    public static PlayerState loadGame() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (file.exists()) {
                String saveData = file.readString();
                if (saveData.isEmpty()) {
                    Gdx.app.log("SaveManager", "Arquivo de save encontrado, mas está vazio.");
                    return null;
                }
                PlayerState state = json.fromJson(PlayerState.class, saveData);
                Gdx.app.log("SaveManager", "Jogo carregado com sucesso de: " + file.path());
                return state;
            } else {
                Gdx.app.log("SaveManager", "Nenhum arquivo de save encontrado.");
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Erro ao carregar o jogo.", e);
        }
        return null;
    }
    //verificar se existe algum save
    public static boolean doesSaveExist() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        return file.exists() && file.length() > 0;
    }

}
