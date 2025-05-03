package br.lawtrel.hero;

import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Game;
import br.lawtrel.hero.screens.WorldMapScreen;

public class Hero extends Game {
    public MapManager mapManager;

    @Override
    public void create() {
        mapManager = new MapManager(this);
        mapManager.changeMap(MapManager.MapType.SHOP);
        //setScreen(new WorldMapScreen(this, this));
    }

    public Player getPlayer() {
    }
}
