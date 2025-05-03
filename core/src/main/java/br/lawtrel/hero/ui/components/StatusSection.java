package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Character;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class StatusSection extends Table {
    private Hero game;
    private Skin skin;

    public StatusSection(Hero game) {
        this.game = game;

        this.skin = new Skin(game.assets.get("skins/uiskin.json")); // ajuste o caminho se necessário

        setBackground(skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.8f));
        pad(10);
        top().left();

        Label title = new Label("Status do Herói", skin);
        add(title).left().row();

        Character hero = game.getPlayer().getCharacter(); // Assumindo que getPlayer() retorna um objeto que tem um Character

        add(new Label("Nome: " + hero.getName(), skin)).left().row();
        add(new Label("HP: " + hero.getHp() + " / " + hero.getMaxHp(), skin)).left().row();
        add(new Label("MP: " + hero.getMp() + " / " + hero.getMaxMP(), skin)).left().row();
        add(new Label("Ataque: " + hero.getAttack(), skin)).left().row();
        add(new Label("Defesa: " + hero.getDefense(), skin)).left().row();
        add(new Label("Status: " + (hero.isAlive() ? "Vivo" : "Derrotado"), skin)).left().row();
    }
}
