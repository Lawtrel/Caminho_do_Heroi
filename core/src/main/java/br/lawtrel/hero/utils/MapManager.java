package br.lawtrel.hero.utils;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.screens.castle.*;
import br.lawtrel.hero.screens.*;

public class MapManager {
    private Hero game;
    private MapType currentMapType;

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
        this.currentMapType = type;
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
    public String getCurrentMapId() {
        if (currentMapType == null) return "maps/word.tmx"; // Um mapa padrão

        switch (currentMapType) {
            case WORLD_MAP:
                return "maps/word.tmx";
            case VILLAGE:
                return "maps/vila.tmx";
            case SHOP:
                return "maps/shop.tmx";
            case CAVE:
                return "maps/cave.tmx";
            case CASTLE:
                return "maps/castle.tmx";
            case CASTLE_IN:
                return "maps/castleIn.tmx";
            case BOSSFIGHT:
                return "maps/bossFight.tmx";
            default:
                return "maps/word.tmx";
        }
    }
    public MapType getMapTypeFromId(String mapId) {
        if (mapId == null) return MapType.WORLD_MAP;
        switch (mapId) {
            case "maps/word.tmx": return MapType.WORLD_MAP;
            case "maps/vila.tmx": return MapType.VILLAGE;
            case "maps/shop.tmx": return MapType.SHOP;
            case "maps/cave.tmx": return MapType.CAVE;
            case "maps/castle.tmx": return MapType.CASTLE;
            case "maps/castleIn.tmx": return MapType.CASTLE_IN;
            case "maps/bossFight.tmx": return MapType.BOSSFIGHT;
            default: return MapType.WORLD_MAP;
        }
    }

}
