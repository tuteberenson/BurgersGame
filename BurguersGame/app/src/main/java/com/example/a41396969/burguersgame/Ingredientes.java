package com.example.a41396969.burguersgame;

import org.cocos2d.nodes.Sprite;

/**
 * Created by 41396969 on 6/10/2016.
 */
public class Ingredientes
{
    private Sprite _Ingrediente;
    private String _Tipo;

    public Ingredientes(Sprite ingrediente, String tipo)
    {
        _Ingrediente = ingrediente;
        _Tipo = tipo;
    }
    public Ingredientes()
    {

    }

    public Sprite get_Ingrediente() {
        return _Ingrediente;
    }

    public void set_Ingrediente(Sprite _Ingrediente) {
        this._Ingrediente = _Ingrediente;
    }

    public String get_Tipo() {
        return _Tipo;
    }

    public void set_Tipo(String _Tipo) {
        this._Tipo = _Tipo;
    }
}
