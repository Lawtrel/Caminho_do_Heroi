package br.lawtrel.hero.screens.castle;

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

public class CastleScreen extends ScreenAdapter{
    private final Hero game;
    private final MapManager  mapManager ;
    private final  OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch batch;
    private Player player;
    private final String MAP_ID = "maps/castle.tmx";


    // Adicione estas constantes com as dimensões REAIS do seu mapa vila.tmx
    private final int MAP_WIDTH_PIXELS = 40 * 16 ; // Exemplo: 100 tiles * 16 pixels/tile
    private final int MAP_HEIGHT_PIXELS = 30 * 16 ; // Exemplo: 100 tiles * 16 pixels/tile

    private static final float WORLD_WIDTH = 400;
    private static final float WORLD_HEIGHT = 300;

    public CastleScreen(Hero game, MapManager mapManager){
        this.game = game;
        this.mapManager = mapManager;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
    }

    @Override
    public void show(){
        game.soundManager.playMusic("audio/music/castle_map.mp3", true);
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        //Carrega o mapa
        map = new TmxMapLoader().load(MAP_ID);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        //Ponto de Spawn
        this.player = game.getPlayer();
        if (player == null) {
            Gdx.app.error("CastleScreen", "Jogador nulo ao mostrar a tela!");
            return;
        }

        player.setScale(0.5f);
        player.setInBattleView(false);

        // <<< CORREÇÃO: Procura o spawn point com o nome correto do seu arquivo TMX >>>
        Vector2 spawnPoint = findSpawnPoint(map);
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
        float oldPLayerX = player.getX();
        float oldPlayerY = player.getY();

        //atualizar o hero
        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        player.update(delta, up, down, left, right);
        checkMapObjectCollisions(oldPLayerX, oldPlayerY); //função nova para lidar com as colisões

        //Colisao com bordas do mapa
        //float clampedX = MathUtils.clamp(player.getX(), 0, MAP_WIDTH_PIXELS - player.getBounds().width);
        //float clampedY = MathUtils.clamp(player.getY(), 0, MAP_HEIGHT_PIXELS - player.getBounds().height);
        //player.setPosition(clampedX, clampedY);

        /*
        //atualiza a camera para seguir hero
        float camX = MathUtils.clamp(player.getX(), camera.viewportWidth / 2, 100 - camera.viewportWidth / 2);
        float camY = MathUtils.clamp(player.getY(), camera.viewportHeight / 2, 100 - camera.viewportHeight / 2);
        camera.position.set(camX, camY, 0);
        camera.update();
        */


        //Camera
        camera.position.x = MathUtils.clamp(player.getX(), viewport.getWorldWidth() / 2f, MAP_WIDTH_PIXELS - viewport.getWorldWidth() / 2f);
        camera.position.y = MathUtils.clamp(player.getY(), viewport.getWorldHeight() / 2f, MAP_HEIGHT_PIXELS - viewport.getWorldHeight() / 2f);
        camera.update();
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

    }

    private void checkMapObjectCollisions(float oldPlayerX, float oldPlayerY){
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

                        switch (targetMap.toLowerCase()) {
                            case "castlein":
                                mapManager.changeMap(MapManager.MapType.CASTLE_IN);
                                return;
                        }
                    }
                }
            }
        }
    }

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

    private Vector2 findSpawnPoint(TiledMap map){
        MapLayer objectLayer = map.getLayers().get("Spawn");
        if(objectLayer != null){
            for (MapObject object : objectLayer.getObjects()){
                if("spawnPoint".equals(object.getName()) && object instanceof RectangleMapObject){
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    return new Vector2(rect.x, rect.y);
                }
            }
        }
        Gdx.app.log("CastleScreen", "Objeto 'spawnPoint' não encontrado na camada 'Spawn'. Usando valor padrão.");
        return new Vector2(100, 100); // valor padrão caso não encontre
    }

    /*
    private void checkDoorCollision() {
        MapLayer mapLayer = map.getLayers().get("Colisoes");

        if (mapLayer == null){
            Gdx.app.log("VillageScreen", "Camada 'Colisoes' não encontrada");
            return;
        }

        Rectangle playerBounds = player.getBounds();

        for(MapObject object : mapLayer.getObjects()){
            if (object instanceof RectangleMapObject){
                Rectangle mapReact = ((RectangleMapObject) object).getRectangle();

                if(playerBounds.overlaps(mapReact)){
                    String targetMap = object.getProperties().get("target", String.class);
                    String targetSpaw = object.getProperties().get("target_spaw", String.class);


                   if (targetMap != null){
                       //Salvar posição do jogador antes de trocar de mapa
                       game.setPlayerLastWorldMapPosition(
                           Float.parseFloat(targetSpaw.split(",")[0]),
                           Float.parseFloat(targetSpaw.split(",")[1]),
                           targetMap.toLowerCase().equals("world") ? "maps/word.tmx" : "maps/" + targetMap.toLowerCase() + ".tmx"
                       );

                       switch(targetMap.toLowerCase()){
                           case "world":
                               mapManager.changeMap(MapManager.MapType.WORLD_MAP);
                               return;
                       }
                   }
                }
            }
        }
    }*/

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
