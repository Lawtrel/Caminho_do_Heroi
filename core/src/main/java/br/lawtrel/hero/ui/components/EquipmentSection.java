package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class EquipmentSection extends Table {
    private Hero game;
    private Skin skin;

    public EquipmentSection(Hero game) {
        this.game = game;
        this.skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        setBackground(skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 0.8f));
        pad(10);
        top().left();

        Label title = new Label("Equipamentos", skin);
        add(title).left().row();

        Player player = game.getPlayer();

        add(new Label("Arma: " + (player.getEquippedWeapon() != null ? player.getEquippedWeapon() : "Nenhuma"), skin)).left().row();
        add(new Label("Armadura: " + (player.getEquippedArmor() != null ? player.getEquippedArmor() : "Nenhuma"), skin)).left().row();
        add(new Label("Acessório: " + (player.getEquippedAccessory() != null ? player.getEquippedAccessory() : "Nenhum"), skin)).left().row();
    }
}
