package br.lawtrel.hero.screens;

import br.lawtrel.hero.entities.Enemy;
import br.lawtrel.hero.entities.EnemyFactory;
import br.lawtrel.hero.entities.Player;
import br.lawtrel.hero.entities.PlayerBuilder;
import br.lawtrel.hero.Hero;
import br.lawtrel.hero.utils.MapManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//Mapa Principal do Game
public class WorldMapScreen extends ScreenAdapter {
    private Hero game;
    private MapManager  mapManager ;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private Player player;

    private final int TILE_SIZE = 16; // Tamanho das texturas
    private final int MAP_WIDTH = 100 * TILE_SIZE;
    private final int MAP_HEIGHT = 100 * TILE_SIZE;

    private float battleTimer  = 0;
    private static final float BATTLE_CHECK_INTERVAL = 5f; // Verifica a cada 5 segundos
    private static final float BATTLE_CHANCE = 0.3f; // 30% de chance

    public  WorldMapScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager  = mapManager ;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        //Carrega o mapa
        map = new TmxMapLoader().load("maps/word.tmx");
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
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Verifica se o jogo deve ser pausado
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //game.pauseGame();
            return;
        }


        //atualizar o hero
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        player.update(delta, up, down, left, right);
        //player.render(batch);

        //atualiza a camera para seguir hero
        float camX = MathUtils.clamp(player.getX(), camera.viewportWidth / 2, MAP_WIDTH - camera.viewportWidth / 2);
        float camY = MathUtils.clamp(player.getY(), camera.viewportHeight / 2, MAP_HEIGHT - camera.viewportHeight / 2);
        camera.position.set(camX, camY, 0);
        camera.update();

        //Renderizar o mapa
        mapRenderer.setView(camera);
        mapRenderer.render();
        checkDoorCollision();

        //Renderizar o Hero
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch, player.getX(), player.getY() );
        batch.end();

        if (player.isMoving()) {
            battleTimer += delta;
            if (battleTimer >= BATTLE_CHECK_INTERVAL) {
                battleTimer = 0;
                if (Math.random() < BATTLE_CHANCE) {
                    startRandomBattle();
                }
            }
        }



    }

    //função que faz a colisão
    private void checkDoorCollision() {
        MapLayer door = map.getLayers().get("Portas");

        if (door == null) return;

        float playerX = player.getX();
        float playerY = player.getY();

        for (MapObject object : door.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle doorRect = ((RectangleMapObject) object).getRectangle();

                if (doorRect.contains(playerX, playerY)) {
                    String target = object.getProperties().get("target", String.class);

                    if (target != null) {
                        switch (target) {
                            case "vila":
                                mapManager.changeMap(MapManager.MapType.VILLAGE);
                                return;
                            case "shop":
                                mapManager.changeMap(MapManager.MapType.SHOP);
                                return;
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
    private void startRandomBattle() {
        Array<Enemy> enemies = new Array<>();
        // Adiciona 1-3 inimigos aleatórios
        int enemyCount = 1 + (int)(Math.random() * 2);

        for (int i = 0; i < enemyCount; i++) {
            EnemyFactory.EnemyType randomType = EnemyFactory.EnemyType.values()[
                (int)(Math.random() * EnemyFactory.EnemyType.values().length)
                ];
            enemies.add(EnemyFactory.createEnemy(randomType, 100f, 100f));
        }

        game.setScreen(new BattleScreen(player, enemies));
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        player.dispose();
    }
}
