package br.lawtrel.hero;

import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.screens.BattleTestScreen;
import br.lawtrel.hero.ui.menu.PauseMenuScreen;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Game;
import br.lawtrel.hero.screens.WorldMapScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class Hero extends Game {
    public MapManager mapManager;

    @Override
    public void create() {
        try {
            Gdx.app.log("Hero", "Carregando");
            setScreen(new BattleTestScreen());
            //mapManager = new MapManager(this);

            //mapManager.changeMap(MapManager.MapType.WORLD_MAP);
            //setScreen(new WorldMapScreen(this, this));
        } catch (Exception e) {
            Gdx.app.log("Hero", "Error ao carregar", e);
            throw e;
        }

    }

    public Player getPlayer() {
        return null;
    }
}
