package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Raindrops extends ApplicationAdapter {
    // Größe der Welt
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int BUCKET_SPEED = 200;
    private final int RAINDROP_SPEED = 200;
    private final int GAMESPEED = 1000;

    // Assets
    private SpriteBatch batch;
    private Texture bucketImage;
    private Texture dropImage;
    private Music rainMusic;
    private Sound dropSound;

    //Camera
    private OrthographicCamera camera;

    // Spielfiguren
    private Rectangle bucket;
    private Array<Rectangle> raindrops;

    // Zeitstempel des letzten Regentropfens
    private long lastDropTime;

    @Override
    public void create () {
        raindrops = new Array<>();
        batch = new SpriteBatch();

        // Lade Assets
        bucketImage = new Texture("bucket.png");
        dropImage = new Texture("raindrop.png");
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

        // Musik abspielen + loopen
        rainMusic.setVolume(0.1f);
        rainMusic.setLooping(true);
        rainMusic.play();

        // Kamera erstellen
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        // Kübel positionieren
        bucket = new Rectangle();
        bucket.width = 64;
        bucket.height = 64;
        bucket.x = WIDTH / 2 - bucket.width / 2;
        bucket.y = 20;

        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.width = 64;
        raindrop.height = 64;
        raindrop.x = MathUtils.random(0, WIDTH-raindrop.width);
        raindrop.y = HEIGHT - raindrop.height;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.millis();
    }

    @Override
    public void render () {
        // Bildschirm löschen
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Maus bzw. Touchabfragen
        if (Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) (touchPos.x - bucket.getWidth()/2);
        }

        // Tastenabfragen
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucket.x -= BUCKET_SPEED * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            bucket.x += BUCKET_SPEED * Gdx.graphics.getDeltaTime();

        // Spielfigur darf den Bildschirm nicht verlassen
        if (bucket.x < 0) bucket.x = WIDTH-bucket.width;
        if (bucket.x > WIDTH-bucket.width) bucket.x = 0;

        // Regentropfen erzeugen
        if (TimeUtils.millis() - lastDropTime > GAMESPEED){
            spawnRaindrop();
        }

        // regnen lassen
        Iterator<Rectangle> iter = raindrops.iterator();
        while(iter.hasNext()){
            Rectangle raindrop = iter.next();
            raindrop.y -= RAINDROP_SPEED * Gdx.graphics.getDeltaTime();

            // Regentropfen unterhalb Bildschirmkante?
            if (raindrop.y + raindrop.getHeight() < 0)
                iter.remove();

            // Regentropfen aufgefangen?
            if (bucket.overlaps(raindrop)){
                iter.remove();
                dropSound.play();
            }
        }

        // Zeichnen
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Kübel rendern
        batch.draw(bucketImage, bucket.x, bucket.y);
        // Regentropfen rendern

        for (Rectangle raindrop : raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        dropImage.dispose();
        bucketImage.dispose();
        batch.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}