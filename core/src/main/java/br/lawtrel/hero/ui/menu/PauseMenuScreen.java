package br.lawtrel.hero.ui.menu;

import br.lawtrel.hero.Hero;
import br.lawtrel.hero.ui.components.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;

public class PauseMenuScreen extends ScreenAdapter {
    private Hero game;
    private Stage stage;
    private Skin skin;
    private Table mainTable;

    // Seções do menu
    private StatusSection statusSection;
    private ItemsSection itemsSection;
    private EquipmentSection equipmentSection;

    // Botões
    private TextButton resumeButton;
    private TextButton saveButton;
    private TextButton settingsButton;
    private TextButton exitButton;
    private TextButton statusTab, itemsTab, equipmentTab;
    private Label feedbackLabel;

    // Posição do menu
    private ButtonGroup<TextButton> tabGroup;
    private int currentTab = 0;

    // Cor para o feedback, pode ser lida do skin ou definida aqui
    private static final Color NES_YELLOW_ACCENT = new Color(1f, 1f, 0.3f, 1f);

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
            Gdx.app.error("PauseMenuScreen", "Nao foi possivel carregar o skin: skins/uiskin.json", e);
            skin = new Skin(); // Fallback para um skin vazio para evitar crash.
        }

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        if (game.getPlayer() == null) {
            Gdx.app.error("PauseMenuScreen", "Player eh null. Resumindo jogo.");
            game.resumeGame();
            return;
        }

        // Criar as Seções passando o skin carregado do arquivo
        statusSection = new StatusSection(game, skin);
        itemsSection = new ItemsSection(game, skin);
        equipmentSection = new EquipmentSection(game, skin);
        itemsSection.setSiblingSections(statusSection, equipmentSection);

        statusTab = new TextButton("Status", skin, "nes-tab");
        itemsTab = new TextButton("Itens", skin, "nes-tab");
        equipmentTab = new TextButton("Equipamentos", skin, "nes-tab");

        tabGroup = new ButtonGroup<>(statusTab, itemsTab, equipmentTab);
        tabGroup.setMaxCheckCount(1); // Garante que apenas uma aba pode ser selecionada
        tabGroup.setMinCheckCount(1); // Garante que uma aba sempre estará selecionada
        tabGroup.setUncheckLast(true); // Permite desmarcar a aba anterior ao clicar em uma nova


        resumeButton = new TextButton("Continuar (ESC)", skin, "nes-style");
        saveButton = new TextButton("Salvar Jogo", skin, "nes-style");
        settingsButton = new TextButton("Configuracoes", skin, "nes-style");
        exitButton = new TextButton("Sair para Menu Principal", skin, "nes-style");

        feedbackLabel = new Label("", skin);
        feedbackLabel.setAlignment(Align.center);
        feedbackLabel.setVisible(false);
        feedbackLabel.setColor(NES_YELLOW_ACCENT);

        // Listeners (nenhuma mudança aqui)
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
                if (game != null) {
                    game.saveGame();
                    showFeedback("Jogo Salvo!");
                }
            }
        });
        settingsButton.addListener(new ClickListener() { /* ... */ });
        exitButton.addListener(new ClickListener() { /* ... */ });

        Table tabTable = new Table();
        tabTable.add(statusTab).pad(5);
        tabTable.add(itemsTab).pad(5);
        tabTable.add(equipmentTab).pad(5);

        final Table contentTable = new Table();
        contentTable.setBackground(skin.getDrawable("nes-button-up"));
        contentTable.setName("content-panel");

        Table buttonTable = new Table();
        buttonTable.add(resumeButton).pad(5).width(250).fillX().height(30).row();
        buttonTable.add(saveButton).pad(5).width(250).fillX().height(30).row();
        buttonTable.add(settingsButton).pad(5).width(250).fillX().height(30).row();
        buttonTable.add(exitButton).pad(5).width(250).fillX().height(30).row();
        buttonTable.add(feedbackLabel).padTop(10).width(250).fillX().row();

        mainTable.add(tabTable).colspan(2).top().padBottom(10).row();
        mainTable.add(contentTable).expand().fill().pad(10);
        mainTable.add(buttonTable).top().right().pad(10).width(260);

        switchTab(currentTab);
    }

    private void showFeedback(String message) {
        if (feedbackLabel != null) {
            feedbackLabel.setText(message);
            feedbackLabel.setVisible(true);
            feedbackLabel.clearActions();
            feedbackLabel.addAction(Actions.sequence(
                Actions.delay(2.0f),
                Actions.fadeOut(0.5f),
                Actions.run(() -> {
                    feedbackLabel.setVisible(false);
                    feedbackLabel.getColor().a = 1.0f;
                })
            ));
        }
    }

    private void switchTab(int tabIndex) {
        currentTab = tabIndex;
        Table contentTable = (Table) mainTable.getCells().get(1).getActor();
        contentTable.clearChildren();
        contentTable.setBackground(skin.getDrawable("nes-button-up")); // Re-aplica o fundo

        switch(tabIndex) {
            case 0:
                contentTable.add(statusSection).expand().fill().pad(10);
                break;
            case 1:
                if (itemsSection != null) itemsSection.updateItemsDisplay();
                contentTable.add(itemsSection).expand().fill().pad(10);
                break;
            case 2:
                contentTable.add(equipmentSection).expand().fill().pad(10);
                break;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.consumeJustPausedFlag()) {
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.resumeGame();
            return;
        }

        try {
            if (stage != null) {
                stage.act(delta);
                stage.draw();
            }
        } catch (Exception e) {
            Gdx.app.error("PauseMenuScreen", "Erro no render do Stage", e);
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
