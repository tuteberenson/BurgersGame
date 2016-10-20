package com.example.a41396969.burguersgame;

import android.location.Location;
import android.text.LoginFilter;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.instant.CallFuncN;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.RotateBy;
import org.cocos2d.actions.interval.RotateTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.tests.SpritesTest;
import org.cocos2d.transitions.SplitRowsTransition;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

import java.security.interfaces.ECKey;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 41396969 on 16/8/2016.
 */

public class clase_Juego {
    CCGLSurfaceView _VistaDelJuego;
    CCSize pantallaDelDispositivo;
    Sprite ImagenFondo,Cinta, tubo;
    Ingredientes sprIngrediente,IngredienteTocado;
    //TODO Pasar ingrediente tocado a tipo Ingredientes y modificar lo que respecta
    float AltoPantalla, AnchoPantalla;
    CCPoint posicionFinalIngrediente;
    ArrayList<Ingredientes> listaIngredientes, listaIngredientesEnTubo;
    Boolean TocoIngrediente;


    public clase_Juego(CCGLSurfaceView vistaDelJuego)
    {
        Log.d("Constructor clase_Juego","Comiemza el constructor de la clase");
        _VistaDelJuego = vistaDelJuego;

    }
    public void ComenzarJuego()
    {
        Log.d("ComenzarJuego","Instancio los objetos de la clase Ingredientes - sprIngrediente");
        sprIngrediente= new Ingredientes();
        Log.d("ComenzarJuego","Instancio los objetos de la clase Ingredientes - IngredienteTocado");
        IngredienteTocado = new Ingredientes();

        Log.d("ComenzarJuego", "Llamo al director y pongo _VistaDelJuego");
        Director.sharedDirector().attachInView(_VistaDelJuego);

        Log.d("ComenzarJuego","Obtengo las dimensiones de la pantalla");
        pantallaDelDispositivo= Director.sharedDirector().displaySize();

        Log.d("ComenzarJuego","Asigno los valores para el alto y ancho de la pantalla");
        AltoPantalla =pantallaDelDispositivo.height;
        AnchoPantalla= pantallaDelDispositivo.width;

        Log.d("Comenzar Juego","Pantalla del dispositivo - Ancho: "+AnchoPantalla +
                " - Alto: "+AltoPantalla);

        Log.d("ComenzarJuego","Inicializo el array de ingredientes global previamente declarado");
        listaIngredientes = new ArrayList<>();

        Log.d("ComenzarJuego","Inicializo el array de ingredientes en el tubo global previamente declarado");
        listaIngredientesEnTubo = new ArrayList<>();

        Log.d("Comenzar Juego","Le digo al director que ejecute la escena");
        Director.sharedDirector().runWithScene(EscenaDelJuego());
    }
  private Scene EscenaDelJuego()
    {
        Log.d("EscenaDelJuego", "Comienzo armar escena del juego");

        Log.d("EscenaDelJuego", "Declaro e instacio la escena a devolver");
        Scene EscenaADevolver;
        EscenaADevolver=Scene.node();

        Log.d("EscenaDelJuego","Declaro e instancio la capa que va a contener la imagen de fondo");
        capaFondo miCapaFondo;
        miCapaFondo = new capaFondo();

        Log.d("EscenaDelJuego","Declaro e instancio la capa que va a contener todo los sprites distintos del fondo");
        capaFrente miCapaFrente;
        miCapaFrente = new capaFrente();

        Log.d("EscenaDelJuego","Indico la posicion de las capas en la pantalla");
        EscenaADevolver.addChild(miCapaFondo, -10);
        EscenaADevolver.addChild(miCapaFrente, -8);

        return EscenaADevolver;
    }

    class capaFrente extends Layer
    {
        public capaFrente()
        {
            Log.d("capaFrente","Inicio el constructor de la capaFrente");

            Log.d("capaFrente","Habilito el touch");
            this.setIsTouchEnabled(true);

            Log.d("capaFrente","Llamo al método de esta clase: ponerCinta");
            ponerCinta();

            Log.d("capaFrente","Llamo al método de esta clase: ponerTubo");
            ponerTubo();

            Log.d("capaFrente", "Repito con un timer el metodo elegirIngredienteAlAzar");
            TimerTask tareaPonerIngredientes;
            tareaPonerIngredientes = new TimerTask() {
                @Override
                public void run()
                {
                    Log.d("capaFrente", "Llamo al metodo elegirIngredienteAlAzar");
                    elegirIngredienteAlAzar(); /* este método va a llamar al metodo ponerIngrediente pasandole
                                          como parametro el ingrediente al azar que elegió*/
                }
            };

           Log.d("capaFrente","Repito con un timer para chequear si llegó al final");
            TimerTask tareaBorrarIngrediente;
            tareaBorrarIngrediente = new TimerTask() {
                @Override
                public void run() {
                    Log.d("capaFrente","Llamo al metodo para eliminar ingredientes");
                    llegoAlFinal();
                }
            };

            Timer relojBorrarIngredientes = new Timer();
            relojBorrarIngredientes.schedule(tareaBorrarIngrediente,0,50);

            Timer relojAgregarIngredientes = new Timer();
            relojAgregarIngredientes.schedule(tareaPonerIngredientes, 0, 1000);
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event) {
            Log.d("comienzaTouch", "Empezó el toque en la posición - X: "+ event.getX()+" - Y: "+ event.getY());

            CCPoint posicionTocada= new CCPoint();
            posicionTocada.x=event.getX();
            posicionTocada.y= pantallaDelDispositivo.getHeight() - event.getY();

            Sprite IngredienteTocadoResultado;

            IngredienteFueTocado(posicionTocada);

            //IngredienteTocado.runAction(RotateTo.action(0.1f,90f));

            return true;
        }

        @Override
        public boolean ccTouchesMoved(MotionEvent event) {
            Log.d("toqueMueve","Posición - X: "+ event.getX()+ " - Y: "+ event.getY());

            if (TocoIngrediente) {
                TrasladarIngrediente(event.getX(), pantallaDelDispositivo.getHeight() - event.getY());
            }
            return true;
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event) {
            final Ingredientes ingredienteCayendo;
            final String resultadoInterseccion="";

            Log.d("TerminoToque","Instancio un objeto de la clase Ingredientes");
            ingredienteCayendo = new Ingredientes();

            //TODO Fijarse y evaluar la posibilidad de poner el timer de las intersecciones en el inicio del juego.
            if (TocoIngrediente) {
                ingredienteCayendo.set_Ingrediente(IngredienteTocado.get_Ingrediente());
                ingredienteCayendo.set_Tipo(IngredienteTocado.get_Tipo());
                eliminarIngrediente(ingredienteCayendo,false,false);//el segundo parametro es para ver si esta en la cinta y el tercero es para ver si lo
                                                                   //o no, si es false, solo se desplaza hasta caer

                final Timer relojIntersecciones = new Timer();

              TimerTask TareaIntersecciones = new TimerTask() {
                  @Override
                  public void run() {

                      InterseccionEntreSprites(ingredienteCayendo);

                         // Log.d("toqueTerminado", "Terminó el toque, borró el ingrediente");
                          //eliminarIngrediente(ingredienteCayendo, false, true);
                      if(ingredienteCayendo.get_Ingrediente().getPositionY()<=40)
                      {
                          relojIntersecciones.cancel();
                      }
                  }
              };
                if (ingredienteCayendo.get_Ingrediente().getPositionY()>=-ingredienteCayendo.get_Ingrediente().getHeight()/2)
                {
                    relojIntersecciones.schedule(TareaIntersecciones, 0, 50);
                }
            }
            return true;
        }

        private boolean InterseccionEntreSprites(Ingredientes ingrediente)
        {

            String resultado = "";
            boolean Devolver=false;

            Log.d("InterseccionEntreSpr","Declaro e inicializo variables para los bordes de los sprites");
            int TuboIzquierda, TuboDerecha, TuboAbajo, TuboArriba;
            int IngredienteIzquierda, IngredienteDerecha, IngredienteAbajo, IngredienteArriba;

            TuboIzquierda = (int)(tubo.getPositionX() - tubo.getWidth()/2+12);
            TuboDerecha=(int) (tubo.getPositionX() + tubo.getWidth()/2-12);
            TuboAbajo=(int) (tubo.getPositionY() - tubo.getHeight()/2);
            TuboArriba=(int) (tubo.getPositionY() + tubo.getHeight()/2);

            IngredienteIzquierda=(int) (ingrediente.get_Ingrediente().getPositionX() - ingrediente.get_Ingrediente().getWidth()/2);
            IngredienteDerecha=(int) (ingrediente.get_Ingrediente().getPositionX() + ingrediente.get_Ingrediente().getWidth()/2);
            IngredienteAbajo=(int) (ingrediente.get_Ingrediente().getPositionY() - ingrediente.get_Ingrediente().getHeight()/2);
            IngredienteArriba=(int) (ingrediente.get_Ingrediente().getPositionY() + ingrediente.get_Ingrediente().getHeight()/2);

            Log.d("Interseccion", "Tubo - Izq: "+TuboIzquierda+" - Der: "+TuboDerecha+" - Aba: "+TuboAbajo+" - Arr: "+TuboArriba);
            Log.d("Interseccion", "IngredienteTocado - Izq: "+IngredienteIzquierda+" - Der: "+IngredienteDerecha+" - Aba:"+IngredienteAbajo+" - Arr: "+IngredienteArriba);



            Log.d("Interseccion","Posicion ingrediente - X: "+ ingrediente.get_Ingrediente().getPositionX()+ " - Y: "+ ingrediente.get_Ingrediente().getPositionY());
            if (EstaEntre(IngredienteIzquierda,TuboIzquierda,TuboDerecha) && EstaEntre(IngredienteDerecha,TuboDerecha,TuboIzquierda))
            {
                Log.d("InterseccionA","Cayó exactamente adentro del tubo");
                resultado="EA";//EA = Exactamente adentro
                Devolver=false;
            }
            else {
                if (ingrediente.get_Ingrediente().getPositionX() > TuboIzquierda && EstaEntre(TuboIzquierda,IngredienteIzquierda,IngredienteDerecha) ) {
                    Log.d("InterseccionA", "Cayó mas adentro - Lado izquierdo");
                    resultado= "IA"; // IA = Cae adentro pero no exacto del lado izquierdo
                    Devolver=true;
                }
                else if (ingrediente.get_Ingrediente().getPositionX() < TuboIzquierda && EstaEntre(TuboIzquierda,IngredienteIzquierda,IngredienteDerecha) ) {
                    Log.d("InterseccionA", "Cayó mas afuera - Lado izquierdo");
                    resultado="IAF";// IAF = Cae afuera del lado Izquierdo
                    Devolver=true;
                }
               else if (ingrediente.get_Ingrediente().getPositionX() < TuboDerecha && EstaEntre(TuboDerecha,IngredienteIzquierda,IngredienteDerecha)) {
                    Log.d("InterseccionA", "Cayó mas adentro - Lado derecho");
                    resultado="DA"; //DA = Cae adentro pero no exacto del lado derecho
                    Devolver=true;
                }
               else if (ingrediente.get_Ingrediente().getPositionX() > TuboDerecha && EstaEntre(TuboDerecha,IngredienteIzquierda,IngredienteDerecha)) {
                    Log.d("InterseccionA", "Cayó mas afuera - Lado derecho");
                    resultado= "DAF";// DAF = Cae afuera del lado derecho
                    Devolver=true;
                }
                else {
                    resultado = "AF"; //AF = Cae afuera
                    Devolver=false;
                }
            }

            evaluarResultadoCaida(resultado, ingrediente);
            return Devolver;
        }

        private void evaluarResultadoCaida(String resultado, Ingredientes Ingrediente)
        {
            //TODO Chequear aca que pasa en cada situacion
            switch (resultado)
            {
                case "EA":
                    agregoIngredienteAlTubo(Ingrediente);
                    break;
                case "IA":
                    break;
                case "IAF":
                    break;
                case "DA":
                    break;
                case "DAF":
                    break;
                case "AF":
                    eliminarIngrediente(Ingrediente, false, true);
                    break;
                default:
                    break;
            }
        }

        private void agregoIngredienteAlTubo(Ingredientes ingrediente)
        {
            CCPoint posicionIngrediente = new CCPoint();
            posicionIngrediente.x=tubo.getPositionX();
            if (listaIngredientesEnTubo.isEmpty())
            {
               posicionIngrediente.y = tubo.getPositionY() - tubo.getHeight()/2 +25;
                //ingrediente.runAction(MoveTo.action(0.1f,tubo.getPositionX(),(tubo.getPositionY() - tubo.getHeight()/2)+25));
            }
           /* else
            {
                for (Sprite unIngrediente:listaIngredientesEnTubo)
                {
                    posicionIngrediente.y = unIngrediente.getPositionY() + unIngrediente.getHeight()/2;
                }
            }*/
            Log.d("arrayIngreTubo","Muevo el ingrediente a la posicion - X: "+posicionIngrediente.x
            + " - Y: "+posicionIngrediente.y);
            ingrediente.get_Ingrediente().runAction(MoveTo.action(0.1f,posicionIngrediente.x,posicionIngrediente.y+35));
            Log.d("arrayIngreTubo","La cantidad de ingredientes en el tubo es de: "+ listaIngredientesEnTubo.size());
            listaIngredientesEnTubo.add(ingrediente);
            Log.d("arrayIngreTubo","Agrego un ingrediente, ahora hay: "+ listaIngredientesEnTubo.size());
        }

        private void IngredienteFueTocado(CCPoint posicionTocada)
        {
            TocoIngrediente= false;
            float BordeIzquierdoIng,BordeDerechoIng,BordeSuperiorIng,BordeInferiorIng;

            Log.d("comienzaTouch","Recorro los ingredientes para ver cual tocó");
            for (Ingredientes unIngrediente : listaIngredientes) {
                BordeIzquierdoIng = unIngrediente.get_Ingrediente().getPositionX() - unIngrediente.get_Ingrediente().getWidth() / 2;
                BordeDerechoIng = unIngrediente.get_Ingrediente().getPositionX() + unIngrediente.get_Ingrediente().getWidth() / 2;
                BordeInferiorIng = unIngrediente.get_Ingrediente().getPositionY() - unIngrediente.get_Ingrediente().getHeight() / 2;
                BordeSuperiorIng = unIngrediente.get_Ingrediente().getPositionY() + unIngrediente.get_Ingrediente().getHeight() / 2;
                Log.d("comienzaTouch", "Borde der: " + BordeDerechoIng + " - izq: " + BordeIzquierdoIng + " - Infe: " + BordeInferiorIng + " - sup: " + BordeSuperiorIng);

                Log.d("comienzaTouch", "Pregunto si la posicion del ingrediente es igual a la que tocó");
                if (posicionTocada.x > BordeIzquierdoIng &&
                        posicionTocada.x < BordeDerechoIng &&
                        posicionTocada.y > BordeInferiorIng &&
                        posicionTocada.y < BordeSuperiorIng) {
                    Log.d("comienzoTouch", "Seteo la variable global de toco ingrediente en true");
                    TocoIngrediente = true;

                    Log.d("comienzaTouch", "Tocó un Ingrediente!!");
                    IngredienteTocado = unIngrediente;

                    Log.d("comienzaTouch", "Anulo el movimiento del ingrediente tocado");
                    unIngrediente.get_Ingrediente().stopAllActions();

                    super.addChild(unIngrediente.get_Ingrediente());
                }
            }
        }
        private void TrasladarIngrediente(float DestinoX, float DestinoY)
        {
            Log.d("trasladarIngrediente","Le digo a donde lo voy a mover");
            IngredienteTocado.get_Ingrediente().runAction(MoveTo.action(0.01f,DestinoX,DestinoY));
            super.addChild(IngredienteTocado.get_Ingrediente());
        }

        public void llegoAlFinal()
       {
           // Log.d("llegoAlFinal","Declaro e inicializo un array donde van a ir los ingredientes a eliminar");
            //ArrayList<Sprite> ingredientesAEliminar = new ArrayList<>();

            Log.d("llegoAlFinal","obtengo la posicion 'X' del ingrediente");
            float posicionX;

            Log.d("llegoAlFinal","Pregunto si la posicion del ingrediente coincide con la posicion final");

            for (Ingredientes unIngrediente : listaIngredientes)
            {
                posicionX = unIngrediente.get_Ingrediente().getPositionX();
                Log.d("llegoAlFinal","la cantidad de ingredientes en listaIngredientes es de: "+listaIngredientes.size());
                Log.d("llegoAlFinal","El ingrediente actual es: "+ unIngrediente.get_Tipo());
                Log.d("llegoAlFinal","Posicion del ingrediente: "+ posicionX + " - Posicion final: "+ posicionFinalIngrediente.x);
                if (posicionX == posicionFinalIngrediente.x)
                {
                    Log.d("llegoAlFinal","La posicion X del ingrediente ("+posicionX+") llegó al final de la cinta, lo elimino");
                    eliminarIngrediente(unIngrediente,true, null);
                }
            }
        }

        private void eliminarIngrediente(Ingredientes spriteAEliminar, Boolean estaEnLaCinta, Boolean Eliminar)
        {

            MoveTo moverParaDesaparecer;
            if (estaEnLaCinta) {
                Log.d("eliminarIngrediente", "Declaro e inicializo los movimientos para la secuencia");
                RotateBy rotarTreintaGrados;
                rotarTreintaGrados = RotateBy.action(0.2f, 30f);
                moverParaDesaparecer = MoveTo.action(1f, spriteAEliminar.get_Ingrediente().getPositionX() + 100, -spriteAEliminar.get_Ingrediente().getHeight() / 2 - 150);

                Log.d("eliminarIngrediente", "Ejecuto una acción previa a la secuencia");
                spriteAEliminar.get_Ingrediente().runAction(moverParaDesaparecer);

                Log.d("eliminarIngrediente", "Defino la funcion a invocar al finalizar la secuencia");
                CallFuncN FinDelMovimiento;
                FinDelMovimiento = CallFuncN.action(this, "borrarIngrediente");

                Log.d("eliminarIngrediente", "Defino la secuencia");
                IntervalAction secuencia;
                secuencia = Sequence.actions(rotarTreintaGrados, rotarTreintaGrados, FinDelMovimiento);

                Log.d("eliminarIngrediente", "Ejecuto la secuencia");
                spriteAEliminar.get_Ingrediente().runAction(secuencia);

                Log.d("eliminarIngrediente", "Elimino el ingrediente");
                super.addChild(spriteAEliminar.get_Ingrediente());
            }
            else
            {
                if(Eliminar) {
                    Log.d("eliminarIngrediente", "Declaro e inicializo los movimientos para la secuencia");
                    moverParaDesaparecer = MoveTo.action(0.5f, IngredienteTocado.get_Ingrediente().getPositionX(), -IngredienteTocado.get_Ingrediente().getHeight() / 2);

                    Log.d("eliminarIngrediente", "Ejecuto una acción previa a la secuencia");
                    spriteAEliminar.get_Ingrediente().runAction(moverParaDesaparecer);

                    Log.d("eliminarIngrediente", "Defino la funcion a invocar al finalizar la secuencia");
                    CallFuncN FinDelMovimiento;
                    FinDelMovimiento = CallFuncN.action(this, "borrarIngrediente");

                    Log.d("eliminarIngrediente", "Defino la secuencia");
                    IntervalAction secuencia;
                    secuencia = Sequence.actions(moverParaDesaparecer, FinDelMovimiento);

                    Log.d("eliminarIngrediente", "Ejecuto la secuencia");
                    spriteAEliminar.get_Ingrediente().runAction(secuencia);

                    Log.d("eliminarIngrediente", "Elimino el ingrediente");
                    super.addChild(spriteAEliminar.get_Ingrediente());
                }
                else
                {
                    Log.d("eliminarIngrediente", "Declaro e inicializo los movimientos para que el ingrediente caiga");
                    moverParaDesaparecer = MoveTo.action(0.5f, IngredienteTocado.get_Ingrediente().getPositionX(), -IngredienteTocado.get_Ingrediente().getHeight() / 2);

                    Log.d("eliminarIngrediente", "Ejecuto el movimiento");
                    spriteAEliminar.get_Ingrediente().runAction(moverParaDesaparecer);

                    Log.d("eliminarIngrediente", "Elimino el ingrediente");
                    super.addChild(spriteAEliminar.get_Ingrediente());
                }
            }
        }

        public void borrarIngrediente(CocosNode ingrediente)
        {
            Log.d("borrarIngrediente","Termino el recorrido");

            super.removeChild(ingrediente, true);
            listaIngredientes.remove(ingrediente);
            Log.d("borrarIngrediente","Se borró el ingrediente, quedan: "+ listaIngredientes.size());
        }

        public boolean EstaEntre(int NumeroAComparar, int NumeroMenor, int NumeroMayor)
        {
            boolean Resultado;

            Log.d("EstaEntre","NumeroMenor: "+ NumeroMenor+" - NumeroMayor: "+NumeroMayor);

            if (NumeroMenor > NumeroMayor)
            {
                Log.d("EstaEntre","Me los mandaron invertidos, los ordeno");
                int auxiliar;
                auxiliar = NumeroMayor;
                NumeroMayor=NumeroMenor;
                NumeroMenor=auxiliar;
            }

            if (NumeroAComparar >= NumeroMenor && NumeroAComparar<=NumeroMayor)
            {
                Log.d("EstaEntre","Está entre");
                Resultado=true;
            }
            else
            {
                Log.d("EstaEntre","No está entre");
                Resultado = false;
            }

            return Resultado;
        }

        private void elegirIngredienteAlAzar() {
            String ingredienteElegido;
            int numAlAzar;

            Log.d("elegirIngredienteAlAzar","Declaro e inicializo un objeto Random");
            Random generadorDeAzar = new Random();

            numAlAzar = generadorDeAzar.nextInt(7);

            Log.d("elegirIngredienteAlAzar","Asigno un ingrediente según el numero al azar");
            switch (numAlAzar)
            {
                case 0:
                    ingredienteElegido = "cebolla";
                    break;
                case 1:
                    ingredienteElegido = "cheddar";
                    break;
                case 2:
                    ingredienteElegido = "hamburguesa";
                    break;
                case 3:
                    ingredienteElegido = "lechuga";
                    break;
                case 4:
                    ingredienteElegido = "pan_abajo";
                    break;
                case 5:
                    ingredienteElegido = "pan_arriba";
                    break;
                case 6:
                    ingredienteElegido = "tomate";
                    break;
                default:
                    Log.d("elegirIngredienteAlAzar","Hubo un error en el switch");
                    ingredienteElegido="";
                    break;
            }

            Log.d("elegirIngredienteAlAzar","Llamo al metodo ponerIngrediente y le paso de parametro: "+ ingredienteElegido);
            ponerIngrediente(ingredienteElegido);// le paso como parametro el ingrediente elegido al azar
        }

        private void ponerIngrediente(String ingrediente) {
            Log.d("ponerIngrediente", "Comienza el metodo");

            Log.d("ponerIngredientes", "Inicializo el sprite del ingrediente, utilizo el parametro");
            sprIngrediente.set_Ingrediente(Sprite.sprite(ingrediente + ".png"));

            Log.d("ponerIngrediente","Guardo el tipo de ingrediente en el objeto ingrediente");
            sprIngrediente.set_Tipo(ingrediente);

            Log.d("ponerIngrediente", "Declaro variales para luego obtener las dimensiones del ingrediente");
            float anchoIngrediente, altoIngrediente;
            anchoIngrediente = sprIngrediente.get_Ingrediente().getWidth();
            altoIngrediente = sprIngrediente.get_Ingrediente().getHeight();

            Log.d("ponerIngrediente", "Declaro e inicializo una variable y obtengo la posicion inicial");
            CCPoint posicionInicial = new CCPoint();
            posicionInicial.x = anchoIngrediente / 2;
            posicionInicial.y = Cinta.getHeight();

            // float posicionInicialX, posicionInicialY;
            // posicionInicialX = anchoIngrediente/2;
            // posicionInicialY = Cinta.getPositionY()+altoIngrediente/2;

            Log.d("ponerIngrediente", "Ubico el ingrediente en la posicion obtenida - X: " + posicionInicial.x + " - Y: " + posicionInicial.y);
            //sprIngrediente.setPosition(posicionInicialX,posicionInicialY);
            sprIngrediente.get_Ingrediente().setPosition(posicionInicial.x, posicionInicial.y);

            Log.d("ponerIngrediente", "inicializo una variable global para determinar la posicion final");
            posicionFinalIngrediente = new CCPoint();

            Log.d("ponerIngrediente","La posicion 'Y' del ingrediente va a ser igual que la inicial");
            posicionFinalIngrediente.y = posicionInicial.y;

            Log.d("ponerIngrediente","La posicion 'X' cambia");
            posicionFinalIngrediente.x = Cinta.getWidth();

            Log.d("ponerIngrediente","Desplazo el objeto hasta la posicion final");
            sprIngrediente.get_Ingrediente().runAction(MoveTo.action(3, posicionFinalIngrediente.x, posicionFinalIngrediente.y));

            Log.d("ponerIngrediente", "Agrego un ingrediente al array");
            listaIngredientes.add(sprIngrediente);

            Log.d("ponerIngrediente","Hay: "+ listaIngredientes.size()+" ingredientes en el array");

            Log.d("ponerIngrediente","Agrego el ingrediente a la capa");
            super.addChild(sprIngrediente.get_Ingrediente());
        }

        private void ponerCinta()
        {
            Log.d("ponerCinta","Comienza el metodo");

            Log.d("ponerCinta","Instancio el sprite de cinta");
            Cinta = Sprite.sprite("cinta_grande.png");

            //Log.d("ponerCinta","Escalo la cinta");
            //Cinta.runAction(ScaleBy.action(0.01f,2f,2f));

            Log.d("ponerCinta","Obtengo las dimensiones de la imagen");
            float AltoCinta, AnchoCinta;
            AltoCinta = Cinta.getHeight();
            AnchoCinta = Cinta.getWidth();

            Log.d("ponerCinta","Declaro e inicializo las variables para la posicion inicial");
            float PosicionInicialX, PosicionInicialY;
            PosicionInicialX = AnchoCinta/2;
            PosicionInicialY = AltoCinta/2;

            Log.d("ponerCinta","Ubico la cinta en la pantalla - X: "+PosicionInicialX+ " - Y: "+PosicionInicialY);
            Cinta.setPosition(PosicionInicialX,PosicionInicialY);

            super.addChild(Cinta);
        }
        private void ponerTubo()
        {
            Log.d("ponerTubo","Comienza el metodo");

            Log.d("ponerTubo","Instancio el sprite del tubo");
            tubo = Sprite.sprite("tubo.png");

            Log.d("ponerTubo","Obtengo las dimensiones de la imagen");
            float altoTubo, anchoTubo;
            altoTubo = tubo.getHeight();
            anchoTubo = tubo.getWidth();

            Log.d("ponerTubo","Declaro e inicializo las variables para la posicion inicial");
            CCPoint PosicionInicialTubo = new CCPoint();
            PosicionInicialTubo.x = AnchoPantalla-anchoTubo/2 - 100;
            PosicionInicialTubo.y = altoTubo/2;

            Log.d("ponerTubo","Ubico el tubo en la posicion - X: "+ PosicionInicialTubo.x+ " - Y: "+PosicionInicialTubo.y);
            tubo.setPosition(PosicionInicialTubo.x,PosicionInicialTubo.y);

            Log.d("ponerTubo","Agrego el tuvo a la capa");
            super.addChild(tubo);
        }

    }

    class capaFondo extends Layer
    {
        public capaFondo()
        {
            Log.d("Capa fondo", "Inicio el constructor de capaFondo");

            Log.d("Capa fondo", "Llamo al metodo ponerImagenFondo, para establecer una imagen de fondo");
            ponerImagenFondo();
        }

        private void ponerImagenFondo()
        {
            Log.d("PonerImagenFondo","Comienza el metodo");

            Log.d("PonerImagenFondo","Instancio el Sprite");
            ImagenFondo = Sprite.sprite("fondoblanco.png");

            Log.d("PonerImagenFondo","Ubico la imagen de fondo en el centro de la pantalla");
            ImagenFondo.setPosition(pantallaDelDispositivo.width/2,pantallaDelDispositivo.height/2);

            Log.d("PonerImagenFondo","Obtengo el tamaño de la imagen de fondo");
            float AlturaFondo, AnchoFondo;

            AnchoFondo = ImagenFondo.getWidth();
            AlturaFondo = ImagenFondo.getHeight();

            float scaleAltura, scaleAncho;
            scaleAltura = AltoPantalla/AlturaFondo;
            scaleAncho = AnchoPantalla/AnchoFondo;

            Log.d("ponerImagenFondo","Pantalla del dispositivo - Alto: "+ AltoPantalla+ " - Ancho: "+AnchoPantalla);
            Log.d("PonerImagenFondo","Escalar - altura: "+scaleAltura + " - ancho: "+ scaleAncho);

            Log.d("PonerImagenFondo","Seteo el tamaño del fondo");
            ImagenFondo.runAction(ScaleBy.action(0.01f,scaleAncho,scaleAltura));

            Log.d("PonerImagenFondo","Agrego la imagen de fondo a la capa");
            super.addChild(ImagenFondo);
        }
    }
}
