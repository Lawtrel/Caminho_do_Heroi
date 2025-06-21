package br.lawtrel.hero.utils;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.screens.castle.*;
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
        CAVE,
        CASTLE,
        CASTLE_IN,
        BOSSFIGHT
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
            case CAVE:
                game.setScreen(new CaveScreen(game, this));
                break;
            case CASTLE:
                game.setScreen(new CastleScreen(game, this));
                break;
            case CASTLE_IN:
                game.setScreen(new CastleInternScreen(game, this));
                break;
            case BOSSFIGHT:
                game.setScreen(new BossFightScreen(game, this));
                break;

        }
    }
}
