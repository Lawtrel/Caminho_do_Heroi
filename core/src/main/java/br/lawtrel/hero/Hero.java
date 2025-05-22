package br.lawtrel.hero;

import br.lawtrel.hero.entities.Character;
import br.lawtrel.hero.entities.CharacterBuilder;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.PlayerBuilder;
import br.lawtrel.hero.screens.BattleTestScreen;
import br.lawtrel.hero.ui.menu.PauseMenuScreen;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Game;
import br.lawtrel.hero.screens.WorldMapScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class Hero extends Game {
    public MapManager mapManager;
    private Player player;

    @Override
    public void create() {
            //setScreen(new BattleTestScreen());
            mapManager = new MapManager(this);

            mapManager.changeMap(MapManager.MapType.WORLD_MAP);

    }

    public Player getPlayer() {
        return player;
    }


}
