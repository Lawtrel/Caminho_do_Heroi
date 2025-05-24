package br.lawtrel.hero.ui.menu;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.ui.components.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseMenuScreen extends ScreenAdapter {
    private Hero game;
    private Stage stage;
    private Skin skin;
    private Table mainTable;

    //Seçcoes do menu
    private StatusSection statusSection;
    private ItemsSection itemsSection;
    private EquipmentSection equipmentSection;

    //botoes
    private TextButton resumeButton;
    private TextButton saveButton;
    private TextButton settingsButton;
    private TextButton exitButton;

    //Posicao do menu
    private int currentTab = 0; // Status , 1 = items, 2 = equipamentos
    public PauseMenuScreen(Hero game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        try {
            skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        } catch (Exception e) {
            Gdx.app.error("PauseMenuScreen", "Erro ao carregar skin uiskin.json. Verifique o caminho.", e);
            skin = new Skin(Gdx.files.internal("assets/skins/uiskin.json"));
        }

        //Configura o layout principal
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        //Criar as Secoes
        statusSection = new StatusSection(game);
        itemsSection = new ItemsSection(game);
        equipmentSection = new EquipmentSection(game);

        //Criar os botoes
        TextButton statusTab = new TextButton("Status", skin);
        TextButton itemsTab = new TextButton("Items", skin);
        TextButton equipmentTab = new TextButton("Equipamentos", skin);

        //Criar os botos de acao
        resumeButton = new TextButton("Continuar(ESC)", skin);
        saveButton = new TextButton("Salvar Jogo", skin);
        settingsButton = new TextButton("Configurações", skin);
        exitButton = new TextButton("Sair para Menu", skin);

        // Adiciona listeners
        statusTab.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                switchTab(0);
            }
        });

        itemsTab.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                switchTab(1);
            }
        });

        equipmentTab.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                switchTab(2);
            }
        });

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.resumeGame();
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                saveGame();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                exitToMenu();
            }
        });

        // Layout do menu
        Table tabTable = new Table();
        tabTable.add(statusTab).pad(5);
        tabTable.add(itemsTab).pad(5);
        tabTable.add(equipmentTab).pad(5);

        Table contentTable = new Table();
        contentTable.add(statusSection).expand().fill();

        Table buttonTable = new Table();
        buttonTable.add(resumeButton).pad(5).width(200);
        buttonTable.row();
        buttonTable.add(saveButton).pad(5).width(200);
        buttonTable.row();
        buttonTable.add(settingsButton).pad(5).width(200);
        buttonTable.row();
        buttonTable.add(exitButton).pad(5).width(200);

        mainTable.add(tabTable).colspan(2).top().row();
        mainTable.add(contentTable).expand().fill().pad(10);
        mainTable.add(buttonTable).right().pad(10);

        // Mostra a tab inicial
        switchTab(0);

    }
    private void switchTab(int tabIndex) {
        currentTab = tabIndex;

        Table contentTable = mainTable.findActor("contentTable");
        contentTable.clearChildren();

        switch(tabIndex) {
            case 0:
                contentTable.add(statusSection).expand().fill();
                break;
            case 1:
                contentTable.add(itemsSection).expand().fill();
                break;
            case 2:
                contentTable.add(equipmentSection).expand().fill();
                break;
        }
    }

    private void resumeGame() {
        //game.setScreen(game.getPreviousScreen());
    }

    private void saveGame() {
        // TODO: Implementar lógica de salvamento
        System.out.println("Jogo salvo!");
    }

    private void exitToMenu() {
        // TODO: Implementar retorno ao menu principal
        System.out.println("Voltando ao menu principal");
    }

    @Override
    public void render(float delta) {
        // Limpa a tela com uma cor semi-transparente para dar o efeito de pausa
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Cor escura semi-transparente
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Verifica se ESC foi pressionado para continuar o jogo
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.resumeGame(); // Chama o método de Hero.java
            return; // Retorna para não processar o stage se o jogo foi resumido
        }

        try {
            stage.act(delta);
            stage.draw();
        } catch (Exception e) {
            Gdx.app.error("PauseMenuScreen", "Erro no render do Stage", e);
            // Fallback seguro: tenta resumir o jogo para evitar um softlock
            game.resumeGame();
        }
    }
    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("PauseMenuScreen", "Disposing PauseMenuScreen");
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }
}
