package br.lawtrel.hero.screens;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ShopScreen extends ScreenAdapter {
    private final Hero game;
    private final MapManager  mapManager ;
    private TiledMap map;
    private final OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch batch;
    private Player player;
    private Viewport viewport;
    private final String MAP_ID = "maps/shop.tmx";

    private final int MAP_WIDTH_PIXELS = 9 * 32;  // Exemplo: 25 tiles de largura * 16 pixels/tile
    private final int MAP_HEIGHT_PIXELS = 11 * 32; // Exemplo: 20 tiles de altura * 16 pixels/tile
    private static final float WORLD_WIDTH = 400;
    private static final float WORLD_HEIGHT = 240;


    public ShopScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager = mapManager;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
    }

    @Override
    public void show() {
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        //Carrega o mapa
        map = new TmxMapLoader().load(MAP_ID);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        this.player = game.getPlayer();
        player.setInBattleView(false);
        //Ponto de Spawn
        Vector2 spawnPoint = findSpawnPoint(map,"spawn_from_vila" );
        player.setPosition(spawnPoint.x, spawnPoint.y);
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }


    @Override
    public void render(float delta) {
        // Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.pauseGame();
            return;
        }

        float oldPlayerX = player.getX();
        float oldPlayerY = player.getY();

        //atualizar o hero
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        player.update(delta, up, down, left, right);
        checkMapObjectCollisions(oldPlayerX, oldPlayerY);

        //camera
        camera.position.x = MathUtils.clamp(player.getX(), viewport.getWorldWidth() / 2f, MAP_WIDTH_PIXELS - viewport.getWorldWidth() / 2f);
        camera.position.y = MathUtils.clamp(player.getY(), viewport.getWorldHeight() / 2f, MAP_HEIGHT_PIXELS - viewport.getWorldHeight() / 2f);
        camera.update();
        viewport.apply();

        //Renderizar o mapa
        mapRenderer.setView(camera);
        mapRenderer.render();

        //renderizar o hero
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch, player.getX(), player.getY());
        batch.end();

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
                    if (targetMap != null) {
                        switch (targetMap.toLowerCase()) {
                            case "vila":
                                mapManager.changeMap(MapManager.MapType.VILLAGE);
                                return;
                            case "world":
                                // Normalmente não se sai de uma loja para o mapa do mundo, mas a opção está aqui.
                                mapManager.changeMap(MapManager.MapType.WORLD_MAP);
                                return;
                        }
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
        Gdx.app.log("ShopScreen", "Objeto de Spawn '" + spawnName + "' não encontrado. Usando (100, 100).");
        return new Vector2(100, 100);
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
