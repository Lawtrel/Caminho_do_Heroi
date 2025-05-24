package br.lawtrel.hero.screens;

import br.lawtrel.hero.entities.*;
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
    private final Hero game;
    private final MapManager  mapManager ;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private Player player;

    private final int TILE_SIZE = 16; // Tamanho das texturas
    private final int MAP_WIDTH_TILES = 100;
    private final int MAP_HEIGHT_TILES = 100;
    private final int MAP_WIDTH = MAP_WIDTH_TILES * TILE_SIZE;
    private final int MAP_HEIGHT = MAP_HEIGHT_TILES * TILE_SIZE;
    private final String MAP_ID = "maps/word.tmx";

    private float battleTimer  = 0;
    private static final float BATTLE_CHECK_INTERVAL = 5f; // Verifica a cada 5 segundos
    private static final float BATTLE_CHANCE = 0.8f; // 80% de chance

    public  WorldMapScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager  = mapManager ;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        //Carrega o mapa
        map = new TmxMapLoader().load(MAP_ID);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        //Ponto de Spawn
        this.player = game.getPlayer();
        player.setInBattleView(false);
        Vector2 lastPosition = null;

        lastPosition = game.getPlayerLastWorldMapPosition(MAP_ID);

        if (lastPosition != null) {
            player.setPosition(lastPosition.x, lastPosition.y);
            Gdx.app.log("WorldMapScreen_show", "Jogador restaurado para a posição salva: " + lastPosition.x + "," + lastPosition.y);
            game.clearPlayerLastWorldMapPosition(); // Limpa a posição para que não seja usada novamente por engano
        } else {
            // Se não houver posição salva (ex: primeira vez no mapa, ou após transição de outro tipo de tela), usa o spawn point
            Vector2 spawnPoint = findSpawnPoint(map);
            player.setPosition(spawnPoint.x, spawnPoint.y);
            Gdx.app.log("WorldMapScreen_show", "Nenhuma posição salva encontrada para " + MAP_ID + ". Usando spawn point: " + spawnPoint.x + "," + spawnPoint.y);
        }

        //Cria a camera do jogo
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Verifica se o jogo deve ser pausado
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (game != null) { // Boa prática verificar se 'game' não é nulo
                game.pauseGame(); // Chama o método de Hero.java para abrir o PauseMenuScreen
            }
            return; // Retorna imediatamente para não processar o resto do frame da WorldMapScreen,
            // pois a tela está mudando para o PauseMenuScreen.
        }


        //atualizar o hero
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        player.update(delta, up, down, left, right);

        //Colisao com bordas do mapa
        float clampedX = MathUtils.clamp(player.getX(), 0, MAP_WIDTH - player.getBounds().width);
        float clampedY = MathUtils.clamp(player.getY(), 0, MAP_HEIGHT - player.getBounds().height);
        player.setPosition(clampedX, clampedY);

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

        float playerCenterX  = player.getX() + player.getBounds().width / 2;
        float playerCenterY  = player.getY() + player.getBounds().height / 2;

        for (MapObject object : door.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle doorRect = ((RectangleMapObject) object).getRectangle();

                if (player.getBounds().overlaps(doorRect)) {
                    if (doorRect.contains(playerCenterX, playerCenterY)) {
                        String target = object.getProperties().get("target", String.class);
                        String targetSpawn = object.getProperties().get("target_spawn", String.class); // Ponto de spawn no novo mapa

                        if (target != null) {
                            switch (target.toLowerCase()) {
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
        int enemyCount = 1 + (int)(Math.random() * 3); // 1-3 inimigos

        for (int i = 0; i < enemyCount; i++) {
            EnemyFactory.EnemyType randomType = EnemyFactory.EnemyType.values()[
                (int)(Math.random() * EnemyFactory.EnemyType.values().length)
                ];

            // Cria o inimigo alinhado na mesma altura (Y) que o jogador
            Enemy enemy = EnemyFactory.createEnemy(randomType, 0, 0);

            if (enemy.getCharacter() != null) {
                enemies.add(enemy);
            }
        }

        if (enemies.size > 0) {
            game.setPlayerLastWorldMapPosition(player.getX(), player.getY(), MAP_ID);
            player.setCurrentArea("floresta");
            game.setScreen(new BattleScreen(game, player, enemies));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
