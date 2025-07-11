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
import com.badlogic.gdx.maps.Map;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

//Mapa Principal do Game
public class WorldMapScreen extends ScreenAdapter {
    private final Hero game;
    private final MapManager  mapManager ;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private Player player;
    private Viewport viewport;

    private final int TILE_SIZE = 16; // Tamanho das texturas
    private final int MAP_WIDTH_TILES = 55;
    private final int MAP_HEIGHT_TILES = 55;
    private final int MAP_WIDTH = MAP_WIDTH_TILES * TILE_SIZE;
    private final int MAP_HEIGHT = MAP_HEIGHT_TILES * TILE_SIZE;
    private final String MAP_ID = "maps/word.tmx";

    private static final float WORLD_WIDTH = 550;
    private static final float WORLD_HEIGHT = 550;

    private float battleTimer  = 0;
    private static final float BATTLE_CHECK_INTERVAL = 5f; // Verifica a cada 5 segundos
    private static final float BATTLE_CHANCE = 0.8f; // 80% de chance

    public WorldMapScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager  = mapManager ;
    }

    public Array<EnemyFactory.EnemyType> worldEnemy(){ //Seleciona os tipos de mosntros que desejo no mundo
        Array<EnemyFactory.EnemyType> enemyWorldArray = new Array<>();
        enemyWorldArray.add(EnemyFactory.EnemyType.GOBLIN);
        enemyWorldArray.add(EnemyFactory.EnemyType.WIZARD);
        enemyWorldArray.add(EnemyFactory.EnemyType.SKELETON);
        return enemyWorldArray;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        //Carrega o mapa
        map = new TmxMapLoader().load(MAP_ID);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        //Ponto de Spawn
        this.player = game.getPlayer();
        float scale = map.getProperties().get("playerScale", 1.0f, Float.class);
        player.setScale(scale); // Aplica a escala ao jogador
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
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(player.getX(), player.getY(), 0);
        //camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //camera.position.set(player.getX(), player.getY(), 0);
        //camera.update();

        game.soundManager.playMusic("audio/music/world_map.mp3", true);
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

        //Salva a posição anterior do jogador antes de atualizar
        float oldPlayerX = player.getX();
        float oldPlayerY = player.getY();

        //atualizar o hero
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        player.update(delta, up, down, left, right);
        checkMapObjectCollisions(oldPlayerX, oldPlayerY);//Função que checa as colisões

        //Colisao com bordas do mapa
        float clampedX = MathUtils.clamp(player.getX(), 0, MAP_WIDTH - player.getBounds().width);
        float clampedY = MathUtils.clamp(player.getY(), 0, MAP_HEIGHT - player.getBounds().height);
        player.setPosition(clampedX, clampedY);

        //atualiza a camera para seguir hero
        float camX = MathUtils.clamp(player.getX(), viewport.getWorldWidth() / 2f, MAP_WIDTH - viewport.getWorldWidth() / 2f);
        float camY = MathUtils.clamp(player.getY(), viewport.getWorldHeight() / 2f, MAP_HEIGHT - viewport.getWorldHeight() / 2f);
        camera.position.set(camX, camY, 0);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();

        //Renderizar o mapa
        mapRenderer.setView(camera);
        mapRenderer.render();
        //checkDoorCollision();

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

    private void checkMapObjectCollisions(float oldPlayerX, float oldPlayerY){//Checa as colisões existentes
        handleSolidObjectCollisions(map.getLayers().get("Colisoes"), oldPlayerX, oldPlayerY);
        handleTransitionCollisions(map.getLayers().get("Portas"));
    }

    //Metodo generico para lidar com colisões que resultam em transições
    private void handleTransitionCollisions(MapLayer layer){
        if (layer == null) return;
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                if (player.getBounds().overlaps(((RectangleMapObject) object).getRectangle())) {
                    String targetMap = object.getProperties().get("target", String.class);
                    if (targetMap != null) {

                        // <<< CORREÇÃO: Salva a posição atual do jogador antes de mudar >>>
                        game.setPlayerLastWorldMapPosition(player.getX(), player.getY(), MAP_ID);

                        if (targetMap != null) {
                            switch (targetMap.toLowerCase().toLowerCase()) {
                                case "vila":
                                    mapManager.changeMap(MapManager.MapType.VILLAGE);
                                    return;
                                case "caverna":
                                    mapManager.changeMap(MapManager.MapType.CAVE);
                                    return;
                            }
                        }
                    }
                }
            }
        }
    }

    //metodo que checa as colisões que impedem o personagem de se mover
    private void handleSolidObjectCollisions(MapLayer layer, float oldPlayerX, float oldPlayerY){
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
            Array<EnemyFactory.EnemyType> worldEnemy = worldEnemy();
            /*EnemyFactory.EnemyType randomType = EnemyFactory.EnemyType.values()[
                (int)(Math.random() * EnemyFactory.EnemyType.values().length)
                ];*/

            // Cria o inimigo alinhado na mesma altura (Y) que o jogador
            Enemy enemy = EnemyFactory.createEnemy(worldEnemy.random(), 0, 0);

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
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true para centralizar a câmera
    }

    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
