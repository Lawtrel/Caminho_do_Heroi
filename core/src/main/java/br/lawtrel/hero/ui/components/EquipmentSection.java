package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.items.Item;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EquipmentSection extends Table {

    public EquipmentSection(Hero game, Skin nesSkin) {
        super(nesSkin);

        pad(10);
        top().left();

        Label title = new Label("Equipamentos", nesSkin);
        add(title).left().colspan(2).padBottom(10).row();
        Player player = game.getPlayer();
        if (player == null) {
            add(new Label("Jogador não encontrado.", nesSkin)).left().colspan(2).row();
            return;
        }
        Item weapon = player.getEquippedWeapon();
        Item armor = player.getEquippedArmor();
        Item accessory = player.getEquippedAccessory();

        add(new Label("Arma:", nesSkin)).left().padRight(5);
        add(new Label(weapon != null ? weapon.getName() : "Nenhuma", nesSkin)).left().row();

        add(new Label("Armadura:", nesSkin)).left().padRight(5);
        add(new Label(armor != null ? armor.getName() : "Nenhuma", nesSkin)).left().row();

        add(new Label("Acessório:", nesSkin)).left().padRight(5);
        add(new Label(accessory != null ? accessory.getName() : "Nenhum", nesSkin)).left().row();

    }
}
