package br.lawtrel.hero.utils;

import br.lawtrel.hero.Hero;
import com.badlogic.gdx.Game;
import br.lawtrel.hero.screens.*;

public class MapManager {
    private Hero game;

    public MapManager(Hero game) {
         this.game = game;
    }

    //Tipos de Mapas
    public enum MapType {
        WORLD_MAP,
        SHOP,
        VILLAGE,
        CAVE
    }

    // função para fazer a troca dos mapas
    public void changeMap(MapType type) {
        switch (type) {
            //aqui voce chama os mapas
            case WORLD_MAP:
                game.setScreen(new WorldMapScreen(game, this));
                break;
            case VILLAGE:
                game.setScreen(new VillageScreen(game, this));
                break;
            case SHOP:
                game.setScreen(new ShopScreen(game, this));
                break;

        }
    }
}
