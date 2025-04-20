package br.lawtrel.hero.screens;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.ScreenAdapter;

public class VillageScreen extends ScreenAdapter {

    private final Hero game;
    private MapManager  mapManager ;

    public VillageScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager = mapManager;
    }
}
