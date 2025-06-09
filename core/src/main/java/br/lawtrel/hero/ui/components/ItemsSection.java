package br.lawtrel.hero.ui.components;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.items.Item;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class ItemsSection extends Table {
    private final Hero game;
    private final Player player;
    private final List<Item> itemListWidget;
    private final Table detailsTable;

    // Referências para as seções "irmãs" para que possamos atualizá-las
    private StatusSection statusSection;
    private EquipmentSection equipmentSection;

    public ItemsSection(Hero game, Skin skin) {
        super(skin);
        this.game = game;
        this.player = game.getPlayer();

        // --- Layout Principal da Seção de Itens ---
        // Coluna da Esquerda: Lista de Itens
        itemListWidget = new List<>(skin);
        ScrollPane scrollPane = new ScrollPane(itemListWidget, skin);
        scrollPane.setFadeScrollBars(false);

        // Coluna da Direita: Painel de Detalhes e Ações
        detailsTable = new Table(skin);
        detailsTable.top().left().padLeft(10);

        // Adiciona as duas colunas à tabela principal da ItemsSection
        this.add(scrollPane).width(250).expandY().fillY();
        this.add(detailsTable).expand().fill();

        // --- Listener de Eventos ---
        // Este listener é ativado sempre que um item diferente é selecionado na lista
        itemListWidget.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Item selectedItem = itemListWidget.getSelected();
                updateDetailsPanel(selectedItem);
            }
        });

        updateItemsDisplay(); // Carrega os itens na lista pela primeira vez
    }

    // Método para conectar esta seção com as outras para permitir a atualização
    public void setSiblingSections(StatusSection statusSection, EquipmentSection equipmentSection) {
        this.statusSection = statusSection;
        this.equipmentSection = equipmentSection;
    }

    // Atualiza o conteúdo da lista de itens
    public void updateItemsDisplay() {
        if (player == null) return;

        // Guarda o item que estava selecionado antes de atualizar
        Item previouslySelectedItem = itemListWidget.getSelected();

        // Converte o inventário do jogador para o formato que o widget de lista aceita
        Array<Item> playerItems = new Array<>();
        for (Item item : player.getInventory()) {
            playerItems.add(item);
        }
        itemListWidget.setItems(playerItems);

        // Tenta manter a seleção após a atualização
        if (playerItems.contains(previouslySelectedItem, false)) {
            itemListWidget.setSelected(previouslySelectedItem);
        }

        updateDetailsPanel(itemListWidget.getSelected());
    }

    // Atualiza o painel da direita com detalhes e botões do item selecionado
    private void updateDetailsPanel(final Item selectedItem) {
        detailsTable.clearChildren(); // Limpa o painel de detalhes
        if (selectedItem == null) {
            detailsTable.add(new Label("Selecione um item", getSkin()));
            return;
        }

        // Exibe nome e descrição
        Label nameLabel = new Label(selectedItem.getName(), getSkin());
        nameLabel.setWrap(true);
        detailsTable.add(nameLabel).width(180).align(Align.left).row();

        Label descLabel = new Label(selectedItem.getDescription(), getSkin());
        descLabel.setWrap(true);
        detailsTable.add(descLabel).width(180).align(Align.left).padTop(10).row();

        detailsTable.add().height(20).row(); // Espaçador

        // Cria botões de ação baseados no tipo do item
        if (selectedItem.getType() == Item.Type.CONSUMABLE) {
            TextButton useButton = new TextButton("Usar", getSkin(), "nes-style");
            detailsTable.add(useButton).width(150).height(30);
            useButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    player.useItem(selectedItem);
                    updateAllPanels(); // Atualiza toda a UI do menu
                }
            });
        } else if (selectedItem.getType() == Item.Type.WEAPON || selectedItem.getType() == Item.Type.ARMOR || selectedItem.getType() == Item.Type.ACCESSORY) {
            TextButton equipButton = new TextButton("Equipar", getSkin(), "nes-style");
            detailsTable.add(equipButton).width(150).height(30);
            equipButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    player.equip(selectedItem);
                    updateAllPanels(); // Atualiza toda a UI do menu
                }
            });
        }
    }

    // Chama os métodos de atualização de todas as seções relevantes
    private void updateAllPanels() {
        updateItemsDisplay();
        if (statusSection != null) {
            statusSection.updateDisplay();
        }
        if (equipmentSection != null) {
            equipmentSection.updateDisplay();
        }
    }
}
