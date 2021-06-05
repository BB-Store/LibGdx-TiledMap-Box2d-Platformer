package de.gamerrik.tiledmaptest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MainActivity extends ApplicationAdapter {

	private SpriteBatch batch;
	private Player player;
	private Texture background;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private float unitScale = 1/16f;
	private OrthographicCamera camera;

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private Matrix4 debugMatrix;

	@Override
	public void create () {
		//Init Values
		background = new Texture(Gdx.files.internal("mountain.png"));
		batch = new SpriteBatch();

		map = new TmxMapLoader().load("Box2dMap.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, unitScale);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 60, 20);
		camera.update();

		world = new World(new Vector2(0, -2), true);
		player = new Player(new Vector2(1, 7), "skelett_sprite.png", world);

		//Init DebugRenderer
		debugMatrix = new Matrix4(camera.combined);
		debugRenderer = new Box2DDebugRenderer();

		//Build Box2d Objects
		buildShapes(map);
	}

	private void buildShapes(TiledMap map) {
		MapObjects objects = map.getLayers().get("Ground").getObjects();
		MapProperties prop = map.getProperties();

		for(MapObject object : objects){
			RectangleMapObject tmpObject = (RectangleMapObject)object;
			Rectangle rectangle = tmpObject.getRectangle();
			PolygonShape shape = new PolygonShape();
			float objectWidth = rectangle.x/16 + rectangle.width/16 * 0.5f;
			float objectHeight = rectangle.y/16 + rectangle.height/16*0.5f;
			Vector2 size = new Vector2(objectWidth, objectHeight);
			float hx  = rectangle.width/16 * 0.5f;
			float hy = rectangle.height/16 * 0.5f;
			shape.setAsBox(hx, hy, size, 0.0f);

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			Body body = world.createBody(bodyDef);
			body.createFixture(shape, 1);

			shape.dispose();
		}
	}


	@Override
	public void render () {
		handleInput();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(background, 0,0, camera.viewportWidth, camera.viewportHeight);
		batch.setProjectionMatrix(camera.combined);
		camera.update();

		//Render Map
		renderer.setView(camera);
		renderer.render();

		world.step(1/60f, 6, 2);

		player.render(batch);

		//debugRenderer
		debugRenderer.render(world, debugMatrix);

		batch.end();
	}

	private void handleInput() {
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			player.handleInput(1, Gdx.graphics.getDeltaTime());
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.D)){
			player.handleInput(2, Gdx.graphics.getDeltaTime());
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.W)){
			player.handleInput(3, Gdx.graphics.getDeltaTime());
		}
	}


	@Override
	public void dispose () {
		map.dispose();
		renderer.dispose();
		background.dispose();
		debugRenderer.dispose();
		world.dispose();
	}


}
