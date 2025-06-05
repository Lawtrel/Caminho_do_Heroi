package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.items.Item;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Array;

public class ItemsSection extends Table {
    private final Player player;
    private final Skin nesSkin; // Armazena o skin recebido
    private final List<String> itemListWidget;
    private final ScrollPane scrollPane;

    public ItemsSection(Hero game, Skin nesSkin) {
        super(nesSkin);
        this.player = game.getPlayer();
        this.nesSkin = nesSkin;

        pad(10);
        top().left();
        this.itemListWidget = new List<>(nesSkin); // Usa o nesSkin para o ListStyle
        this.scrollPane = new ScrollPane(itemListWidget, nesSkin); // Usa o nesSkin para o ScrollPaneStyle
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
    }
    public void updateItemsDisplay() {
        clearChildren();
        if (player == null) {
            add(new Label("Jogador não encontrado.", nesSkin)).left().row();
            return;
        }

        Label title = new Label("Inventário (" + player.getInventory().size() + " itens)", nesSkin);
        add(title).left().padBottom(5).row();

        java.util.List<Item> playerItems = player.getInventory();

        if (playerItems == null || playerItems.isEmpty()) {
            add(new Label("Nenhum item no inventário.", nesSkin)).left().row();
        } else {
            // Converte List<Item> para Array<String> para o widget List
            Array<String> itemNames = new Array<>();
            for (Item item : playerItems) {
                itemNames.add(item.getName() + " (Tipo: " + item.getType() + ")"); // Ou apenas item.getName()
            }
            itemListWidget.setItems(itemNames); // Define os itens no widget List

            // Adiciona o ScrollPane (que contém a itemListWidget) à ItemsSection (que é uma Table)
            add(scrollPane).height(150).width(250).left().expandY().fillY(); // expandY e fillY para o scroll
        }
    }
}
