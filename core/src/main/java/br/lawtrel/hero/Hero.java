package br.lawtrel.hero;

import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.screens.BattleTestScreen;
import br.lawtrel.hero.ui.menu.PauseMenuScreen;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class Hero extends Game {
    public MapManager mapManager;
    private Player player;
    private Screen screenBeforePause;

    @Override
    public void create() {
            setScreen(new BattleTestScreen());
            //mapManager = new MapManager(this);
           // mapManager.changeMap(MapManager.MapType.WORLD_MAP);

    }
    public void setPlayer(Player player) {
        this.player = player;
        if (this.player != null) {
            Gdx.app.log("Hero", "Instância do Jogador definida: " + player.getCharacter().getName());
        } else {
            Gdx.app.log("Hero", "Instância do Jogador definida como null.");
        }
    }

    public Player getPlayer() {
        if (this.player == null) {
            Gdx.app.log("Hero_getPlayer", "AVISO: getPlayer() chamado, mas a instância do jogador é nula.");
        }
        return player;
    }

    public void pauseGame() {
        Screen currentActiveScreen = getScreen();
        if (currentActiveScreen != null && !(currentActiveScreen instanceof PauseMenuScreen)) {
            this.screenBeforePause = currentActiveScreen; // Guarda a tela que está sendo pausada
            super.setScreen(new PauseMenuScreen(this)); // Usa super.setScreen para não interferir com screenBeforePause
            Gdx.app.log("Hero", "Jogo pausado. Tela anterior: " + screenBeforePause.getClass().getSimpleName());
        } else if (currentActiveScreen != null) {
            Gdx.app.log("Hero", "O jogo já está pausado.");
        } else {
            Gdx.app.log("Hero", "Nenhuma tela ativa para pausar.");
        }
    }

    public void resumeGame() {
        if (this.screenBeforePause != null) {
            Gdx.app.log("Hero", "Resumindo para: " + screenBeforePause.getClass().getSimpleName());
            super.setScreen(this.screenBeforePause); // Usa super.setScreen para restaurar diretamente
            this.screenBeforePause = null; // Limpa a referência após resumir
        } else {
            Gdx.app.log("Hero", "Nenhuma tela para resumir. Voltando para o Mapa Mundial como fallback.");
            // se não houver tela para resumir, volta ao mapa mundial
            if (mapManager != null) { // Verifica se mapManager está inicializado
                mapManager.changeMap(MapManager.MapType.WORLD_MAP);
            } else {
                Gdx.app.error("Hero", "MapManager é nulo, não é possível voltar ao mapa mundial como fallback.");
            }
        }
    }

        @Override
        public void setScreen(Screen screen) {
            Gdx.app.log("Hero_setScreen", "Trocando para tela: " + (screen != null ? screen.getClass().getSimpleName() : "null"));
            super.setScreen(screen);
        }
}
