package br.lawtrel.hero;

import com.badlogic.gdx.Game;
import br.lawtrel.hero.screens.WorldMapScreen;

public class Hero extends Game {

    @Override
    public void create() {
        setScreen(new WorldMapScreen(this));
    }
}
