/****************************************/
/* Renderer.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import eu.cherrytree.gdx.particles.ParticleEmitter;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import javax.swing.JOptionPane;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class Renderer implements ApplicationListener, InputProcessor
{
	//--------------------------------------------------------------------------

	private float maxActiveTimer;
	private int maxActive, lastMaxActive;
	private int activeCount;
	private BitmapFont font;
	private SpriteBatch spriteBatch;
	
	private OrthographicCamera worldCamera;
	private OrthographicCamera textCamera;
	
	private ParticleEditor editor;
	
	//--------------------------------------------------------------------------

	public Renderer(ParticleEditor editor)
	{
		this.editor = editor;
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	public void create()
	{
		if (spriteBatch != null)
			return;

		spriteBatch = new SpriteBatch();

		worldCamera = new OrthographicCamera();
		textCamera = new OrthographicCamera();

		font = new BitmapFont(Gdx.files.getFileHandle("eu/cherrytree/gdx/particles/editor/res/default.fnt", Files.FileType.Internal),
		Gdx.files.getFileHandle("eu/cherrytree/gdx/particles/editor/res/default.png", Files.FileType.Internal), true);

		editor.createNewEmitter();

		Gdx.input.setInputProcessor(this);
	}
	
	//--------------------------------------------------------------------------

	public OrthographicCamera getWorldCamera()
	{
		return worldCamera;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void resize(int width, int height)
	{
		Gdx.gl.glViewport(0, 0, width, height);

		worldCamera.setToOrtho(false, width, height);
		worldCamera.update();

		textCamera.setToOrtho(true, width, height);
		textCamera.update();

		editor.getEffect().setPosition(worldCamera.viewportWidth / 2, worldCamera.viewportHeight / 2);
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void render()
	{
		int viewWidth = Gdx.graphics.getWidth();
		int viewHeight = Gdx.graphics.getHeight();

		float delta = Math.max(0, Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		worldCamera.setToOrtho(false, viewWidth, viewHeight);
		worldCamera.zoom = 1.0f;
		worldCamera.update();
		editor.getEffect().setPosition(worldCamera.viewportWidth / 2, worldCamera.viewportHeight / 2);

		spriteBatch.setProjectionMatrix(worldCamera.combined);

		spriteBatch.begin();
		spriteBatch.enableBlending();
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		activeCount = 0;
		boolean complete = true;
		for (ParticleEmitter emitter : editor.getEffect().getEmitters())
		{
			if (emitter.getSprite() == null && emitter.getImagePath() != null)
				loadImage(emitter);
			boolean enabled = editor.isEnabled(emitter);
			if (enabled)
			{
				if (emitter.getSprite() != null)
					emitter.draw(spriteBatch, delta);
				activeCount += emitter.getActiveCount();
				if (!emitter.isComplete())
					complete = false;
			}
		}
		if (complete)
			editor.getEffect().start();

		maxActive = Math.max(maxActive, activeCount);
		maxActiveTimer += delta;
		if (maxActiveTimer > 3)
		{
			maxActiveTimer = 0;
			lastMaxActive = maxActive;
			maxActive = 0;
		}

		spriteBatch.setProjectionMatrix(textCamera.combined);

		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, 15);
		font.draw(spriteBatch, "Count: " + activeCount, 5, 35);
		font.draw(spriteBatch, "Max: " + lastMaxActive, 5, 55);
		font.draw(spriteBatch, (int) (editor.getEmitter().getPercentComplete() * 100) + "%", 5, 75);

		spriteBatch.end();
	}
	
	//--------------------------------------------------------------------------

	private void loadImage(ParticleEmitter emitter)
	{
		final String imagePath = emitter.getImagePath();
		String imageName = new File(imagePath.replace('\\', '/')).getName();
		try
		{
			FileHandle file;
			if ((imagePath.contains("/") || imagePath.contains("\\")) && !imageName.contains(".."))
			{
				file = Gdx.files.absolute(imagePath);
				if (!file.exists())
				{
					// try to use image in editor.getEffect() directory
					file = Gdx.files.absolute(new File(editor.getEffectFile().getParentFile(), imageName).getAbsolutePath());
				}
			}
			else
			{
				file = Gdx.files.absolute(new File(editor.getEffectFile().getParentFile(), imagePath).getAbsolutePath());
			}
			emitter.setSprite(new Sprite(new Texture(file)));
			if (editor.getEffectFile() != null)
			{
				URI relativeUri = editor.getEffectFile().getParentFile().toURI().relativize(file.file().toURI());
				emitter.setImagePath(relativeUri.getPath());
			}
		}
		catch (GdxRuntimeException ex)
		{
			ex.printStackTrace();
			EventQueue.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					JOptionPane.showMessageDialog(editor, "Error loading image:\n" + imagePath);
				}
			});
			emitter.setImagePath(null);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean touchDown(int x, int y, int pointer, int newParam)
	{
		Vector3 touchPoint = new Vector3(x, y, 0);
		worldCamera.unproject(touchPoint);
		editor.getEffect().setPosition(touchPoint.x, touchPoint.y);
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean touchUp(int x, int y, int pointer, int button)
	{
		editor.dispatchEvent(new WindowEvent(editor, WindowEvent.WINDOW_LOST_FOCUS));
		editor.dispatchEvent(new WindowEvent(editor, WindowEvent.WINDOW_GAINED_FOCUS));
		editor.requestFocusInWindow();
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean touchDragged(int x, int y, int pointer)
	{
		Vector3 touchPoint = new Vector3(x, y, 0);
		worldCamera.unproject(touchPoint);
		editor.getEffect().setPosition(touchPoint.x, touchPoint.y);
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void dispose()
	{
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void pause()
	{
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void resume()
	{
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean mouseMoved(int x, int y)
	{
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}
	
	//--------------------------------------------------------------------------
}