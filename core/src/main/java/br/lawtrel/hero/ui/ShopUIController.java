package br.lawtrel.hero.ui;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.items.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ShopUIController {

    private final Stage stage;
    private final Hero game;
    private final Skin skin;

    // Janelas principais
    private final Window optionsWindow;
    private final Window goldWindow;
    private final Window infoWindow;

    // Janelas de compra e venda (começam escondidas)
    private final Window buyWindow;
    private final Window sellWindow;

    // Componentes da UI
    private final Label infoLabel;
    private final Label goldLabel;
    private final List<Item> buyList;
    private final List<Item> sellList;

    private Runnable onCloseAction;

    private enum ShopState { NONE, BUYING, SELLING }
    private ShopState currentState = ShopState.NONE;

    public ShopUIController(Hero game, Skin skin) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = skin;

        // --- Criação dos Componentes ---
        infoLabel = new Label("Bem-vindo!", skin);
        goldLabel = new Label("OURO: 0", skin);
        buyList = new List<>(skin);
        sellList = new List<>(skin);

        // --- Criação das Janelas ---
        optionsWindow = createOptionsWindow();
        goldWindow = createGoldWindow();
        infoWindow = createInfoWindow();
        buyWindow = createListWindow("Comprar", buyList);
        sellWindow = createListWindow("Vender", sellList);

        // Adiciona ao palco
        stage.addActor(optionsWindow);
        stage.addActor(goldWindow);
        stage.addActor(infoWindow);
        stage.addActor(buyWindow);
        stage.addActor(sellWindow);

        setupListeners();
        setVisible(false);
    }

    private Window createOptionsWindow() {
        Window window = new Window("Loja", skin, "dialog");
        TextButton buyButton = new TextButton("Comprar", skin, "nes-style");
        TextButton sellButton = new TextButton("Vender", skin, "nes-style");
        TextButton exitButton = new TextButton("Sair", skin, "nes-style");

        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enterBuyMode();
            }
        });
        sellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enterSellMode();
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (onCloseAction != null) onCloseAction.run();
            }
        });

        Table table = new Table();
        table.add(buyButton).pad(5).row();
        table.add(sellButton).pad(5).row();
        table.add(exitButton).pad(5).row();
        window.add(table);
        window.pack();
        window.setPosition(40, 40);
        return window;
    }

    private Window createGoldWindow() {
        Window window = new Window("", skin, "dialog");
        window.add(goldLabel).pad(10);
        window.pack();
        window.setPosition(Gdx.graphics.getWidth() - window.getWidth() - 40, 40);
        return window;
    }

    private Window createInfoWindow() {
        Window window = new Window("Info", skin, "dialog");
        infoLabel.setWrap(true);
        window.add(infoLabel).width(300).pad(10);
        window.pack();
        window.setPosition(Gdx.graphics.getWidth() - window.getWidth() - 40, Gdx.graphics.getHeight() - window.getHeight() - 40);
        return window;
    }

    private Window createListWindow(String title, List<Item> list) {
        Window window = new Window(title, skin, "dialog");
        ScrollPane scrollPane = new ScrollPane(list, skin);
        scrollPane.setFadeScrollBars(false);
        window.add(scrollPane).width(250).height(200);
        window.setPosition(optionsWindow.getX() + optionsWindow.getWidth() + 20, 40);
        window.setVisible(false);
        return window;
    }

    private void setupListeners() {
        buyList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Item selected = buyList.getSelected();
                if (selected != null) {
                    infoLabel.setText(selected.getName() + "\n\n" + selected.getDescription() + "\n\nPreco: " + selected.getPrice());
                }
            }
        });
        sellList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Item selected = sellList.getSelected();
                if (selected != null) {
                    int sellPrice = Math.max(1, selected.getPrice() / 2); // Vende por metade do preço
                    infoLabel.setText(selected.getName() + "\n\n" + selected.getDescription() + "\n\nVender por: " + sellPrice);
                }
            }
        });
    }

    public void enterBuyMode() {
        currentState = ShopState.BUYING;
        buyWindow.setVisible(true);
        sellWindow.setVisible(false);
        infoLabel.setText("O que deseja comprar?");
    }

    public void enterSellMode() {
        currentState = ShopState.SELLING;
        // Atualiza a lista de venda com o inventário atual do jogador
        //sellList.setItems(new Array<>(game.getPlayer().getInventory()));
        sellWindow.setVisible(true);
        buyWindow.setVisible(false);
        infoLabel.setText("O que deseja vender?");
    }

    public void executeAction() {
        if (currentState == ShopState.BUYING) {
            Item itemToBuy = buyList.getSelected();
            if (itemToBuy != null && game.getPlayer().getMoney() >= itemToBuy.getPrice()) {
                game.getPlayer().spendMoney(itemToBuy.getPrice());
                game.getPlayer().addItem(itemToBuy);
                game.soundManager.playSound("purchase_sound"); // Adicione este som
            }
        } else if (currentState == ShopState.SELLING) {
            Item itemToSell = sellList.getSelected();
            if (itemToSell != null) {
                int sellPrice = Math.max(1, itemToSell.getPrice() / 2);
                game.getPlayer().getInventory().remove(itemToSell);
                game.getPlayer().addMoney(sellPrice);
                game.soundManager.playSound("purchase_sound");
                // Atualiza a lista de venda
                //sellList.setItems(new Array<>(game.getPlayer().getInventory()));
            }
        }
    }

    public void exitSubMenu() {
        currentState = ShopState.NONE;
        buyWindow.setVisible(false);
        sellWindow.setVisible(false);
        infoLabel.setText("Bem-vindo!");
    }

    public void populateShopInventory(Array<Item> items) {
        buyList.setItems(items);
    }

    public void setVisible(boolean visible) {
        optionsWindow.setVisible(visible);
        goldWindow.setVisible(visible);
        infoWindow.setVisible(visible);
        if (!visible) {
            exitSubMenu();
        }
    }

    public Stage getStage() { return stage; }
    public ShopState getCurrentState() { return currentState; }

    public void update(float delta) {
        goldLabel.setText("OURO: " + game.getPlayer().getMoney());
        stage.act(delta);
    }

    public void render() { stage.draw(); }
    public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    public void dispose() { stage.dispose(); }
    public void setOnCloseAction(Runnable action) { this.onCloseAction = action; }
}
