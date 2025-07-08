// Crie este arquivo em: core/src/main/java/br/lawtrel/hero/screens/MainMenuScreen.java
package br.lawtrel.hero.screens;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.utils.SaveManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends ScreenAdapter {

    private final Hero game;
    private Stage stage;
    private Skin skin; // Usaremos um skin básico, pode ser o mesmo do PauseMenu

    public MainMenuScreen(Hero game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        // Você pode criar um skin reutilizável ou criar um aqui
        // Para simplicidade, vamos usar o skin do jogo (uiskin.json)
        try {
            this.skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Não foi possível carregar uiskin.json. Usando skin padrão.", e);
            this.skin = new Skin(); // Fallback para um skin vazio
        }
    }

    @Override
    public void show() {
        game.soundManager.playMusic("audio/music/intro_theme.mp3", true);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("Caminho do Heroi", skin);
        title.setFontScale(2.0f); // Aumenta o tamanho da fonte do título

        // --- Botões do Menu ---
        TextButton newGameButton = new TextButton("Novo Jogo", skin);
        TextButton loadGameButton = new TextButton("Carregar Jogo", skin);
        TextButton exitButton = new TextButton("Sair", skin);

        // Verifica se o save existe para habilitar/desabilitar o botão "Carregar"
        boolean saveExists = SaveManager.doesSaveExist();
        loadGameButton.setDisabled(!saveExists);
        if (!saveExists) {
            loadGameButton.setColor(Color.GRAY); // Estilo visual para desabilitado
        }

        // --- Listeners dos Botões ---
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startNewGame(); // Chama o método em Hero para iniciar um novo jogo
            }
        });

        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!loadGameButton.isDisabled()) {
                    boolean loaded = game.loadGame(); // Chama o método em Hero para carregar
                    if (loaded) {
                        game.continueGame(); // Se carregou, continua para o mapa
                    } else {
                        // Opcional: Mostrar uma mensagem de erro
                        Gdx.app.log("MainMenuScreen", "Falha ao carregar o jogo.");
                    }
                }
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Adiciona os elementos à tabela
        table.add(title).padBottom(40).row();
        table.add(newGameButton).width(200).height(40).pad(10).row();
        table.add(loadGameButton).width(200).height(40).pad(10).row();
        table.add(exitButton).width(200).height(40).pad(10).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1); // Fundo azul escuro
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        // Para a música quando o jogador sai da tela
        game.soundManager.stopMusic();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
