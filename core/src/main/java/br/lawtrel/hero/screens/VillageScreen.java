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

public class VillageScreen extends ScreenAdapter {

    private final Hero game;
    private MapManager  mapManager ;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private Player player;
    private final String MAP_ID = "maps/vila.tmx";

    // Adicione estas constantes com as dimensões REAIS do seu mapa vila.tmx
    private final int MAP_WIDTH_PIXELS = 100 * 32 ; // Exemplo: 100 tiles * 16 pixels/tile
    private final int MAP_HEIGHT_PIXELS = 100 * 32 ; // Exemplo: 100 tiles * 16 pixels/tile

    public VillageScreen(Hero game, MapManager mapManager) {
        this.game = game;
        this.mapManager = mapManager;

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
            Gdx.app.log("VillageScreen_show", "Jogador restaurado para a posição salva: " + lastPosition.x + "," + lastPosition.y);
            game.clearPlayerLastWorldMapPosition(); // Limpa a posição para que não seja usada novamente por engano
        } else {
            // Se não houver posição salva (ex: primeira vez no mapa, ou após transição de outro tipo de tela), usa o spawn point
            Vector2 spawnPoint = spawnPoint(map);
            player.setPosition(spawnPoint.x, spawnPoint.y);
            Gdx.app.log("VillageScreen_show", "Nenhuma posição salva encontrada para " + MAP_ID + ". Usando spawn point: " + spawnPoint.x + "," + spawnPoint.y);
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

        //Colisao com bordas do mapa
        float clampedX = MathUtils.clamp(player.getX(), 0, MAP_WIDTH_PIXELS - player.getBounds().width);
        float clampedY = MathUtils.clamp(player.getY(), 0, MAP_HEIGHT_PIXELS - player.getBounds().height);
        player.setPosition(clampedX, clampedY);

        /*
        //atualiza a camera para seguir hero
        float camX = MathUtils.clamp(player.getX(), camera.viewportWidth / 2, 100 - camera.viewportWidth / 2);
        float camY = MathUtils.clamp(player.getY(), camera.viewportHeight / 2, 100 - camera.viewportHeight / 2);
        camera.position.set(camX, camY, 0);
        camera.update();
        */

        //Analisa a colisão dos objetos
        checkMapObjectCollisions(oldPLayerX, oldPlayerY); //função nova para lidar com as colisões


        //Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();


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
        //Camada de Entradas
        MapLayer entranceLayer = map.getLayers().get("Entradas");
        if(entranceLayer != null){
            handleTransitionCollisions(entranceLayer); //Reutiliza função de transição
        } else{
            Gdx.app.log("VillageScreen", "Camada 'Entradas' não  encontrada");
        }

        //Camada de Colisões
        MapLayer collisionLayer = map.getLayers().get("Colisoes");
        if (collisionLayer != null){
            handleSolidObjectCollisions(collisionLayer, oldPlayerX, oldPlayerY);
        } else{
            Gdx.app.log("VillageScreen", "Camada 'Colisoes' não encontrada");
        }

        //Camada de Ávores
        MapLayer treesLayer = map.getLayers().get("Arvores");
        if(treesLayer != null){
            handleSolidObjectCollisions(treesLayer, oldPlayerX, oldPlayerY);
        } else{
            Gdx.app.log("VillageScreen", "Camada 'Arvores' não encontrada");
        }
    }

    //Metodo generico para lidar com colisões que resultam em transições
    private void handleTransitionCollisions(MapLayer layer){
        Rectangle playerBounds = player.getBounds();

        for (MapObject object : layer.getObjects()){
            if(object instanceof RectangleMapObject){
                Rectangle transitionRect = ((RectangleMapObject) object).getRectangle();

                if(playerBounds.overlaps(transitionRect)){
                    String targetMap = object.getProperties().get("target", String.class);
                    String targetSpawn = object.getProperties().get("target_spawn", String.class);

                    if(targetMap != null && targetSpawn != null){
                        try{
                            float spawnX = Float.parseFloat(targetSpawn.split(",")[0]);
                            float spawnY = Float.parseFloat(targetSpawn.split(",")[1]);
                            String mapFileName;

                            //Lógica para determinar o nome do destino
                            switch (targetMap.toLowerCase()){
                                case "shop":
                                    mapFileName = "maps/shop.tmx";
                                    break;

                                case "world":
                                    mapFileName = "maps/word.tmx";
                                    break;

                                default:
                                    return; //Já logado acima, apenas dá o retornr
                            }
                            return; //Retorna após a transição ter sucesso
                        } catch (NumberFormatException e){
                            Gdx.app.error("VillageScreen","Erro ao parsear target_spawn: " + targetSpawn, e);
                        }
                    }

                }
            }
        }
    }

    private void handleSolidObjectCollisions(MapLayer layer, float oldPlayerX, float oldPlayerY){
        Rectangle playerBounds = player.getBounds();

        for (MapObject object : layer.getObjects()){
            if (object instanceof RectangleMapObject){
                Rectangle objectRect = ((RectangleMapObject) object).getRectangle();

                if(playerBounds.overlaps(objectRect)){
                    player.setPosition(oldPlayerX, oldPlayerY);
                    return;//Retorna após primeira colisão, para evitar bugs de textura, como o persoangem grudar
                }
            }
        }
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

    private Vector2 spawnPoint(TiledMap map){
        MapLayer objectLayer = map.getLayers().get("Spawn");
        if(objectLayer != null){
            for (MapObject object : objectLayer.getObjects()){
                if("spawnPoint".equals(object.getName()) && object instanceof RectangleMapObject){
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    return new Vector2(rect.x, rect.y);
                }
            }
        }
        Gdx.app.log("VillageScreen", "Objeto 'spawnPoint' não encontrado na camada 'Spawn'. Usando valor padrão.");
        return new Vector2(100, 100); // valor padrão caso não encontre
    }
}
