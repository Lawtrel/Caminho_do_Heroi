package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Character;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class StatusSection extends Table {

    public StatusSection(Hero game, Skin nesSkin) {
        super(nesSkin);

        pad(10); // Padding interno da StatusSection
        top().left();

        Label title = new Label("Status do Herói", nesSkin); // Usa o nesSkin
        add(title).left().colspan(2).padBottom(10).row();

        Character hero = game.getPlayer().getCharacter();
        add(new Label("Nome:", nesSkin)).left();
        add(new Label(hero.getName(), nesSkin)).left().padLeft(5).row();

        add(new Label("Nível:", nesSkin)).left();
        add(new Label(String.valueOf(hero.getLevel()), nesSkin)).left().padLeft(5).row();

        add(new Label("EXP:", nesSkin)).left();
        add(new Label(hero.getExp() + " / " + hero.getExpToNextLevel(), nesSkin)).left().padLeft(5).row();

        add(new Label("HP:", nesSkin)).left();
        add(new Label(hero.getHp() + " / " + hero.getMaxHp(), nesSkin)).left().padLeft(5).row();

        add(new Label("MP:", nesSkin)).left();
        add(new Label(hero.getMp() + " / " + hero.getMaxMP(), nesSkin)).left().padLeft(5).row();

        add(new Label("Ataque:", nesSkin)).left();
        add(new Label(String.valueOf(hero.getAttack()), nesSkin)).left().padLeft(5).row();

        add(new Label("Defesa:", nesSkin)).left();
        add(new Label(String.valueOf(hero.getDefense()), nesSkin)).left().padLeft(5).row();

        add(new Label("Atq. Mágico:", nesSkin)).left();
        add(new Label(String.valueOf(hero.getMagicAttack()), nesSkin)).left().padLeft(5).row();

        add(new Label("Def. Mágica:", nesSkin)).left();
        add(new Label(String.valueOf(hero.getMagicDefense()), nesSkin)).left().padLeft(5).row();

        add(new Label("Velocidade:", nesSkin)).left();
        add(new Label(String.valueOf(hero.getSpeed()), nesSkin)).left().padLeft(5).row();

    }
}
