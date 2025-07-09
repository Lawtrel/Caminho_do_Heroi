package br.lawtrel.hero.screens;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.NPC;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.items.Item;
import br.lawtrel.hero.entities.items.ItemFactory;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.List;

public class ShopScreen extends ScreenAdapter implements InputProcessor {

    private final Hero game;
    private final MapManager mapManager;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final Stage stage;
    private final Skin skin;

    // UI da Loja
    private final Window shopWindow;
    private final com.badlogic.gdx.scenes.scene2d.ui.List<Item> buyList;
    private final com.badlogic.gdx.scenes.scene2d.ui.List<Item> sellList;
    private final Label infoLabel;
    private final Label goldLabel;
    private final TextButton actionButton;

    private final Array<NPC> npcs;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Player player;
    private Viewport viewport;
    private boolean isShopOpen = false;

    private enum ShopMode {BUY, SELL}

    private ShopMode currentMode = ShopMode.BUY;


    private final String MAP_ID = "maps/shop.tmx";
    private final String MAP_THEME_MUSIC = "audio/music/village_map.mp3";
    private final String SHOP_THEME_MUSIC = "audio/music/shop_theme.mp3";

    private final int MAP_WIDTH_PIXELS = 9 * 32;
    private final int MAP_HEIGHT_PIXELS = 11 * 32;
    private static final float WORLD_WIDTH = 400;
    private static final float WORLD_HEIGHT = 240;

    public ShopScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager = mapManager;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.npcs = new Array<>();
        this.skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        this.stage = new Stage(new ScreenViewport());

        // --- Criação da UI da Loja ---
        shopWindow = new Window("Loja", skin, "dialog");
        buyList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
        sellList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
        infoLabel = new Label("Bem-vindo!", skin);
        goldLabel = new Label("Ouro: 0", skin);
        actionButton = new TextButton("Comprar (Z)", skin, "nes-style"); // Inicialização corrigida
        setupShopWindow();
    }

    private void setupShopWindow() {
        infoLabel.setWrap(true);
        goldLabel.setAlignment(Align.left);

        TextButton buyTabButton = new TextButton("Comprar", skin, "nes-tab");
        TextButton sellTabButton = new TextButton("Vender", skin, "nes-tab");
        TextButton exitButton = new TextButton("Sair (X)", skin, "nes-style");

        ButtonGroup<TextButton> tabs = new ButtonGroup<>(buyTabButton, sellTabButton);
        tabs.setMaxCheckCount(1);
        tabs.setMinCheckCount(1);
        buyTabButton.setChecked(true);

        Table detailsTable = new Table();
        detailsTable.add(infoLabel).width(250).height(80).align(Align.topLeft).colspan(2).row();
        detailsTable.add(goldLabel).colspan(2).align(Align.left).padTop(10).row();
        detailsTable.add(actionButton).pad(10);
        detailsTable.add(exitButton).pad(10);

        ScrollPane buyScrollPane = new ScrollPane(buyList, skin);
        ScrollPane sellScrollPane = new ScrollPane(sellList, skin);
        buyScrollPane.setFadeScrollBars(false);
        sellScrollPane.setFadeScrollBars(false);
        sellScrollPane.setVisible(false); // Começa escondido

        Table listContainer = new Table();
        listContainer.stack(buyScrollPane, sellScrollPane);

        Table mainTable = new Table();
        mainTable.add(buyTabButton).pad(5);
        mainTable.add(sellTabButton).pad(5).row();
        mainTable.add(listContainer).colspan(2).width(280).expandY().fillY().pad(5);

        shopWindow.add(mainTable);
        shopWindow.add(detailsTable).expandX().fillX().pad(5);
        shopWindow.pack();
        shopWindow.setPosition(
            (Gdx.graphics.getWidth() - shopWindow.getWidth()) / 2,
            (Gdx.graphics.getHeight() - shopWindow.getHeight()) / 2
        );
        shopWindow.setVisible(false);
        stage.addActor(shopWindow);

        // --- Listeners ---
        buyTabButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    game.soundManager.playSound("menu_select");
                    setMode(ShopMode.BUY);
                    buyScrollPane.setVisible(true);
                    sellScrollPane.setVisible(false);
                }
            }
        });
        sellTabButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    game.soundManager.playSound("menu_select");
                    setMode(ShopMode.SELL);
                    sellScrollPane.setVisible(true);
                    buyScrollPane.setVisible(false);
                }
            }
        });

        buyList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                game.soundManager.playSound("menu_select");
                updateInfo();
            }
        });
        sellList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                game.soundManager.playSound("menu_select");
                updateInfo();
            }
        });
        actionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                executeAction();
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                closeShop();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        game.soundManager.playMusic(MAP_THEME_MUSIC, true);

        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        map = new TmxMapLoader().load(MAP_ID);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        this.player = game.getPlayer();
        if (player == null) {
            Gdx.app.error("ShopScreen", "Jogador nulo. A voltar para o menu.");
            game.setScreen(new MainMenuScreen(game));
            return;
        }
        player.setScale(0.5f);
        player.setInBattleView(false);

        Vector2 spawnPoint = findSpawnPoint(map, "spawn_from_vila");
        player.setPosition(spawnPoint.x, spawnPoint.y);
        loadNpcsAndShopInventory();
    }

    private void setMode(ShopMode mode) {
        this.currentMode = mode;
        if (mode == ShopMode.BUY) {
            actionButton.setText("Comprar (Z)");
            sellList.getSelection().clear(); // Limpa a seleção da outra lista
        } else {
            actionButton.setText("Vender (Z)");
            buyList.getSelection().clear();
            // Atualiza a lista de venda com o inventário atual do jogador
            sellList.setItems(new Array<>(player.getInventory().toArray(new Item[0])));

        }
        updateInfo();
    }

    private void executeAction() {
        if (currentMode == ShopMode.BUY) {
            buySelectedItem();
        } else {
            sellSelectedItem();
        }
    }

    private void sellSelectedItem() {
        Item selectedItem = sellList.getSelected();
        if (selectedItem != null) {
            int sellPrice = Math.max(1, selectedItem.getPrice() / 2); // Vende por metade do preço
            player.addMoney(sellPrice);
            player.removeItem(selectedItem);
            game.soundManager.playSound("purchase_sound");
            sellList.setItems(new Array<>(player.getInventory().toArray(new Item[0])));
            updateInfo();
        }
    }

    @Override
    public void hide() {
        game.soundManager.stopMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isShopOpen) {
            // Lógica de movimento do jogador
            boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            player.update(delta, up, down, left, right);

            float oldPlayerX = player.getX();
            float oldPlayerY = player.getY();
            checkMapObjectCollisions(oldPlayerX, oldPlayerY);
        }

        camera.position.x = MathUtils.clamp(player.getX(), viewport.getWorldWidth() / 2f, MAP_WIDTH_PIXELS - viewport.getWorldWidth() / 2f);
        camera.position.y = MathUtils.clamp(player.getY(), viewport.getWorldHeight() / 2f, MAP_HEIGHT_PIXELS - viewport.getWorldHeight() / 2f);
        camera.update();
        viewport.apply();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch, player.getX(), player.getY());
        for (NPC npc : npcs) {
            npc.render(batch);
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (isShopOpen) {
            if (keycode == Input.Keys.X || keycode == Input.Keys.ESCAPE) {
                closeShop();
                return true;
            }
            if (keycode == Input.Keys.Z) {
                buySelectedItem();
                return true;
            }
            return stage.keyDown(keycode);
        }

        if (keycode == Input.Keys.Z) {
            checkForNpcInteraction();
            return true;
        }

        if (keycode == Input.Keys.ESCAPE) {
            game.pauseGame();
            return true;
        }

        return false;
    }

    private void openShop() {
        isShopOpen = true;
        shopWindow.setVisible(true);
        setMode(ShopMode.BUY);
        updateInfo();
        game.soundManager.playMusic(SHOP_THEME_MUSIC, true);
    }

    private void closeShop() {
        isShopOpen = false;
        shopWindow.setVisible(false);
        game.soundManager.playMusic(MAP_THEME_MUSIC, true);
    }

    private void buySelectedItem() {
        Item selectedItem = buyList.getSelected();
        if (selectedItem != null) {
            if (player.getMoney() >= selectedItem.getPrice()) {
                player.spendMoney(selectedItem.getPrice());
                player.addItem(ItemFactory.createItem(selectedItem.getId()));
                game.soundManager.playSound("purchase_sound");
                updateInfo();
            } else {
                infoLabel.setText("Ouro insuficiente!");
            }
        }
    }
    private void updateInfo() {
        Item selectedItem = (currentMode == ShopMode.BUY) ? buyList.getSelected() : sellList.getSelected();
        if (selectedItem != null) {
            if (currentMode == ShopMode.BUY) {
                infoLabel.setText(selectedItem.getDescription() + "\n\nPreco: " + selectedItem.getPrice() + " Ouro");
            } else {
                int sellPrice = Math.max(1, selectedItem.getPrice() / 2);
                infoLabel.setText(selectedItem.getDescription() + "\n\nVender por: " + sellPrice + " Ouro");
            }
        } else {
            infoLabel.setText((currentMode == ShopMode.BUY) ? "Selecione um item para comprar." : "Selecione um item para vender.");
        }
        goldLabel.setText("Ouro: " + player.getMoney());
    }

    private void checkForNpcInteraction() {
        for (NPC npc : npcs) {
            if (player.getBounds().overlaps(npc.getBounds())) {
                if ("shopkeeper".equals(npc.getNpcType())) {
                    openShop();
                    break;
                }
            }
        }
    }

    private void loadNpcsAndShopInventory() {
        MapLayer npcLayer = map.getLayers().get("NPCs");
        if (npcLayer == null) return;

        for (MapObject object : npcLayer.getObjects()) {
            if ("shopkeeper".equals(object.getProperties().get("type", String.class))) {
                float x = ((RectangleMapObject) object).getRectangle().x;
                float y = ((RectangleMapObject) object).getRectangle().y;
                String spritePath = object.getProperties().get("sprite", String.class);
                NPC npc = new NPC(new Texture(Gdx.files.internal(spritePath)), x, y, "shopkeeper");
                npcs.add(npc);

                String inventoryStr = object.getProperties().get("shop_inventory", String.class);
                if (inventoryStr != null) {
                    Array<Item> shopItems = new Array<>();
                    String[] itemIds = inventoryStr.split(",");
                    for (String id : itemIds) {
                        Item item = ItemFactory.createItem(id.trim());
                        if (item != null) shopItems.add(item);
                    }
                    buyList.setItems(shopItems);
                    if (shopItems.size > 0) {
                        buyList.setSelectedIndex(0);
                    }
                }
                break;
            }
        }
    }

    private void checkMapObjectCollisions(float oldPlayerX, float oldPlayerY) {
        handleSolidObjectCollisions(map.getLayers().get("Colisoes"), oldPlayerX, oldPlayerY);
        handleTransitionCollisions(map.getLayers().get("Portas"));
    }

    private void handleSolidObjectCollisions(MapLayer layer, float oldPlayerX, float oldPlayerY) {
        if (layer == null) return;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if (player.getBounds().overlaps(((RectangleMapObject) object).getRectangle())) {
                    player.setPosition(oldPlayerX, oldPlayerY);
                    return;
                }
            }
        }
    }

    private void handleTransitionCollisions(MapLayer layer) {
        if (layer == null) return;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if (player.getBounds().overlaps(((RectangleMapObject) object).getRectangle())) {
                    String targetMap = object.getProperties().get("target", String.class);
                    if (targetMap != null && "vila".equalsIgnoreCase(targetMap)) {
                        mapManager.changeMap(MapManager.MapType.VILLAGE);
                        return;
                    }
                }
            }
        }
    }

    private Vector2 findSpawnPoint(TiledMap map, String spawnName) {
        MapLayer objectLayer = map.getLayers().get("Spawn");
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (spawnName.equals(object.getName()) && object instanceof RectangleMapObject) {
                    return new Vector2(((RectangleMapObject) object).getRectangle().x, ((RectangleMapObject) object).getRectangle().y);
                }
            }
        }
        return new Vector2(100, 100);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        skin.dispose();
        stage.dispose();
        for (NPC npc : npcs) {
            npc.dispose();
        }
    }

    @Override public boolean keyUp(int keycode) { return stage.keyUp(keycode); }
    @Override public boolean keyTyped(char character) { return stage.keyTyped(character); }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return stage.touchDown(screenX, screenY, pointer, button); }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return stage.touchUp(screenX, screenY, pointer, button); }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return stage.touchDragged(screenX, screenY, pointer); }
    @Override public boolean mouseMoved(int screenX, int screenY) { return stage.mouseMoved(screenX, screenY); }
    @Override public boolean scrolled(float amountX, float amountY) { return stage.scrolled(amountX, amountY); }
}
