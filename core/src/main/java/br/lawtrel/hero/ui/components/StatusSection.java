package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Character;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class StatusSection extends Table {
    private final Hero game;

    public StatusSection(Hero game, Skin nesSkin) {
        super(nesSkin);
        this.game = game;
        pad(10); // Padding interno da StatusSection
        top().left();
        updateDisplay();
    }

    public void updateDisplay() {
        this.clearChildren(); // Limpa todo Conteudo antigo

        if (game.getPlayer() == null || game.getPlayer().getCharacter() == null) {
            add(new Label("Jogador não encontrado", getSkin()));
        }
        Character hero = game.getPlayer().getCharacter();
        Label title = new Label("Status", getSkin()); // Usa o nesSkin
        add(title).left().colspan(2).padBottom(10).row();


        add(new Label("Nome:", getSkin())).left();
        add(new Label(hero.getName(), getSkin())).left().padLeft(5).row();

        add(new Label("Nível:", getSkin())).left();
        add(new Label(String.valueOf(hero.getLevel()), getSkin())).left().padLeft(5).row();

        add(new Label("EXP:", getSkin())).left();
        add(new Label(hero.getExp() + " / " + hero.getExpToNextLevel(), getSkin())).left().padLeft(5).row();

        add(new Label("HP:", getSkin())).left();
        add(new Label(hero.getHp() + " / " + hero.getMaxHp(), getSkin())).left().padLeft(5).row();

        add(new Label("MP:", getSkin())).left();
        add(new Label(hero.getMp() + " / " + hero.getMaxMP(), getSkin())).left().padLeft(5).row();

        add(new Label("Ataque:", getSkin())).left();
        add(new Label(String.valueOf(hero.getAttack()), getSkin())).left().padLeft(5).row();

        add(new Label("Defesa:", getSkin())).left();
        add(new Label(String.valueOf(hero.getDefense()), getSkin())).left().padLeft(5).row();

        add(new Label("Atq. Mágico:", getSkin())).left();
        add(new Label(String.valueOf(hero.getMagicAttack()), getSkin())).left().padLeft(5).row();

        add(new Label("Def. Mágica:", getSkin())).left();
        add(new Label(String.valueOf(hero.getMagicDefense()), getSkin())).left().padLeft(5).row();

        add(new Label("Velocidade:", getSkin())).left();
        add(new Label(String.valueOf(hero.getSpeed()), getSkin())).left().padLeft(5).row();

        add(new Label("Sorte:", getSkin())).left();
        add(new Label(String.valueOf(hero.getLuck()), getSkin())).left().pad(5).row();

    }
}
