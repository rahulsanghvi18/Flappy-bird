package com.example.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import javax.xml.soap.Text;

import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;
	Texture[] birds;
	Texture topTube,bottomTube;
	Circle  birdCirle;
	int scoringTube = 0;
	int score = 0;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	float gravity = 2;
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubevelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	BitmapFont bitmapFont;
	Texture gameOver;
	@Override
	public void create () {
		gameOver = new Texture("game-over.jpg");
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()*0.75f;
		shapeRenderer = new ShapeRenderer();
		birdCirle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		bitmapFont = new BitmapFont();
		bitmapFont.setColor(Color.WHITE);
		bitmapFont.getData().setScale(10);
		startGame();
	}

	public  void  startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;
		for(int i = 0 ; i < numberOfTubes ; i++){
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


		if(gameState == 1){
			if(Gdx.input.justTouched()){
				velocity = -30;
			}

			for(int i = 0 ; i < numberOfTubes ; i++) {
				if(tubeX[i] < -topTube.getWidth()){
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}else{
					if(tubeX[scoringTube] < Gdx.graphics.getWidth()){
						score++;
						Gdx.app.log("score: ",Integer.toString(score));
						if(scoringTube < numberOfTubes - 1) scoringTube++;
						else scoringTube = 0;
					}
				}
				tubeX[i] = tubeX[i] - tubevelocity;
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(),bottomTube.getHeight());
			}

			if(birdY > 0){
				velocity += gravity;
				birdY -= velocity;
			}else{
				gameState = 2;
			}

			if(flapState == 0)  flapState = 1;
			else  flapState = 0;
		}else if(gameState == 0){
			if(Gdx.input.justTouched()) gameState = 1;
		}else if(gameState == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight());
			if(Gdx.input.justTouched()){
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2,birdY);
		bitmapFont.draw(batch,String.valueOf(score),100,200);
		batch.end();

		birdCirle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);

		for(int i = 0 ; i < numberOfTubes ; i++){
			if(Intersector.overlaps(birdCirle,topTubeRectangles[i]) || Intersector.overlaps(birdCirle,bottomTubeRectangles[i])){
				gameState = 2;
			}
		}
		shapeRenderer.end();
	}
}
