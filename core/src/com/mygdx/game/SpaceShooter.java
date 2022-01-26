package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;
import java.util.Iterator;

public class SpaceShooter extends ApplicationAdapter {
    // Größe der Welt

    private final int WIDTH = 854;
    private final int HEIGHT = 480;
    private int DEFENSESHIP_SPEED = 400; //nicht final weil variabel
    private int ENEMYSHIP_SPEED = 300;
    private final int LASERSHOT_SPEED = 500;
    private int ENEMYLASERSHOT_SPEED = 500;



    private final int GAMESPEED = 1000;

    // Assets
    private SpriteBatch batch;
    private Texture defenseShipImage;
    private Texture enemyShipImage;
    private Texture backgroundPlanetsImage; //Planeten im Hintergrund
    //private Array<Texture> asteroidImages;
    private Texture asteroidImage;
    private Texture laserImage;
    private Texture enemyLaserImage;
    private Texture gameOverScreen;
    private BitmapFont font;
    private int score = 0;
    private int collisionCounter = 0;
    private Texture heartImage;

    private Music backgroundMusic;
    private Sound crashSound;
    Texture background1, background2;
    float yMax, yCoordBg1, yCoordBg2;
    int BACKGROUND_MOVE_SPEED = 75; // pixels per second. Put your value here.

    //Camera
    private OrthographicCamera camera;

    // Spielfiguren
    private Rectangle defenseship;
    private Array<Rectangle> asteroids;
    private Array<Rectangle> enemyShips;
    private Array<Rectangle> laserShots;
    private Array<Rectangle> enemyLaserShots;
    private Array<Texture> hearts;
    //private Array<Texture> planets; Derzeit noch inkompatibel


    // Zeitstempel des letzten Regentropfens
    private long lastDropTime;

    private long lastAsteroidTime;
    private boolean asteroidflag = true; //für dynamische Hinzufügung der Asteroiden, d.h. sobald alle asteroidimages importiert ist flag flase

    @Override
    public void create() {
        enemyShips = new Array<>();

        asteroids = new Array<>();
        //asteroidImages = new Array<>();
        laserShots = new Array<>();
        enemyLaserShots = new Array<>();
        hearts = new Array<>(3);
        hearts.add(heartImage);
        hearts.add(heartImage);
        hearts.add(heartImage);
        batch = new SpriteBatch();


        //Lade Assets in Array
        /*for (int i = 1; asteroidflag; i++) {
            asteroidImages.add(new Texture("asteroid"+i+".png"));

            if (new File("asteroid"+(i+1)+".png").exists()){
                asteroidflag=false;
            }
        }

         */

        /*asteroidImages.add(new Texture("asteroid1.png"));
        asteroidImages.add(new Texture("asteroid2.png"));
        asteroidImages.add(new Texture("asteroid3.png"));
        asteroidImages.add(new Texture("asteroid4.png"));
        asteroidImages.add(new Texture("asteroid5.png"));

         */



        // Lade Assets
        defenseShipImage = new Texture("DefenseShip64x64-Dark.png");
        enemyShipImage = new Texture("EnemyShip64x64.png");
        asteroidImage = new Texture("asteroid1.png");
        laserImage = new Texture("laser48x11.png");
        enemyLaserImage = new Texture("laserenemy48x11.png");
        heartImage = new Texture("heart.png");
        font = new BitmapFont(Gdx.files.internal("ssfont2.fnt"));
        background1 = new Texture(Gdx.files.internal("background.png"));
        background2 = new Texture(Gdx.files.internal("background.png")); // identical
        gameOverScreen = new Texture("GameOverScreen.png"); // identical
        backgroundPlanetsImage = new Texture("planet1.png");

        //Background props
        yMax = 854;
        yCoordBg1 = yMax * (1);
        yCoordBg2 = 0;

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("LHS_RLD10.mp3"));
        crashSound = Gdx.audio.newSound(Gdx.files.internal("EnemyHit.mp3"));

        // Musik abspielen + loopen
        backgroundMusic.setVolume(0.1f);
        backgroundMusic.setLooping(true);
        backgroundMusic.play();




        // Kamera erstellen
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        // DefenseShip positionieren
        defenseship = new Rectangle();
        defenseship.width = 64;
        defenseship.height = 64;
        defenseship.x = 20;
        defenseship.y = HEIGHT / 2 - defenseship.height / 2;

        spawnAsteroids();

    }

    private Rectangle spawnEnemyShip() {
        Rectangle enemyShip = new Rectangle();
        enemyShip.width = 64;
        enemyShip.height = 64;
        enemyShip.x = WIDTH;
        //enemyShip.y = HEIGHT - enemyShip.height;
        enemyShip.y = MathUtils.random(0, HEIGHT - enemyShip.height);

        enemyShips.add(enemyShip);
        lastDropTime = TimeUtils.millis();
        return enemyShip;
    }

    /*private void showhearts(){
        for (Texture heart : hearts){
            Rectangle heart = new Rectangle();
        }
    }

     */

    private void spawnLaser() {
        Rectangle laserShot = new Rectangle();
        laserShot.width = 48;
        laserShot.height = 11;
        laserShot.x = defenseship.x + defenseship.width;
        laserShot.y = defenseship.y + defenseship.height / 2 - laserShot.height / 2;

        laserShots.add(laserShot);

    }

    private void spawnAsteroids() {
        Rectangle asteroid = new Rectangle();
        asteroid.width = 48;
        asteroid.height = 48;
        asteroid.x = WIDTH;
        asteroid.y = MathUtils.random(0, HEIGHT - asteroid.height);

        asteroids.add(asteroid);
        lastAsteroidTime = TimeUtils.millis();

    }

    private void spawnEnemyLaser(Rectangle enemyship) {
        Rectangle enemyLaserShot = new Rectangle();
        enemyLaserShot.width = 48;
        enemyLaserShot.height = 11;
        enemyLaserShot.x = enemyship.x - enemyship.width;
        enemyLaserShot.y = enemyship.y + enemyship.height / 2 - enemyLaserShot.height / 2;

        enemyLaserShots.add(enemyLaserShot);
    }

    @Override
    public void render() {
        if (collisionCounter < 3) {
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
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                DEFENSESHIP_SPEED = 600;
            else DEFENSESHIP_SPEED = 400;

            if (Gdx.input.isKeyPressed(Input.Keys.A))
                defenseship.x -= DEFENSESHIP_SPEED/1.5 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                defenseship.x += DEFENSESHIP_SPEED/1.5 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.W))
                defenseship.y += DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.S))
                defenseship.y -= DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) spawnLaser();

            /*if (Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                defenseship.x -= DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.D) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                defenseship.x += DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.W) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                defenseship.y += DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.S) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                defenseship.y -= DEFENSESHIP_SPEED * Gdx.graphics.getDeltaTime();

             */




            // Spielfigur darf den Bildschirm nicht verlassen

            if (defenseship.x < 0) defenseship.x = 0;
            if (defenseship.x > WIDTH - defenseship.width) defenseship.x = (WIDTH - defenseship.width);
            if (defenseship.y < 0) defenseship.y = 0;
            if (defenseship.y > HEIGHT - defenseship.height) defenseship.y = (HEIGHT - defenseship.height);

            // Regentropfen erzeugen
            if (TimeUtils.millis() - lastDropTime > GAMESPEED) //spawnEnemyShip();
                spawnEnemyLaser(spawnEnemyShip());

            if (TimeUtils.millis() - lastAsteroidTime - 1000 > GAMESPEED) //spawnEnemyShip();
                spawnAsteroids();

            // enemy fliegen lassen lassen
            Iterator<Rectangle> iterEnemyShip = enemyShips.iterator();
            while (iterEnemyShip.hasNext()) {
                Rectangle enemyShip = iterEnemyShip.next();
                enemyShip.x -= ENEMYSHIP_SPEED * Gdx.graphics.getDeltaTime();

                // EnemyShip außerhalb Bildschirmbereich
                if (enemyShip.x < 0 - enemyShip.width)
                    iterEnemyShip.remove();

                // Kollision Defense- und EnemyShip
                if (defenseship.overlaps(enemyShip)) {
                    collisionCounter++;
                    iterEnemyShip.remove();
                    //renderGameOverScreen();
                    //dropSound.play();
                }
            }


            // asteroid fliegen lassen lassen
            Iterator<Rectangle> iterAsteroid = asteroids.iterator();
            while (iterAsteroid.hasNext()) {
                Rectangle asteroid = iterAsteroid.next();
                asteroid.x -= (ENEMYSHIP_SPEED - 100) * Gdx.graphics.getDeltaTime();
                //asteroid.y -= ( MathUtils.random(-500,500)) * Gdx.graphics.getDeltaTime();


                // EnemyShip außerhalb Bildschirmbereich
                if (asteroid.x < 0 - asteroid.width)
                    iterAsteroid.remove();

                // Kollision Defense- und EnemyShip
                if (defenseship.overlaps(asteroid)) {
                    collisionCounter++;
                    iterAsteroid.remove();
                    //renderGameOverScreen();
                    //dropSound.play();
                }
            }

            //enemylaser fliegen lassen
            Iterator<Rectangle> iterEnemyLaser = enemyLaserShots.iterator();

            while (iterEnemyLaser.hasNext()) {

                Rectangle enemylasershot = iterEnemyLaser.next();
                enemylasershot.x -= ENEMYLASERSHOT_SPEED * Gdx.graphics.getDeltaTime();

                // EnemyLaser außerhalb Bildschirmbereich
                if (enemylasershot.x < 0)
                    iterEnemyLaser.remove();

                // Kollision EnemyLasershot und DefenseShip
                if (enemylasershot.overlaps(defenseship)) {
                    collisionCounter = 3;
                    //dropSound.play();
                }
            }


            //defenselaser fliegen lassen
            Iterator<Rectangle> iterLaser = laserShots.iterator();

            while (iterLaser.hasNext()) {

                Rectangle lasershot = iterLaser.next();
                lasershot.x += LASERSHOT_SPEED * Gdx.graphics.getDeltaTime();

                // Laser außerhalb Bildschirmbereich
                if (lasershot.x > WIDTH)
                    iterLaser.remove();

                // Kollision Lasershot und EnemyShip
                Iterator<Rectangle> iterEnemyShip2 = enemyShips.iterator();
                while (iterEnemyShip2.hasNext()) {
                    Rectangle enemyShip = iterEnemyShip2.next();
                    if (lasershot.overlaps(enemyShip)) {
                        iterLaser.remove();
                        iterEnemyShip2.remove();
                        ENEMYSHIP_SPEED = ENEMYSHIP_SPEED + 5;
                        ENEMYLASERSHOT_SPEED = ENEMYLASERSHOT_SPEED + 5;
                        BACKGROUND_MOVE_SPEED = BACKGROUND_MOVE_SPEED +1;
                        score++;
                        crashSound.play(0.1f);
                    }
                }

            }


            //yCoordBg1 -= (DEFENSESHIP_SPEED+600) * Gdx.graphics.getDeltaTime(); //variable speed

            yCoordBg1 -= BACKGROUND_MOVE_SPEED * Gdx.graphics.getDeltaTime(); //constant speed
            yCoordBg2 = yCoordBg1 - yMax;  // We move the background, not the camera
            if (yCoordBg1 <= 0) {
                yCoordBg1 = yMax * (1);
                yCoordBg2 = 0;
            }
            // Zeichnen
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            batch.draw(background1, yCoordBg1, 0);
            batch.draw(background2, yCoordBg2, 0);

            batch.draw(backgroundPlanetsImage, 200, 200);

            font.getData().setScale(1f);
            font.draw(batch, String.valueOf(score), WIDTH - 80, HEIGHT - 20);


            // Kübel rendern
            batch.draw(defenseShipImage, defenseship.x, defenseship.y);

            //Hearts rendern

            //Temporary if statements
            if (!(collisionCounter > 2)) batch.draw(heartImage, WIDTH - 140, HEIGHT - 55);
            if (!(collisionCounter > 1)) batch.draw(heartImage, WIDTH - 180, HEIGHT - 55);
            if (!(collisionCounter > 0)) batch.draw(heartImage, WIDTH - 220, HEIGHT - 55);


            //Asteroiden rendern

            /*for (Rectangle asteroid : asteroids) {

                batch.draw(asteroidImages.get(MathUtils.random(0, asteroids.size)), asteroid.x, asteroid.y,asteroid.);
            }
             */
            for (Rectangle asteroid : asteroids) {

                batch.draw(asteroidImage, asteroid.x, asteroid.y);
            }


            // Regentropfen rendern


            if (!laserShots.isEmpty()) {
                for (Rectangle lasershot : laserShots) {
                    batch.draw(laserImage, lasershot.x, lasershot.y);
                }
            }


            for (Rectangle enemyShip : enemyShips) {
                batch.draw(enemyShipImage, enemyShip.x, enemyShip.y);
            }

            for (Rectangle enemylasershot : enemyLaserShots) {
                batch.draw(enemyLaserImage, enemylasershot.x, enemylasershot.y);
            }

        } else { //falls DefenseShip zerstört
            enemyShips.clear();
            laserShots.clear();
            enemyLaserShots.clear();
            ENEMYSHIP_SPEED = 400;
            ENEMYLASERSHOT_SPEED = 500;
            BACKGROUND_MOVE_SPEED = 75;
            score = 0;
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.draw(gameOverScreen, 0, 0);
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                collisionCounter = 0;
            }
        }
        batch.end();


        System.out.println(collisionCounter);
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