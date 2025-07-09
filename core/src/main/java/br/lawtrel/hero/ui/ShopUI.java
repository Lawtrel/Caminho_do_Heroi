package br.lawtrel.hero.ui;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.items.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class ShopUI extends Window {

    private final Hero game;
    private final Player player;
    private final Skin skin;
    private Runnable onCloseAction;

    private final List<Item> shopItemsList;
    private final Label itemDescriptionLabel;
    private final Label playerMoneyLabel;
    private final TextButton buyButton;
    private final TextButton leaveButton;

    public ShopUI(Hero game, Skin skin) {
        super("Loja", skin, "dialog"); // Usando o estilo "dialog"
        this.game = game;
        this.player = game.getPlayer();
        this.skin = skin;

        // --- Layout ---
        setModal(true); // Bloqueia o input para o resto do jogo enquanto a loja estiver aberta
        setMovable(false);
        this.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setPosition(0, 0);

        // Painel Esquerdo: Lista de Itens
        shopItemsList = new List<>(skin);
        ScrollPane scrollPane = new ScrollPane(shopItemsList, skin);
        scrollPane.setFadeScrollBars(false);

        // Painel Direito: Detalhes e Ações
        Table detailsTable = new Table();
        itemDescriptionLabel = new Label("Selecione um item para ver a descrição.", skin);
        itemDescriptionLabel.setWrap(true);
        playerMoneyLabel = new Label("Ouro: " + player.getMoney(), skin);

        buyButton = new TextButton("Comprar", skin, "nes-style");
        leaveButton = new TextButton("Sair", skin, "nes-style");

        detailsTable.add(itemDescriptionLabel).width(250).height(60).align(Align.topLeft).colspan(2).row();
        detailsTable.add(playerMoneyLabel).colspan(2).align(Align.left).padTop(10).row();
        detailsTable.add(buyButton).pad(10);
        detailsTable.add(leaveButton).pad(10);

        // Adiciona os painéis à janela principal
        this.add(scrollPane).width(300).expandY().fillY().pad(10);
        this.add(detailsTable).expandX().fillX().pad(10);

        pack(); // Ajusta o tamanho da janela ao conteúdo
        setVisible(false);

        // --- Lógica dos Botões ---
        setupListeners();
    }
    public void setOnCloseAction(Runnable action) {
        this.onCloseAction = action;
    }

    private void setupListeners() {
        // Quando um item da lista é selecionado
        shopItemsList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateDetails();
            }
        });

        // Quando o botão "Comprar" é clicado
        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Item selectedItem = shopItemsList.getSelected();
                if (selectedItem != null) {
                    if (player.getMoney() >= selectedItem.getPrice()) {
                        player.spendMoney(selectedItem.getPrice());
                        player.addItem(selectedItem);
                        game.soundManager.playSound("purchase_sound"); // Adicione este som no seu SoundManager
                        updateDetails(); // Atualiza o dinheiro do jogador na UI
                    } else {
                        itemDescriptionLabel.setText("Ouro insuficiente!");
                    }
                }
            }
        });

        // Quando o botão "Sair" é clicado
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (onCloseAction != null) {
                    onCloseAction.run();
                }
            }
        });
    }

    // Atualiza o painel de detalhes com a informação do item selecionado
    private void updateDetails() {
        Item selectedItem = shopItemsList.getSelected();
        if (selectedItem != null) {
            itemDescriptionLabel.setText(selectedItem.getDescription() + "\nPreço: " + selectedItem.getPrice() + " Ouro");
        } else {
            itemDescriptionLabel.setText("Selecione um item.");
        }
        playerMoneyLabel.setText("Ouro: " + player.getMoney());
    }

    // Preenche a lista da loja com os itens
    public void populateShop(Array<Item> items) {
        shopItemsList.setItems(items);
        if (items.size > 0) {
            shopItemsList.setSelectedIndex(0);
        }
        updateDetails();
    }

    // Abre a loja
    public void openShop() {
        updateDetails();
        setVisible(true);
    }

    // Fecha a loja
    public void closeShop() {
        setVisible(false);
    }
}
