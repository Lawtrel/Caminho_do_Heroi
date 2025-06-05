package br.lawtrel.hero.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import br.lawtrel.hero.magic.Grimoire;
import br.lawtrel.hero.magic.Magics;

public class MagicMenu {
}
/*
    Array<Magics> magicList = new Array<>();

    //Metodo que pega os valores do grimorio
    public void setMagics(Grimoire grimoire){
        this.magicList = grimoire.chooseMagic();
    }

    private int selectedIndex = 0;
    private boolean canMove = true;

    public void update(){
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && canMove){ //tecla pra cima
            selectedIndex -= 1;
            canMove = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && canMove){
            selectedIndex += 1;
            canMove = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            canMove = true;
        }
    }

    //Metodo que pega o que foi selecionado pelo index
    public int getSelectedIndex(){
        return selectedIndex;
    }

    //Metodo que pega a magia escolhida
    public Magics getSelectedOption(){
        return getSelectedMagic();
    }

    //Metodo que desenha o menu com as magias
    public void render(SpriteBatch batch, BitmapFont font, float x, float y){
        for(int i = 0; i < magicList.size; i++){
            String prefix = (i == selectedIndex) ? ">" : " ";
            font.draw(batch, prefix + magicList.get(i).getMagicName(), x, y - i * 20);
        }
    }

    //Metodo que define que qual magia foi selecionada pelo usuario
    public Magics getSelectedMagic(){
        if(magicList.size > 0 && selectedIndex >= 0 && selectedIndex < magicList.size){
            return magicList.get(selectedIndex);
        }
        return null;
    }
}
*/
