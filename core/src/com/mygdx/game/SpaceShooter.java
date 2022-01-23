package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class SpaceShooter extends ApplicationAdapter {
    // Größe der Welt
    private final int WIDTH = 854;
    private final int HEIGHT = 480;
    private final int DEFENSESHIP_SPEED = 400;
    private final int ENEMYSHIP_SPEED = 200;
    private final int LASERSHOT_SPEED = 500;


    private final int GAMESPEED = 1000;

    // Assets
    private SpriteBatch batch;
    private Texture defenseShipImage;
    private Texture enemyShipImage;
    private Texture laserImage;

    //private Music rainMusic;
    //private Sound dropSound;
    Texture background1, background2;
    float yMax, yCoordBg1, yCoordBg2;
    final int BACKGROUND_MOVE_SPEED = 100; // pixels per second. Put your value here.

    //Camera
    private OrthographicCamera camera;

    // Spielfiguren
    private Rectangle defenseship;
    private Array<Rectangle> enemyShips;
    private Array<Rectangle> laserShots;


    // Zeitstempel des letzten Regentropfens
    private long lastDropTime;

    @Override
    public void create() {
        enemyShips = new Array<>();
        laserShots = new Array<>();
        batch = new SpriteBatch();

        // Lade Assets
        defenseShipImage = new Texture("DefenseShip64x64.png");
        enemyShipImage = new Texture("EnemyShip64x64.png");
        laserImage = new Texture("laser32x32.png");
        background1 = new Texture(Gdx.files.internal("background.png"));
        background2 = new Texture(Gdx.files.internal("background.png")); // identical
        yMax = 854;
        yCoordBg1 = yMax*(-1); yCoordBg2 = 0;
        /*rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

        // Musik abspielen + loopen
        rainMusic.setVolume(0.1f);
        rainMusic.setLooping(true);
        rainMusic.play();

         */

        // Kamera erstellen
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        // Kübel positionieren
        defenseship = new Rectangle();
        defenseship.width = 64;
        defenseship.height = 64;
        defenseship.x = 20;
        defenseship.y = HEIGHT / 2 - defenseship.height / 2;

        spawnEnemyShip();
    }

    private void spawnEnemyShip() {
        Rectangle enemyShip = new Rectangle();
        enemyShip.width = 64;
        enemyShip.height = 64;
        enemyShip.x = WIDTH;
        //enemyShip.y = HEIGHT - enemyShip.height;
        enemyShip.y = MathUtils.random(0, HEIGHT - enemyShip.height);

        enemyShips.add(enemyShip);
        lastDropTime = TimeUtils.millis();
    }

    private void spawnLaser() {
        Rectangle lasershot = new Rectangle();
        lasershot.width = 32;
        lasershot.height = 32;
        lasershot.x = defenseship.x + 48;
        lasershot.y = defenseship.y + 16;

        laserShots.add(lasershot);

    }

    @Override
    public void render() {
        // Bildschirm löschen
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // Maus bzw. Touchabfragen
        /*if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) (touchPos.x - bucket.getWidth() / 2);
        }

         */

        // Tastenabfragen
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            defenseship.x -= DEFENSESHIP_SPEED / 2 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            defenseship.x += DEFENSESHIP_SPEED / 2 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            defenseship.y += DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            defenseship.y -= DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) spawnLaser();



        // Spielfigur darf den Bildschirm nicht verlassen

        if (defenseship.x < 0)  defenseship.x = 0;
        if (defenseship.x > WIDTH - defenseship.width) defenseship.x = (WIDTH - defenseship.width);
        if (defenseship.y < 0) defenseship.y = 0;
        if (defenseship.y >HEIGHT - defenseship.height) defenseship.y = (HEIGHT - defenseship.height);

        // Regentropfen erzeugen
        if (TimeUtils.millis() - lastDropTime > GAMESPEED) spawnEnemyShip();


        // enemy fliegen lassen lassen
        Iterator<Rectangle> iterEnemyShip = enemyShips.iterator();
        while (iterEnemyShip.hasNext()) {
            Rectangle enemyShip = iterEnemyShip.next();
            enemyShip.x -= ENEMYSHIP_SPEED * Gdx.graphics.getDeltaTime();

            // Regentropfen unterhalb Bildschirmkante?
            if (enemyShip.y + enemyShip.getHeight() < 0)
                iterEnemyShip.remove();

            // Regentropfen aufgefangen?
            if (defenseship.overlaps(enemyShip)) {

                //renderGameOverScreen();
                //dropSound.play();
            }
        }

        //laser fliegen lassen
        Iterator<Rectangle> iterLaser = laserShots.iterator();

        while (iterLaser.hasNext()) {

            Rectangle lasershot = iterLaser.next();
            lasershot.x += LASERSHOT_SPEED * Gdx.graphics.getDeltaTime();

            // Regentropfen unterhalb Bildschirmkante?
            if (lasershot.x > WIDTH)
                iterLaser.remove();

            // Regentropfen aufgefangen?
            Iterator<Rectangle> iterEnemyShip2 = enemyShips.iterator();
            while (iterEnemyShip2.hasNext()) {
                Rectangle enemyShip = iterEnemyShip2.next();
                if (lasershot.overlaps(enemyShip)) {
                    iterLaser.remove();
                    iterEnemyShip2.remove();
                    //dropSound.play();
                }
            }

        }


        yCoordBg1 -= BACKGROUND_MOVE_SPEED * Gdx.graphics.getDeltaTime();
        yCoordBg2 = yCoordBg1 + yMax;  // We move the background, not the camera
        if (yCoordBg1 >= 0) {
            yCoordBg1 = yMax*(-1); yCoordBg2 = 0;
        }
        // Zeichnen
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(background1, yCoordBg1, 0);
        batch.draw(background2, yCoordBg2, 0);
        // Kübel rendern
        batch.draw(defenseShipImage, defenseship.x, defenseship.y);
        // Regentropfen rendern


        if (!laserShots.isEmpty()) {
            for (Rectangle lasershot : laserShots) {
                batch.draw(laserImage, lasershot.x, lasershot.y);
            }
        }


        for (Rectangle enemyShip : enemyShips) {
            batch.draw(enemyShipImage, enemyShip.x, enemyShip.y);
        }
        batch.end();
        System.out.println(laserShots.size);
    }

   /* private void renderGameOverScreen(){
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background1,0,0);
        batch.end();
    }

    */

    @Override
    public void dispose() {
        super.dispose();
        enemyShipImage.dispose();
        defenseShipImage.dispose();
        laserImage.dispose();
        batch.dispose();
        //dropSound.dispose();
        //rainMusic.dispose();
    }
}