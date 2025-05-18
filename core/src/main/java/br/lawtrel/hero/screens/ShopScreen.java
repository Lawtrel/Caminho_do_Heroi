package br.lawtrel.hero.screens;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.PlayerBuilder;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ShopScreen extends ScreenAdapter {
    private Hero game;
    private MapManager  mapManager ;
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private Player player;


    public ShopScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager = mapManager;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        //Carrega o mapa
        map = new TmxMapLoader().load("maps/shop.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        //Ponto de Spawn
        Vector2 spawn = findSpawnPoint(map);

        //Criar o Player com o builder
        player = new PlayerBuilder()
            .setPosition(spawn.x, spawn.y)
            .loadAnimation("sprites/hero.png")
            .build();

        //Cria a camera do jogo
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 250, 250); // ajuste baseado tamanho do map
        camera.position.set(player.getX(), player.getY(), 0); // centraliza no player
        camera.update();
    }

    @Override
    public void render(float delta) {
        //atualizar o hero
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        player.update(delta, up, down, left, right);

        // Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //camera
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();

        //Renderizar o mapa
        mapRenderer.setView(camera);
        mapRenderer.render();

        //renderizar o hero
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch, player.getX(), player.getY());
        batch.end();

        checkDoorCollision();
    }

    private void checkDoorCollision() {
        MapLayer doorLayer = map.getLayers().get("Portas");

        if (doorLayer == null) return;

        float playerX = player.getX();
        float playerY = player.getY();

        for (MapObject object : doorLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle doorRect = ((RectangleMapObject) object).getRectangle();

                if (doorRect.contains(playerX, playerY)) {
                    String target = object.getProperties().get("target", String.class);

                    if (target != null) {
                        switch (target) {
                            case "world":
                                mapManager.changeMap(MapManager.MapType.WORLD_MAP);
                                return;
                        }
                    }
                }
            }
        }
    }
    private Vector2 findSpawnPoint(TiledMap map) {
        MapLayer objectLayer = map.getLayers().get("spawn"); //nome da camada
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if ("spawn".equals(object.getName()) && object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    return new Vector2(rect.x, rect.y);
                }
            }
        }
        return new Vector2(100, 100); // valor padrão caso não encontre

    }
    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
