package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.items.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EquipmentSection extends Table {
    private final Hero game;

    public EquipmentSection(Hero game, Skin nesSkin) {
        super(nesSkin);
        this.game = game;
        pad(10);
        top().left();

        updateDisplay();
    }
    public void updateDisplay() {
        this.clearChildren(); // Limpa todo conteudo

        if (game.getPlayer() == null) {
            add(new Label("Jogador Nao Encontrado", getSkin()));
            return;
        }
        Player player = game.getPlayer();

        Label title = new Label("Equipamentos", getSkin());
        add(title).left().colspan(2).padBottom(10).row();

        Item weapon = player.getEquippedWeapon();
        Item armor = player.getEquippedArmor();
        Item accessory = player.getEquippedAccessory();

        add(new Label("Arma:", getSkin())).left().padRight(5);
        add(new Label(weapon != null ? weapon.getName() : "Nenhuma", getSkin())).left().row();

        add(new Label("Armadura:", getSkin())).left().padRight(5);
        add(new Label(armor != null ? armor.getName() : "Nenhuma", getSkin())).left().row();

        add(new Label("Acessório:", getSkin())).left().padRight(5);
        add(new Label(accessory != null ? accessory.getName() : "Nenhum", getSkin())).left().row();

    }
}
